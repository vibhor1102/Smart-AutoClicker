## Description

Importing a scenario on the same phone shows the warning that it was generated on a device with a different screen when the export and import were performed in different orientations.

I reproduced this using a v3.5.1-era archive made on the same Xiaomi M2012K11I phone. I can provide the example scenario and further evidence if needed.

## Steps to reproduce

1. Rotate a phone to landscape.
2. Export a scenario. The backup records dimensions such as `2400 x 1080`.
3. Rotate the same phone to portrait.
4. Import the scenario. The phone now reports `1080 x 2400`.

## Expected

The app recognizes that the dimensions represent the same physical screen in a different orientation, or presents an orientation-specific message if that distinction matters.

## Actual

The app warns that the scenario was generated from a device with a different screen.

## Cause

`SmartBackupDataSource.verifyExtractedBackup` performs a strict ordered comparison:

```kotlin
screenCompatWarning = screenSize != Point(backup.screenWidth, backup.screenHeight)
```

Consequently, `2400 x 1080` and `1080 x 2400` are considered different screens.

## Suggested fix

Normalize both dimension pairs before comparing them, such as comparing `(min, max)`, and add tests covering same-resolution portrait/landscape imports. If orientation should remain noteworthy, use a separate, accurately worded orientation warning.

