# Image detection area reset when importing a beta02 scenario

## Reproduction

- App: normal debug package `com.buzbuz.smartautoclicker.debug`
- Reported version: `4.0.0-beta02`, version code `20087`; source branch contains the post-beta02 database v22 fixes.
- Source archive: `one only.zip`
- Scenario: `Electro Dragons`
- Event: `Star/Time Exit`
- Affected condition: second image condition, 12% difference threshold
- Reproduced on 2026-06-30. The imported condition is displayed as whole-screen matching instead of its saved custom detection area.

## Root cause

The source archive is database version 21. Its JSON correctly stores the affected condition as:

- `detectionType`: `3` (`IN_AREA`)
- `detectionAreaLeft`: `1193`
- `detectionAreaTop`: `52`
- `detectionAreaRight`: `1245`
- `detectionAreaBottom`: `114`

The current app database is version 22, so importing this v21 archive uses `CompatDeserializer`. Its detection-type bounds are incorrectly declared as 1 through 2:

```kotlin
const val DETECTION_TYPE_LOWER_BOUND = 1
const val DETECTION_TYPE_UPPER_BOUND = 2
```

`deserializeConditionImageDetected` applies `coerceIn(1, 2)`, changing the valid `IN_AREA` value `3` to `WHOLE_SCREEN` value `2`.

The database captured immediately after reproduction proves the result:

- `threshold`: `12`
- `detection_type`: `2`
- all four detection-area coordinates remain present (`1193,52,1245,114`)

The coordinates are not lost. The mode that makes the app use them is corrupted during compatibility import.

## Screen compatibility warning

This is related evidence but not the cause of the area reset.

- The archive declares `screenWidth=1080` and `screenHeight=2400` because those were the device dimensions at export time.
- The image-condition coordinates are from a landscape setup (for example, X positions exceed 1080).
- Importing while the phone reports portrait `1080x2400` produces no warning.
- Importing while it reports landscape `2400x1080` can produce the warning, despite it being the same physical phone.

This explains why the warning is intermittent. The area-reset bug itself is deterministic whenever a pre-v22 `IN_AREA` image condition goes through this compatibility reader.

## Likely fix

Allow `IN_AREA` in the compatibility bounds by changing the upper bound to `3` (preferably referring to the shared detection-type constants), and add an import/deserialization test proving that a v21 image condition with `detectionType=3` retains both its mode and rectangle.

## Evidence

- `one-only-source.zip`: exact source archive pulled from the phone
- `after-import-condition.png`: post-import screen capture
- `click_database`, `click_database-wal`, `click_database-shm`: database snapshot after reproduction

