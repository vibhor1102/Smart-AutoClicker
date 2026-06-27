# Scenario restarts during waits after an orientation change

## Status

Root cause identified from live logs, a read-only database snapshot, and the runtime code path. This is not simply inaccurate wait timing: orientation handling cancels the action sequence while it is waiting and restarts event processing from the beginning.

## Scenario captured

Debug package: `com.buzbuz.smartautoclicker.debug`

Build context:

- Built from `dev-4.0.0-beta03-fixes` at commit `f5e34dc8215a741ac19d1640f970b698ca1984d4`.
- Installed package reports `versionName=4.0.0-beta02`, `versionCode=20087`; the development branch had not updated the displayed version name.

Scenario `Default name`, event `Default event`:

1. System action: Recent Apps
2. Wait: 1,444 ms
3. Wait: 100 ms
4. System action: Home
5. Wait: 200 ms
6. Intent: start `com.supercell.clashofclans/com.supercell.titan.GameApp`
7. Wait: 20,000 ms
8. Swipe

The event has a 109,999 ms detection cooldown and a one-pixel color condition.

## Actual result

The scenario visibly starts again several times within roughly ten seconds. The configured long wait and cooldown do not get a chance to complete.

## Live evidence

While the loop was active, the app repeatedly received alternating display configurations:

- Landscape: 2400 x 1080
- Portrait: 1080 x 2400

The changes occurred about every 2-5 seconds. Each change logged:

- `DisplayMetrics: New DisplayConfig`
- `DetectorEngine: onOrientationChanged`
- screen-capture virtual-display resize
- abandonment of the previous image buffer
- occasional invalid/out-of-bounds detection-area messages during the transition

The first action opens Recent Apps. In this device state, Recent Apps participates in the landscape/portrait transition, creating the configuration change that interrupts the same action sequence.

## Code mechanism

1. `ActionExecutor.executeActions` executes actions sequentially in one coroutine.
2. A Wait action is implemented with cancellable coroutine `delay(...)`.
3. `DetectorEngine.onScreenOrientationChanged` cancels and joins the whole `processingJob` before resizing screen capture.
4. The active Wait is therefore cancelled, along with the remainder of the event's actions.
5. `DetectorEngine` starts a fresh processing job after the resize.
6. The event cooldown is started only after `executeActions(...)` returns successfully. Because cancellation interrupts it first, cooldown is never started.
7. If the event condition still matches after processing restarts, its actions begin again at action 1, opening Recent Apps again and sustaining the loop.

Relevant code:

- `core/smart/processing/.../ActionExecutor.kt`: sequential action execution and cancellable Pause delay.
- `core/smart/processing/.../DetectorEngine.kt`: orientation handler cancels and recreates the processing job.
- `core/smart/processing/.../ScenarioProcessor.kt`: cooldown starts only after all event actions finish.

## Confirmed minimal reproduction

1. Start in a landscape app.
2. Create an always/reliably matching screen event with a long reload/cooldown time.
3. Add only a Recent Apps system action followed by a long Wait.
4. Run it on a device where Recents changes the display orientation.

Expected: Recent Apps runs once, the Wait completes, and cooldown prevents another run.

Actual: the orientation change cancels the Wait; processing restarts; the event immediately executes Recent Apps again.

This minimal setup was reproduced successfully after the original investigation. Both configured protections are defeated: the Wait is cancelled by the orientation restart, and the reload/cooldown time is never armed because the action sequence never completes successfully.

## Evidence files

- `screen-looping.png`: scenario visibly active over Clash of Clans during the bad loop.
- `C:\Users\Vibhor\Downloads\Screenrecorder-2026-06-27-17-19-14-801 (1).mp4`: 36-second recording of the confirmed minimal setup repeatedly switching between the landscape app and Recents.
- `database-copy/`: read-only snapshot of the debug app's scenario database used to confirm exact actions, waits, condition, and cooldown.

## Follow-up questions for upstream grouping

- Should an orientation change pause/resume the in-flight action sequence instead of cancelling it?
- If cancellation is intentional, should the interrupted event enter cooldown or otherwise avoid immediate re-entry?
- Does the same restart occur with ordinary physical rotation, app-driven rotation, and Recents independently?
