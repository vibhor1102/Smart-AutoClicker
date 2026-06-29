# Try Condition hides confidence after an initially rejected color match

## Build and device

- Branch: `dev-4.0.0-beta03-fixes`
- Commit: `f5e34dc8215a741ac19d1640f970b698ca1984d4`
- Package: `com.buzbuz.smartautoclicker.debug`
- Installed manifest: `versionName=4.0.0-beta02`, `versionCode=20087`
- Android 14 / Xiaomi device

## Reproduction

The selected screen pixel consistently produces approximately `98.30%` color confidence.

### Working initialization

1. Save the color condition with 2% difference allowed (98% required confidence).
2. Open Try Condition.
3. The UI displays `98.30%` and accepts it against the 98% threshold.
4. Raise the live requirement to 99%.
5. The UI keeps displaying `98.30%` and correctly marks the result rejected.

### Faulty initialization

1. Save the same condition with 1% difference allowed (99% required confidence).
2. Open Try Condition on the same unchanged screen.
3. The Results column is blank instead of showing the computed `98.30%` rejected result.
4. Lower the live requirement to 98% without closing Try Condition.
5. The Results column remains blank and cannot recover, even though the same sample passes when Try Condition starts at 98%.

## Expected behavior

Try Condition should always display the detector's confidence when a valid sample was evaluated, independently of whether it passes the saved threshold. Starting at 99% should show `98.30%` as rejected; lowering the live threshold to 98% should immediately mark that existing result accepted.

## Root cause

The detector does calculate a result and confidence. The result is discarded later by the live-debugging pipeline:

1. `GetDebugLiveDetectionResultUseCase.invoke` defaults `filterNotFulfilled` to `true`.
2. Its flow filters out the entire event occurrence when the initial condition does not fulfill the saved threshold.
3. `TryImageConditionViewModel` therefore receives no `ScreenConditionResultUiState`, so the Results column is blank.
4. The live slider only changes `userThreshold` and re-evaluates the `positive` flag on an already-published result.
5. Because the rejected initial result was never published, lowering the slider has nothing to re-evaluate and cannot recover until Try Condition is closed and restarted with a permissive saved threshold.

The threshold is therefore incorrectly controlling whether a diagnostic result exists, when it should only control whether the displayed confidence is accepted.

## Likely fix direction

The Try Condition screen should subscribe to unfiltered live detection results, for example by invoking `GetDebugLiveDetectionResultUseCase` with `filterNotFulfilled = false`. Other debugging consumers that intentionally want only fulfilled events can retain the existing default behavior.

## Evidence

- `screen-initial.png`: Try Condition initialized at 98%, displaying `98.30%` confidence.
- `screen-99-initial.png`: identical screen initialized at 99%, with a blank Results column.
- `screen-99-then-98.png`: same Try Condition session lowered to 98%, still blank.
- Logs confirm the faulty session started with color condition `threshold=1` and the detector continued processing.
