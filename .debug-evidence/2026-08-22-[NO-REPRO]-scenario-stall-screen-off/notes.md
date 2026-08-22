# Scenario stall around screen-off

## Environment

- Device: Xiaomi M2012K11I (`haydnin`), Android 14 per repository notes
- Package: `com.buzbuz.smartautoclicker.patched`
- Version: `4.0.0-beta08-patched.1` (`versionCode=20102`), debuggable
- Initial app PID: `14030`
- Evidence branch: `wip/upstream-bug-triage`

## Reported behavior

- A running scenario appeared to stall, possibly while no visible event was firing.
- The phone screen later turned off from inactivity.
- After the user woke/checked the phone, scenario activity appeared to resume.
- The in-app debug report only showed information from after the screen came back on.
- A counter-changing event may have continued during the apparently stalled interval; this is unconfirmed.

## Expected behavior

- Determine whether scenario evaluation genuinely stops, continues without visible actions, or is suspended by Android when the display turns off.
- Debug evidence should cover the interval before, during, and after the stall/screen-off transition.

## Evidence

- `logcat-before-live.txt`: full Android log buffer preserved immediately after attaching (about 21 MB).
- Live full-device logcat started afterward and writes to `/sdcard/Download/klickr-live-stall.txt`; pull after reproduction.
- Initial `dumpsys power` snapshot: awake, battery 59%, battery saver off, screen timeout 120 seconds, no Klick'r wake lock visible at capture time.

## Current hypotheses

1. Android/display sleep suspends some part of scenario evaluation, which resumes on wake.
2. The scenario remains active but only a non-visible action (such as a counter change) fires during the interval.
3. The in-app report is session/UI-lifecycle scoped and therefore omits pre-wake history even if execution continued.

## Unrelated or unconfirmed observation

- Repeated `OverlayMenu` failures to remove `OnComputeInternalInsetsListener`, ending in a proxy `NullPointerException`, occurred while navigating scenario/debug overlays around 23:43:40 and later. This has not been connected to the execution stall.

## Next capture

- On the next stall, preserve the faulty state for 20-30 seconds.
- Capture process/thread state plus power/display state before the user wakes or otherwise interacts when possible.
- After wake/resumption, stop and pull the live log, then correlate timestamps.

## Closure (2026-08-23)

- The user attempted another reproduction for roughly 10-15 minutes but could not reproduce missing pre-screen-off report data.
- The user considers the original observation likely to have been a report-reading mistake.
- `logcat-live-no-repro.txt` contains the full follow-up capture (about 42 MB).
- All observed report starts/stops match explicit detection-session starts/stops. No `Previous debug report was not finished`, protobuf read error, or report file write error was logged.

### Code audit

- Report occurrences are appended to `DebugReportMessages.pb` and flushed after every event occurrence.
- Screen off/on is not wired to report deletion, detection stop, or detection restart.
- Orientation changes cancel only the current frame-processing job and then continue the same detection/report session.
- Stopping detection finalizes the report overview and closes the message stream.
- Starting a new report intentionally recreates both report files, replacing the previous run. The UI/repository exposes the **last detection session report**, not report history.
- Disabling debug-report generation intentionally deletes the saved report.
- While a report is actively being written, report reads intentionally return no report content; availability becomes true after the session is stopped/finalized.

### Conclusion

- No anomalous loss/overwrite path associated with screen-off was found.
- Close as `[NO-REPRO]` / not currently considered a bug. Reopen only if missing data can be reproduced with timestamps or a consistent sequence involving starting a new run, disabling reports, or viewing before the run is finalized.
