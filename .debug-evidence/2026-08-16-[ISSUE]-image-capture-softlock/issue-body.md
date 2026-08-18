# Image-condition capture can leave an unrecoverable touch-blocking overlay when no screen frame arrives

## Summary

Creating an image condition can softlock the device interaction if the active screen-capture session does not provide a frame. Klick'r hides its capture toolbar, leaves a transparent full-screen overlay in place, and offers no cancel or error recovery. The underlying app/game remains visible, but Klick'r receives the taps.

This is separate from the cause of the missing screen frames: regardless of why a capture frame is unavailable, the image-condition capture UI must recover safely.

## Reproduction

1. Start a scenario/capture session that is in a state where `DisplayRecorder` cannot obtain a screen frame.
2. Open an image condition and request a new screenshot for it.
3. Wait for the capture attempt to fail.

## Expected

The app should restore a usable capture menu and clearly offer retry/cancel (or dismiss the overlay) when no screenshot is available.

## Actual

The toolbar disappears and an invisible full-screen Klick'r overlay remains. Taps no longer reach the app/game and there is no in-app cancel route from this state. On the affected Xiaomi Android 14 device, wireless ADB was needed to investigate/recover; without it the user would need to restart the phone.

## Evidence

- Xiaomi M2012K11I, Android 14, patched Klick'r, physical 1080x2400, Clash of Clans in landscape.
- `dumpsys media_projection` reports an active `TYPE_SCREEN_CAPTURE` owned by Klick'r.
- Window-manager state shows a complete 2400x1080 Klick'r application-overlay window (`type=2032`) above the resumed game. It is not `NOT_TOUCHABLE`, so it intercepts taps despite being visually transparent.
- `softlock-live.png` shows the game visible with no capture toolbar.
- A direct JDWP debugger attachment after the failure found all dispatcher workers idle, consistent with the timed capture attempt having completed without a result rather than a thread remaining blocked.

## Code path

`CaptureMenu` enters `CAPTURE`, hides its menu and enables the full-screen selector overlay. `CaptureViewModel.takeScreenshot()` invokes `DisplayRecorder.takeScreenshot()` after 200 ms. The recorder retries only for one second and returns `null` on missing frames. The ViewModel then returns without invoking its callback, so `CaptureMenu` never leaves `CAPTURE`. `onCancel()` intentionally handles only `SELECTION` and `ADJUST`, not `CAPTURE`.

- `CaptureMenu.kt:83-104`, `143-178`
- `CaptureViewModel.kt:44-53`
- `DisplayRecorder.kt:160-170`

## Suggested fix

Treat a null screenshot, capture exception, and coroutine cancellation as explicit capture outcomes. Always exit `CAPTURE`, restore a usable menu/overlay state, and show a retry/cancel/error message. The cancel action should also be available while a capture is pending.
