## Summary

Try Condition can hide the computed color confidence entirely when the initial saved threshold is stricter than the detected confidence.

On the same screen, a color sample that shows about `98.30%` confidence behaves correctly if Try Condition starts at `98%`, but if it starts at `99%`, the Results column is blank instead of showing `98.30%` as rejected. Lowering the live slider afterward to `98%` still does not recover until Try Condition is closed and reopened.

I have been able to reproduce this issue reliably on-device.

## Build and device

- Branch tested: `dev-4.0.0-beta03-fixes`
- Commit tested: `f5e34dc8215a741ac19d1640f970b698ca1984d4`
- Package: `com.buzbuz.smartautoclicker.debug`
- Installed manifest: `versionName=4.0.0-beta02`, `versionCode=20087`
- Android 14 / Xiaomi device

## Reproduction

The selected screen pixel consistently produces approximately `98.30%` color confidence.

### Working initialization

1. Save the color condition with `2%` difference allowed (`98%` required confidence).
2. Open Try Condition.
3. The UI displays `98.30%` and accepts it against the `98%` threshold.
4. Raise the live requirement to `99%`.
5. The UI keeps displaying `98.30%` and correctly marks the result rejected.

### Faulty initialization

1. Save the same condition with `1%` difference allowed (`99%` required confidence).
2. Open Try Condition on the same unchanged screen.
3. The Results column is blank instead of showing the computed `98.30%` rejected result.
4. Lower the live requirement to `98%` without closing Try Condition.
5. The Results column remains blank and cannot recover, even though the same sample passes when Try Condition starts at `98%`.

## Expected behavior

Try Condition should always display the detector's confidence when a valid sample was evaluated, even if the current threshold rejects it.

Starting at `99%` should show `98.30%` as rejected. Lowering the live threshold to `98%` should immediately mark that existing result accepted.

## Actual behavior

If the first evaluation does not fulfill the saved threshold, Try Condition hides the confidence completely and the live slider cannot recover the result in the same session.

## Suspected root cause

The detector does calculate a result and confidence, but the live-debugging pipeline appears to filter that result out too early:

1. `GetDebugLiveDetectionResultUseCase.invoke` defaults `filterNotFulfilled` to `true`.
2. The flow filters out the entire occurrence when the initial condition does not fulfill the saved threshold.
3. `TryImageConditionViewModel` then receives no `ScreenConditionResultUiState`, so the Results column is blank.
4. The live slider only changes `userThreshold` and re-evaluates the `positive` flag on an already-published result.
5. Because the rejected initial result was never published, lowering the slider has nothing to re-evaluate and cannot recover until Try Condition is restarted with a permissive saved threshold.

This makes the threshold control whether a diagnostic result exists, when it should only control whether the displayed confidence is accepted.

## Possible fix direction

The Try Condition screen should likely subscribe to unfiltered live detection results, for example by invoking `GetDebugLiveDetectionResultUseCase` with `filterNotFulfilled = false`. Other debugging consumers that intentionally want only fulfilled events could keep the current default behavior.

## Extra evidence

I can provide screenshots and any additional evidence if needed. I also have screenshots showing:

- Try Condition initialized at `98%`, displaying `98.30%`.
- The identical screen initialized at `99%`, with a blank Results column.
- The same Try Condition session lowered from `99%` to `98%`, still blank.
