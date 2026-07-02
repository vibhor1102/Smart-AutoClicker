# Bug Notes: Crash / Abrupt Termination Creates Empty Report and Locks UI

## Environment
* **App Package:** `com.buzbuz.smartautoclicker.debug` / `com.buzbuz.smartautoclicker.patched.debug`
* **Branch/Build:** `wip/upstream-bug-triage`
* **Device:** Android 14 device

## Reproduction Steps
1. Enable the **Debug Report** option globally.
2. Start a scenario run.
3. While the scenario is actively detecting/running, force kill the application process (via ADB shell kill, system task manager, or by inducing a runtime crash).
4. Re-open the application.
5. Go to the "More" settings screen. Observe that the "Show Debug Report" option is visible.
6. Click on the "Show Debug Report" option.

## Observed vs Expected Behavior
* **Observed:** The dialog opens and immediately disappears (auto-dismisses), returning the user to the "More" settings screen. There is no feedback or indication of what happened.
* **Expected:** Either the UI should not show the "Show Debug Report" button (since no valid report exists), or clicking it should display a friendly message indicating that the previous run ended abruptly or crashed, rather than silently opening and instantly closing a dialog.

## Evidence Files and Paths
* Verification of file existence on startup: [DebugReportLocalDataSource.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/debugging/src/main/java/com/buzbuz/smartautoclicker/core/smart/debugging/data/DebugReportLocalDataSource.kt#L65)
* File creation at start of run: [DebugReportLocalDataSource.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/debugging/src/main/java/com/buzbuz/smartautoclicker/core/smart/debugging/data/DebugReportLocalDataSource.kt#L88-L89)
* UI auto-dismiss logic: [DebugReportOverviewContent.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/feature/smart-debugging/src/main/java/com/buzbuz/smartautoclicker/feature/smart/debugging/ui/dialog/report/overview/DebugReportOverviewContent.kt#L77-L79)

## Root Cause
When a session is initiated, `DebugReportLocalDataSource.startReportWrite()` invokes `safeRecreate()` on the overview and messages files, creating them as empty files.
If the application is killed mid-run, `stopReportWrite()` is never called, and the overview file remains empty and invalid.
Upon next launch:
1. `isReportAvailable` is initialized to `overviewFile.safeExists()`, which evaluates to `true` (since the empty file exists).
2. The UI displays the "Show Debug Report" option because `isReportAvailable` is true.
3. When the user clicks the button, `DebugReportOverviewViewModel` tries to load the overview. `readOverview()` returns `null` because parsing the empty file fails.
4. The overview state is marked as `NotAvailable`, causing `DebugReportOverviewContent.toNotAvailableState()` to call `dialogController.back()`, which instantly dismisses the dialog.

## Suggested Fix
1. Ensure `isReportAvailable` is only true if the overview file exists **and has a non-zero size** or can be parsed successfully.
2. Alternatively, catch parsing/loading errors in the view model or dialog and present an "Abrupt termination / report corrupted" error screen instead of immediately closing the dialog.
