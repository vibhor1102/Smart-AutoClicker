# Bug Notes: Cache Storage vs Persistent Files Storage (OS-level Cache Cleanup)

## Environment
* **App Package:** `com.buzbuz.smartautoclicker.debug` / `com.buzbuz.smartautoclicker.patched.debug`
* **Branch/Build:** `wip/upstream-bug-triage`
* **Device:** Android 14 device

## Reproduction Steps
1. Enable the **Debug Report** option globally.
2. Run a full scenario session to create a valid debug report.
3. Stop the session. Verify the debug report is visible and populated.
4. Let the device run other heavy applications or trigger system cache clearing (e.g. low-storage simulation or system optimizer tools).
5. Open the app and observe if the "Show Debug Report" option is still available.

## Observed vs Expected Behavior
* **Observed:** The debug report files have been deleted, and the button to view the report is no longer available in the UI.
* **Expected:** The debug report should persist until the next scenario run or until the user manually deletes/disables it, even if the OS performs a routine cache sweep.

## Evidence Files and Paths
* Path configuration: [DebugReportLocalDataSource.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/debugging/src/main/java/com/buzbuz/smartautoclicker/core/smart/debugging/data/DebugReportLocalDataSource.kt#L57-L58)
```kotlin
private val overviewFile: File = File(context.cacheDir, DEBUG_REPORT_OVERVIEW_FILE_NAME)
private val messagesFile: File = File(context.cacheDir, DEBUG_REPORT_MESSAGES_FILE_NAME)
```

## Root Cause
The debug report files are stored in `context.cacheDir`. In Android, the `cache` directory is volatile and subject to automatic, silent deletion by the operating system whenever the system resources/storage are low. This makes it unreliable for persisting debugging reports that the user intends to review later.

## Suggested Fix
Move the debug report files from `context.cacheDir` to `context.filesDir` (or a dedicated subdirectory under `filesDir`, such as `context.filesDir/debug_reports/`), which is protected from OS-level automatic deletion.
