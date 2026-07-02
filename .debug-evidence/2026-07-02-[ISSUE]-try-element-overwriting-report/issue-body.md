### Description
When **Debug Report** is globally enabled in settings, executing a temporary test run in the scenario editor (e.g., via the "Try Event", "Try Condition", or "Try Action" buttons) completely overwrites and deletes the existing debug report generated from full scenario runs.

### Steps to Reproduce
1. Go to settings and enable **Debug Report**.
2. Run a full scenario session to generate a valid debug report.
3. Stop the detection session and verify that the "Show Debug Report" option is populated and visible in the menu.
4. Go to the scenario editor and select any Event, Condition, or Action.
5. Tap **Try** (test run) to execute a single-frame preview of that element.
6. Return to the main screen / More options menu and click "Show Debug Report" or observe the debug report state.

### Expected Behavior
A temporary test/preview run inside the editor (with `generateReport = false` and `liveDebugging = true`) should not delete or overwrite the debug report from full scenario sessions.

### Actual Behavior
The previous debug report is deleted and replaced with a temporary single-frame try session report. If the try session ends abruptly, the files might be empty/invalid, causing the debug report dialog to immediately auto-dismiss upon opening.

### Root Cause
In `DetectorEngine.startDetection`, the editor try session passes `generateReport = false` and `liveDebugging = true`. Because `liveDebugging` is true, it attaches the `debuggingListener` (`DebugEngine`).
When `DebugEngine.onSessionStarted` is called, it does not receive the `generateReport` parameter. Instead, it checks the global setting `debugConfigurationLocalDataSource.isDebugReportEnabled()`. Because this is true globally, it initiates a report writing session by calling `debugReportLocalDataSource.startReportWrite()`, which immediately recreates/wipes out the existing report files (`messagesFile.safeRecreate()` and `overviewFile.safeRecreate()`).

### Suggested Fix
Pass the `generateReport` parameter to the `SmartProcessingListener.onSessionStarted(...)` method so that `DebugEngine` can respect the individual session's report configuration instead of always falling back to the global setting:
1. Update `SmartProcessingListener.onSessionStarted` signature to include `generateReport: Boolean`.
2. Update the calls in `DetectorEngine.kt` to pass the `generateReport` parameter.
3. Update `DebugEngine.kt` to set `isReportEnabled = generateReport` instead of reading the global settings directly.
