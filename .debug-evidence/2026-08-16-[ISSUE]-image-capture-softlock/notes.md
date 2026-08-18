# Image-condition capture softlock

## Status

Live investigation in the normal patched app. No force-stop, permission, or scenario-data changes made.

## User report

After the resized screen-capture path stopped producing usable detection frames, requesting a new image-condition screenshot leaves Klick'r on a UI with no toolbar and an input-blocking overlay. Scenarios and test actions remain allowed even though they show no detection output.

## Initial aim

Capture the current screen, window/focus/capture state, logs, and debugger stack while the softlock is live. Determine whether the capture request is waiting for a frame, whether an overlay/capture activity has no recovery path, and document a maintainer-ready issue.

## Live evidence (2026-08-16)

- Device: Xiaomi M2012K11I, Android 14, physical 1080x2400; game running landscape.
- App process: `com.buzbuz.smartautoclicker.patched`, PID 6221; debuggable and attached through JDWP without modifying the app.
- `dumpsys media_projection` still reports the patched app as the owner of an active `TYPE_SCREEN_CAPTURE` session.
- Window manager shows multiple Klick'r application-overlay windows (`type=2032`) above the still-resumed Clash of Clans activity. One Klick'r window covers the complete 2400x1080 logical display. This is the visible input-blocking overlay. Screenshot: `softlock-live.png`.
- The full-screen window is marked `NOT_FOCUSABLE` but not `NOT_TOUCHABLE`; it therefore remains transparent to sight while still receiving taps. This explains the apparent game UI with blocked clicks.
- The debugger thread list after the failure has no worker currently running `takeScreenshot`; all dispatcher workers are waiting. This is consistent with the one-second screenshot attempt having already returned `null`, rather than a thread stuck in a wait.

## Confirmed app-side softlock path

1. `CaptureMenu` switches to `CAPTURE`, which hides the toolbar/menu, enables the full-screen overlay, and hides the selector (`CaptureMenu.kt:83-104`, `143-150`).
2. `CaptureViewModel.takeScreenshot()` waits 200ms then calls `DisplayRecorder.takeScreenshot()` (`CaptureViewModel.kt:44-53`).
3. `takeScreenshot()` retries a missing frame for only one second and returns `null` (`DisplayRecorder.kt:160-170`).
4. On `null`, the ViewModel returns without invoking the callback. Therefore `CaptureMenu` never changes from `CAPTURE` to `ADJUST` and never restores the toolbar (`CaptureMenu.kt:147-150`).
5. `onCancel()` has cases only for `SELECTION` and `ADJUST`, not `CAPTURE` (`CaptureMenu.kt:173-177`). The user consequently has no recovery action while the full-screen overlay remains active.

This is a confirmed app bug independent of whether the original loss of screen frames is caused by Android/Xiaomi or the earlier capture-resize path. Any missing frame can trigger the unrecoverable UI state.

## Suggested issue scope

Report the softlock separately from the frame-loss investigation. A robust fix must always leave `CAPTURE` on null/exception/cancellation, restore controls, and show a clear retry/cancel/error state. It should not rely on every device always returning a screenshot frame.

## Filed

Upstream issue: https://github.com/Nain57/Smart-AutoClicker/issues/1050
