# Wait action / frame reuse investigation

## Environment

- Date: 2026-08-15
- Device: M2012K11I (Android 14), connected through wireless ADB.
- App under investigation: patched production Klick'r app with its debug flag enabled (package/build to confirm).
- Repository branch left unchanged: `feature/scenario-switcher-v4.1`.

## Reported behavior

- In the most recently saved report, the event `battle machine or flying machine` contains a wait action of several seconds.
- The following event, `if heroes undeployed`, appears a few hundred milliseconds later and detects from the same captured frame (frame #96).

## Expected behavior to verify

- Determine whether a wait delays the scenario's whole event progression or only the actions that follow it within the same event/action sequence.
- Correlate saved-report data, app logs, and the source execution path.

## Status

- Investigation complete; no app data was changed.

## Confirmed evidence

- Connected package: `com.buzbuz.smartautoclicker.patched`, version `4.0.0-beta08-patched.1`, debuggable.
- The current database has event 20, `Battle Machine or Flying Machine`, at priority 8. Its actions are three clicks, then `Wait` (`PAUSE`) at action priority 3 with `pauseDuration=3210`, then a toggle action.
- The toggle action disables event 20 and enables event 27, `If Heroes Undeployed` (priority 9). Both events have `keep_detecting=1`.
- The report contains event 20 at `9473 ms` and event 27 at `9844 ms`: a 371 ms difference. Both are recorded against frame 96.
- Randomization is enabled for this scenario, but it can vary a pause by only plus/minus 5 ms, not seconds.

## Root cause / interpretation

- A `Pause` is awaited synchronously by `ActionExecutor`; it must delay subsequent actions and the next event in that same processing loop.
- `keep_detecting=true` intentionally continues through later events using the already-captured bitmap. A pause does not request or wait for a fresh screen frame. Thus, the same-frame part is intended behavior, even after a real wait.
- The 371 ms report gap cannot result from the current 3210 ms pause. The debug report records occurrences but does **not** store a historical action snapshot. Its UI resolves event/action details from the current scenario database. Therefore the strongest explanation is that the report was generated before the Wait existed or had this duration (or while a previously loaded scenario version was active). If the current configuration was definitely in place before that session started, this is a real pause-execution/reporting bug requiring a focused fresh reproduction.

## Evidence files

- `DebugReportMessages.pb` and `DebugReportOverview.pb`: copied from the patched app cache.
- `click_database` with WAL/SHM: read-only snapshot of current scenario configuration.
