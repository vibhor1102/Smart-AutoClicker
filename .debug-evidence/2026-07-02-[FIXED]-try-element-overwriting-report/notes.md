# Bug Notes: Try Element Overwriting Debug Report

## Environment
* **App Package:** `com.buzbuz.smartautoclicker.debug` / `com.buzbuz.smartautoclicker.patched.debug`
* **Branch/Build:** `wip/upstream-bug-triage`
* **Device:** Android 14 device

## Reproduction Steps
1. Enable the **Debug Report** option globally in the application settings (under More settings).
2. Start and run a full smart scenario detection session to generate a valid debug report.
3. Stop the scenario detection session. Observe that the "Show Debug Report" option is available in the UI and displays correct statistics/timeline.
4. Open the scenario editor for any scenario.
5. Select any Event, Condition, or Action, and tap the **Try** (test) button. This executes a temporary, single-frame test run.
6. Return to the main screen / More options menu and observe the "Show Debug Report" option.

## Observed vs Expected Behavior
* **Observed:** The previous full-run debug report has been deleted. If the try session ended immediately or didn't write overview data, the report is either empty/corrupt (triggering the dialog dismiss bug) or replaced with a single-frame dummy report from the "Try" action.
* **Expected:** The "Try" action in the editor is a temporary test run and should not overwrite or delete the debug report generated from full scenario executions.

## Evidence Files and Paths
* Triggering of try run: [SmartProcessingRepositoryImpl.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/processing/src/main/java/com/buzbuz/smartautoclicker/core/processing/domain/SmartProcessingRepositoryImpl.kt#L195-L235)
* Listener dispatching: [DetectorEngine.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/processing/src/main/java/com/buzbuz/smartautoclicker/core/processing/data/DetectorEngine.kt#L237-L243)
* Global preference checking and overwriting: [DebugEngine.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/debugging/src/main/java/com/buzbuz/smartautoclicker/core/smart/debugging/engine/DebugEngine.kt#L91-L107)

## Root Cause
In `DetectorEngine.startDetection`, the editor try session passes `generateReport = false` and `liveDebugging = true`. Since `liveDebugging` is true, it attaches the `debuggingListener` (`DebugEngine`).
When `DebugEngine.onSessionStarted` is called, it does not receive the `generateReport` flag (which is missing from the `SmartProcessingListener` interface method signature). Instead, it queries the global setting:
```kotlin
isReportEnabled = debugConfigurationLocalDataSource.isDebugReportEnabled()
```
Because the setting is globally true, it triggers `debugReportLocalDataSource.startReportWrite()`, which immediately calls `safeRecreate()` on the report files, clearing all previous report contents.

## Suggested Fix
1. Modify `SmartProcessingListener.onSessionStarted` signature to include `generateReport: Boolean`.
2. Update the calls in `DetectorEngine.kt` to pass the `generateReport` parameter.
3. Update `DebugEngine.kt` to use the passed `generateReport` value to determine `isReportEnabled` instead of reading the global settings directly.
