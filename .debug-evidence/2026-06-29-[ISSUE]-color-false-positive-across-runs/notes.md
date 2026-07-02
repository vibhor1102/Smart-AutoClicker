# First color evaluation of a new run can reuse the previous run's frame

## Build and device

- Branch: `dev-4.0.0-beta03-fixes`
- Commit: `f5e34dc8215a741ac19d1640f970b698ca1984d4`
- Package: `com.buzbuz.smartautoclicker.debug`
- Installed manifest: `versionName=4.0.0-beta02`, `versionCode=20087`
- Android 14 / Xiaomi device

## Reproduction

Use a scenario containing a screen event with:

- a color condition;
- a Notification action;
- a Toggle Event action configured to disable all events, which stops the scenario.

Steps:

1. Display a screen where the configured color condition matches.
2. Run the scenario once. It correctly posts the notification, disables all events, and stops.
3. Move to a screen where the color condition clearly does not match.
4. Run the scenario again.

## Actual behavior

The second run can immediately post the notification and stop as though the color still matched. The false positive can be reproduced repeatedly on the non-matching screen after a prior positive run.

## Live timing evidence

Two false-positive runs captured in the non-matching location showed:

- `13:52:46.123` start detection; `13:52:46.169` stop detection: 46 ms.
- `13:54:00.560` start detection; `13:54:00.602` stop detection: 42 ms.
- `13:54:01.321` start detection; `13:54:01.355` stop detection: 34 ms.

Each run resized the virtual display to the scaled detection size, processed the condition, executed the stop actions, and resized back to the physical display size within a few dozen milliseconds.

## Root cause

`ImageReaderProxy` retains the most recently converted bitmap in `lastFrame`.

When a scenario starts:

1. `DetectorEngine.startDetection` asks `DisplayRecorder` to resize screen capture to the scenario's scaled detection size.
2. `DisplayRecorder.resizeDisplay` calls `ImageReaderProxy.resize`.
3. `ImageReaderProxy.resize` closes the current `ImageReader` and creates a new one, but does not clear `lastFrame`.
4. The processing loop immediately calls `acquireLatestBitmap` / `getLastFrame`.
5. If the newly created reader has not received its first frame yet, `acquireLatestImage()` returns null.
6. `getLastFrame` then executes `?: lastFrame`, returning the bitmap retained from the previous run.
7. The color condition is evaluated against that stale matching bitmap. Its actions execute and stop detection before a fresh frame arrives.

The relevant behavior is in `core/common/display/.../ImageReaderProxy.kt`:

- `resize(...)` replaces the reader but leaves `lastFrame` populated.
- `getLastFrame()` falls back to `lastFrame` when the active reader has no new image.

This is a frame-lifecycle bug, not a color-distance false positive or retained condition result.

## Fix direction

Clear `lastFrame` whenever `resize(...)` replaces the `ImageReader`. After a resize, `getLastFrame()` should return null until the new reader supplies a frame from the current screen.

Add focused coverage proving that:

1. a frame may be reused while the same reader remains active, if that behavior is intentional;
2. a frame from the previous reader is never returned after resize;
3. detection waits for a fresh frame before processing after start/resize.

## Diagnostic note

Adding a Wait action is unnecessary for root-cause confirmation. Actions run only after the condition has already been evaluated, so a Wait would not prevent the stale-frame false positive.
