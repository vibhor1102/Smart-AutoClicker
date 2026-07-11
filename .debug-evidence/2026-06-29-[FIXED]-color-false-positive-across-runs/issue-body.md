## Summary

The first color evaluation of a new run can reuse the previous run's captured frame, causing an immediate false positive even when the current screen no longer matches.

I have been able to reproduce this issue reliably on-device.

## Build and device

- Branch tested: `dev-4.0.0-beta03-fixes`
- Commit tested: `f5e34dc8215a741ac19d1640f970b698ca1984d4`
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

## Expected behavior

The second run should evaluate the current screen only. Since the current screen no longer matches, the color condition should not trigger.

## Actual behavior

The second run can immediately post the notification and stop as though the color still matched. I was able to reproduce the false positive repeatedly on the non-matching screen after a prior positive run.

## Live timing evidence

Captured false-positive runs in the non-matching location showed:

- `13:52:46.123` start detection; `13:52:46.169` stop detection: `46 ms`
- `13:54:00.560` start detection; `13:54:00.602` stop detection: `42 ms`
- `13:54:01.321` start detection; `13:54:01.355` stop detection: `34 ms`

Each run resized the virtual display to the scaled detection size, processed the condition, executed the stop actions, and resized back to the physical display size within a few dozen milliseconds.

## Suspected root cause

`ImageReaderProxy` appears to retain the previous run's bitmap in `lastFrame`.

Probable flow:

1. `DetectorEngine.startDetection` asks `DisplayRecorder` to resize screen capture to the scenario's scaled detection size.
2. `DisplayRecorder.resizeDisplay` calls `ImageReaderProxy.resize`.
3. `ImageReaderProxy.resize` closes the current `ImageReader` and creates a new one, but does not clear `lastFrame`.
4. The processing loop immediately calls `acquireLatestBitmap` / `getLastFrame`.
5. If the newly created reader has not received its first frame yet, `acquireLatestImage()` returns `null`.
6. `getLastFrame()` then falls back to `lastFrame`, returning the bitmap retained from the previous run.
7. The color condition is evaluated against that stale matching bitmap. Its actions execute and stop detection before a fresh frame arrives.

This looks like a frame-lifecycle bug, not a genuine color-distance false positive.

## Possible fix direction

Clear `lastFrame` whenever `resize(...)` replaces the `ImageReader`. After a resize, `getLastFrame()` should return `null` until the new reader supplies a frame from the current screen.

Focused coverage could verify:

1. a frame may still be reused while the same reader remains active, if that behavior is intentional;
2. a frame from the previous reader is never returned after resize;
3. detection waits for a fresh frame before processing after start/resize.

## Extra evidence

I can provide more evidence, screenshots, or a sample scenario if needed.
