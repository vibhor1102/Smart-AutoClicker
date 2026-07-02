# Same-phone backup incorrectly warns about a different screen

## Reproduction

- Source archive: `/sdcard/Download/1.zip`
- Source app: v3.5.1-era backup, database version 18
- Same physical Xiaomi M2012K11I phone used for export and import
- Import app: normal debug package `com.buzbuz.smartautoclicker.debug`
- Reproduced on 2026-06-30
- Import log: `Smart scenario is valid, has warnings: true`

## Root cause

The archive stores the screen size at the orientation active during export:

- Exported size in `1/1.json`: `2400 x 1080` (landscape)
- Phone size during import: `1080 x 2400` (portrait)

`SmartBackupDataSource.verifyExtractedBackup` compares these as ordered points:

```kotlin
screenCompatWarning = screenSize != Point(backup.screenWidth, backup.screenHeight)
```

Therefore, the same physical resolution rotated by 90 degrees is considered a different screen. This is not caused by a physical screen change or by upgrading from v3.5.1. The v3.5.1 exporter stored `screenSize.x` and `screenSize.y` in the same orientation-sensitive way, and its verifier used the same strict comparison.

## Impact

- Users receive a misleading warning that the scenario came from a device with a different screen.
- Whether the warning appears depends on the orientation during export and import.
- The warning itself does not rescale or discard coordinates; verification returns the imported scenario unchanged.
- This is separate from the v21-to-v22 `IN_AREA` compatibility bug documented in the neighboring evidence folder.

## Suggested behavior

Treat dimensions that differ only by orientation as the same physical screen for this compatibility check. For example, compare normalized dimensions `(min(width, height), max(width, height))`.

If orientation compatibility is important, the warning should instead explicitly say that the scenario was exported in a different orientation. Ideally, backup metadata would separately record the physical display dimensions and scenario/export orientation.

## Evidence

- `1-source.zip`: exact archive pulled from the phone
- `after-import.png`: screen capture after import

