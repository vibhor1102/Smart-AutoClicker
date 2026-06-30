## Description

Importing a database-v21 scenario into the current database-v22 app changes image conditions configured as **In area** to **Whole screen**, even though the saved detection rectangle is present in the backup.

I can reproduce this consistently with a scenario exported from 4.0.0-beta02. I can provide the example scenario, screenshots, and further evidence if needed.

## Steps to reproduce

1. In a database-v21 build (4.0.0-beta02), create an image condition using **In area** and select a custom detection zone.
2. Export the scenario.
3. Import it into the current database-v22 build.
4. Open the imported image condition.

## Expected

The condition remains configured as **In area** and retains its custom detection zone.

## Actual

The condition is changed to **Whole screen**. The four rectangle coordinates remain in the imported database, but they are no longer used because the detection mode was changed.

## Cause

`CompatDeserializer` defines the valid detection-type range as 1 through 2. However, `IN_AREA` is value 3. During v21 compatibility import, `deserializeConditionImageDetected` applies `coerceIn(1, 2)`, converting `IN_AREA` (`3`) to `WHOLE_SCREEN` (`2`).

In the reproduced example, the source JSON contained:

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

Immediately after import, the database contained the same coordinates but `detection_type=2`.

## Suggested fix

Include value 3 / `IN_AREA` in the compatibility deserializer's accepted range and add a regression test for importing a v21 in-area image condition.

