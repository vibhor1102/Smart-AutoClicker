# Screen events can keep executing from a stale per-frame enabled snapshot after Disable All

## Build and device

- Branch: `dev-4.0.0-beta03-fixes`
- Commit: `f5e34dc8215a741ac19d1640f970b698ca1984d4`
- Package: `com.buzbuz.smartautoclicker.debug`
- Installed manifest: `versionName=4.0.0-beta02`, `versionCode=20087`
- Android 14 / Xiaomi device

## Reproduction

Observed on a large imported scenario with 20+ events.

Relevant event shape:

- multiple screen events using color matching conditions;
- each matching event sends a notification;
- each matching event also includes a Toggle Event action that disables all events, intended to stop the scenario immediately.

Observed behavior:

1. Two color events match on the same frame.
2. The first event runs and executes its Disable All action.
3. Despite that, another matching color event still runs in the same scenario pass and also sends its notification.
4. The debug report shows both events as executed, even though the first one should have stopped the scenario from progressing further.

The user has reproduced this reliably in the large scenario, including after exporting/importing it into the debug app. A tiny clean reproduction has not yet been found.

## Expected behavior

Once a screen event executes a Disable All action, scenario processing should stop for the current run. Later screen events from that same frame should not continue executing after all events have already been disabled.

## Root cause

This is most likely a same-frame iteration bug in `ScenarioProcessor`, not a false positive detector bug.

The key flow is:

1. `ScenarioProcessor.process(...)` fetches the enabled screen events once via `processingState.getEnabledScreenEvents()`.
2. `EventsState.getEnabledScreenEvents()` returns a collection snapshot derived from the currently enabled events.
3. `processScreenEvents(...)` then iterates that passed-in collection for the whole frame.
4. When a screen event executes a Toggle Event action with `toggleAllType = DISABLE`, `ActionExecutor.executeToggleEvent(...)` immediately calls `processingState.disableAll()`.
5. `disableAll()` does update the shared enabled-state maps right away.
6. But `processScreenEvents(...)` does not re-check `processingState.isEventEnabled(screenEvent.id.databaseId)` before processing each next screen event.
7. It also does not stop when `processingState.areAllEventsDisabled()` becomes true mid-loop.
8. So any later screen event that was already present in the frame's initial `events` collection can still be processed and execute actions, even though it is no longer enabled in state.

Code references:

- `core/smart/processing/src/main/java/com/buzbuz/smartautoclicker/core/processing/data/processor/ScenarioProcessor.kt`
  - line 120: captures enabled screen events once for the frame.
  - lines 158-178: iterates screen events without re-checking enabled state after earlier actions mutate state.
- `core/smart/processing/src/main/java/com/buzbuz/smartautoclicker/core/processing/data/processor/ActionExecutor.kt`
  - lines 215-224: Toggle Event with `toggleAll` calls `processingState.disableAll()`.
- `core/smart/processing/src/main/java/com/buzbuz/smartautoclicker/core/processing/data/processor/state/EventsState.kt`
  - lines 109-111 and 168-170: `disableAll()` really does remove the events from the enabled maps immediately.

There is already a defensive enabled-state re-check for trigger events in `processTriggerEvents(...)`, but there is no equivalent guard in `processScreenEvents(...)`.

## Why it may show up more in large scenarios

Large scenarios increase the chance that:

- more than one screen event matches on the same captured frame;
- the first matching event has `keepDetecting = true`, allowing the loop to continue naturally;
- another later event is already sitting in the same precomputed collection and still gets processed after Disable All.

That would explain why the bug reproduces in the user's large imported scenario but was harder to trigger in a tiny fresh setup.

## Fix direction

`processScreenEvents(...)` should behave like the trigger-event loop and re-check live enabled state before processing each event. It should also stop immediately once all events become disabled.

A minimal safe fix would be one or both of:

1. Skip any screen event whose id is no longer enabled at the top of each loop iteration.
2. Break the loop immediately after `executeActions(...)` if `processingState.areAllEventsDisabled()` is now true.

## Test gap

There is already a trigger-event test covering concurrent state mutation during iteration:

- `ProcessingTests.kt`: `TriggerEvent concurrent modification`

There does not appear to be an equivalent screen-event regression test covering:

- two matching screen events in the same frame;
- the first one disabling the second or disabling all events;
- verification that the second screen event never executes.
