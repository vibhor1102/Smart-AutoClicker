# Detection never matches — triage notes

## Environment

- Date: 2026-08-15
- Branch: `wip/upstream-bug-triage` (already active)
- Device: Xiaomi M2012K11I (Android 14), 1080 × 2400
- Connected through wireless ADB: yes
- Installed packages: normal, normal debug, patched, and patched debug

## Reported symptom

When a scenario runs, image detection does not find a match and remains stuck in a no-match state.

## Current status

Initial device inspection completed. The reported scenario was clarified as `✅ Electro Dragons` (scenario id 5), not `Auto Farm`.

## Confirmed facts

- The normal patched app is running on the connected Android 14 phone; its media projection is active.
- At 23:57:07, `DetectorEngine` started normally and reports a 1200 × 540 virtual display for the 2400 × 1080 landscape screen. This is the expected 0.5 scale for a detection quality of 1200.
- The engine loaded 44 conditions and is processing at 10 FPS. No native-library or screen-capture failure was logged.
- Electro Dragons' enabled `Attack` event uses OR logic across six exact-position image conditions. One current reference (condition 471) is a close visual match to the live screen crop: a 1.29% mean RGB difference versus its configured 10% threshold. It should be eligible to fire.
- Several older Attack templates no longer closely match the live UI, but OR logic means that alone cannot explain the failure.

## Working hypothesis

Confirmed: a virtual-display resize left the detector with no usable frames. The engine starts and its state changes to `DETECTING`, but `acquireLatestBitmap()` returns no frame; the loop therefore waits and never calls the scenario processor. Stopping and restarting the scenario keeps the same projection/virtual-display pipeline, so it cannot repair this state.

## Finalized report

- Scenario id: 5 (`✅ Electro Dragons`)
- Duration: 285,753 ms (4 min 45.753 s)
- Processed frames: 0
- Fulfilled image events: 0
- Triggered events: 0
- Counters loaded: `AttackCount`, `ConnectionError`

The surrounding logs show the capture pipeline being resized from 600 × 270 to 1200 × 540 and also passing through portrait/landscape changes. The final Electro Dragons run recreates an `ImageReader` at 1200 × 540, but there is no post-resize frame validation. This Xiaomi/MIUI device can therefore leave the reader producing no frames without surfacing a capture error.

## Direct debugger check

- Attached a Java debugger directly to the normal patched process (`pid 6221`) through its JDWP endpoint. No APK was installed or changed.
- A breakpoint in `DetectorEngine.processScreenImages()` was hit while Electro Dragons started. This directly proves the detection coroutine entered its image-acquisition loop.
- The synchronized run's final debug overview is scenario 5, duration 83,193 ms, with no frame-count field and no fulfilled-event fields (both protobuf defaults: zero).
- The next boundary breakpoint (`ScenarioProcessor.process()`) did not yield a trustworthy result because the command-line Android debugger client stalled. Therefore the exact return value from `acquireLatestBitmap()` was **not** directly inspected.

Current proven boundary: detection starts and enters its acquisition loop; no frame is recorded as processed by the scenario processor. The unproven boundary is whether `ImageReader.acquireLatestImage()` returns null, whether a captured bitmap is discarded before processing, or whether the report's frame counter is itself defective.

## Evidence

- `screen-live.png` and `screen-electro-dragons-live.png`: live phone screenshots.
- `logcat-snapshot-live.txt`: device log snapshot.
- `click_database-live.db` and `click_database-live.db-wal`: read-only database snapshot.
- `active-471.png`: matching Electro Dragons Attack reference image.
- `DebugReportOverview-final.pb` and `DebugReportMessages-final.pb`: finalized report files.
