## Description

Importing a database-v21 scenario into the current database-v22 app changes image conditions configured as **In area** to **Whole screen**, even though the saved detection rectangle is present in the backup.

I can reproduce this consistently with a scenario exported from 4.0.0-beta02. A minimal reproducer and a screenshot of the intended detection area are attached below.

## Reproduction files

[Download the minimal scenario backup: `detection area bug.zip`](PASTE_ZIP_UPLOAD_LINK_HERE)

The affected `Faulty Event` contains one image condition with a 12% difference threshold. The archive also contains a separate `Stop` trigger event inherited from the source scenario, but it is not involved in the bug.

This is how the custom **In area** rectangle is supposed to look before export:

![Expected custom detection area](PASTE_SCREENSHOT_UPLOAD_LINK_HERE)

## Steps to reproduce

1. Import the attached `detection area bug.zip` into the current app.
2. Open the imported `detection area bug reproduction` scenario.
3. Open the `Faulty Event` event.
4. Edit its only image condition, which has a 12% difference threshold.
5. Check the selected detection mode.

## Expected

The condition remains configured as **In area** and retains the small custom detection zone shown in the screenshot.

## Actual

The condition is changed to **Whole screen**. The four rectangle coordinates remain in the imported database, but they are no longer used because the detection mode was changed.

## Confirmed cause

The attached archive is database version 21. Its JSON correctly contains:

```json
{
  "threshold": 12,
  "detectionType": 3,
  "detectionAreaLeft": 1193,
  "detectionAreaTop": 52,
  "detectionAreaRight": 1245,
  "detectionAreaBottom": 114
}
```

`detectionType=3` is `IN_AREA`.

The current database is version 22, so the import goes through `CompatDeserializer`. That deserializer defines the accepted detection-type range as 1 through 2. `deserializeConditionImageDetected` then applies `coerceIn(1, 2)`, converting the valid `IN_AREA` value `3` to `WHOLE_SCREEN` value `2`.

Immediately after reproducing the import, the database contained the same four detection-area coordinates and the same 12% threshold, but `detection_type=2`. This confirms that the rectangle itself is preserved while the mode that uses it is corrupted.

## Suggested fix

Include value 3 / `IN_AREA` in the compatibility deserializer's accepted range, preferably using the shared detection-type constants, and add a regression test proving that importing a v21 in-area image condition preserves both its mode and rectangle.
