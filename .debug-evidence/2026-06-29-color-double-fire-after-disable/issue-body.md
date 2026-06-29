## Summary

When two screen/color events match on the same frame, the first event can execute a Disable All action and yet another matching screen event can still run afterward in that same pass.

In my case, both events sent their notifications, even though the first event was supposed to stop the scenario immediately by disabling all events.

I have been able to reproduce this issue on-device.

## Build and device

- Branch tested: `dev-4.0.0-beta03-fixes`
- Commit tested: `f5e34dc8215a741ac19d1640f970b698ca1984d4`
- Package: `com.buzbuz.smartautoclicker.debug`
- Installed manifest: `versionName=4.0.0-beta02`, `versionCode=20087`
- Android 14 / Xiaomi device

## Reproduction

I have reproduced this reliably in a large imported scenario with 20+ events.

Relevant event shape:

- multiple screen events using color matching conditions;
- each matching event sends a notification;
- each matching event also includes a Toggle Event action that disables all events, intended to stop the scenario immediately.

Observed behavior:

1. Two color events match on the same frame.
2. The first event runs and executes its Disable All action.
3. Despite that, another matching color event still runs in the same scenario pass and also sends its notification.
4. The debug report shows both events as executed, even though the first one should have stopped further execution.

I have not yet found a tiny clean reproduction, but I was able to reproduce it repeatedly in the larger scenario, including after exporting/importing that scenario into the debug app.

## Expected behavior

Once a screen event executes a Disable All action, later screen events from that same run should not continue executing. The scenario should stop there.

## Actual behavior

Another screen event that was also matching can still execute after Disable All, so both notifications end up being sent.

## Suspected root cause

This looks like a same-frame iteration bug in `ScenarioProcessor`, not a detector false positive.

Probable flow:

1. `ScenarioProcessor.process(...)` fetches the enabled screen events once via `processingState.getEnabledScreenEvents()`.
2. `processScreenEvents(...)` iterates that passed-in collection for the whole frame.
3. When the first matching screen event executes a Toggle Event action with `toggleAllType = DISABLE`, `ActionExecutor.executeToggleEvent(...)` immediately calls `processingState.disableAll()`.
4. `disableAll()` does update the enabled-state maps immediately.
5. But `processScreenEvents(...)` does not re-check `processingState.isEventEnabled(screenEvent.id.databaseId)` before continuing to the next screen event.
6. It also does not stop when all events become disabled mid-loop.
7. So a later screen event that was already present in that frame's initial collection can still be processed and execute actions, even though it is no longer enabled in state.

There is already an enabled-state re-check for trigger events in `processTriggerEvents(...)`, but there does not appear to be an equivalent guard in `processScreenEvents(...)`.

## Why this may show up more in large scenarios

This is probably easier to trigger when:

- more than one screen event matches on the same captured frame;
- the first matching event has `keepDetecting = true`, allowing the loop to continue naturally;
- a later event is already sitting in the precomputed collection for that same frame.

That would explain why I can reproduce it in a large scenario but not yet in a tiny fresh one.

## Possible fix direction

`processScreenEvents(...)` should likely behave more like the trigger-event loop and re-check live enabled state before processing each event. It should also stop immediately once all events become disabled.

A minimal fix could be one or both of:

1. skip any screen event whose id is no longer enabled at the top of each loop iteration;
2. break the loop immediately after `executeActions(...)` if `processingState.areAllEventsDisabled()` is now true.

## Extra evidence

I can provide more evidence, screenshots, debug report details, or the example scenario if needed.
