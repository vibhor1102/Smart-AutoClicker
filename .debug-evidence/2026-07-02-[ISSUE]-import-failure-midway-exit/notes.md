# Bug Triage Notes: Import Scenario Mid-way Failure

## Environment
- **Device**: Android 14 device
- **App Package**: `com.buzbuz.smartautoclicker.debug`
- **Branch**: `wip/upstream-bug-triage`

## Reproduction Steps
1. Prepare a backup file from a previous database version (e.g., version 21) that includes a scenario with an `ON_NUMBER_DETECTED` condition.
2. In the app, go to Settings -> Backup & Restore -> Restore Scenario.
3. Select the backup file and attempt to import it.

## Observed vs Expected Behavior
- **Observed**: The import fails mid-way, and a `NullPointerException` is thrown from `ConditionMapper.kt:toDomainNumberCondition`.
- **Expected**: The scenarios are imported successfully.

## Evidence Files & Paths
- **Backup archive**: `.debug-evidence/2026-07-02-import-bug-triage/backup.zip` (Pulled from `/sdcard/Download/SmartAutoClicker-Backup (8).zip` on the device).
- **Extracted json**: `.debug-evidence/2026-07-02-import-bug-triage/backup_extracted/15/15.json` has `ON_NUMBER_DETECTED` condition with `"threshold": 0`.
- **Logcat**: `.debug-evidence/2026-07-02-import-bug-triage/logcat_utf8.txt`

### Stacktrace from Logcat:
```
07-02 17:25:31.856 E/BackupEngine(15886): java.lang.NullPointerException
07-02 17:25:31.856 E/BackupEngine(15886): 	at com.buzbuz.smartautoclicker.core.domain.model.condition.ConditionMapperKt.toDomainNumberCondition(ConditionMapper.kt:195)
07-02 17:25:31.856 E/BackupEngine(15886): 	at com.buzbuz.smartautoclicker.core.domain.model.condition.ConditionMapperKt.toDomain(ConditionMapper.kt:158)
07-02 17:25:31.856 E/BackupEngine(15886): 	at com.buzbuz.smartautoclicker.core.domain.model.event.EventMapperKt.toDomainScreenEvent(EventMapper.kt:78)
07-02 17:25:31.856 E/BackupEngine(15886): 	at com.buzbuz.smartautoclicker.core.domain.model.event.EventMapperKt.toDomain(EventMapper.kt:63)
07-02 17:25:31.856 E/BackupEngine(15886): 	at com.buzbuz.smartautoclicker.core.domain.model.scenario.ScenarioMapperKt.toDomain(ScenarioMapper.kt:56)
07-02 17:25:31.856 E/BackupEngine(15886): 	at com.buzbuz.smartautoclicker.core.domain.Repository.addScenarioCopy(Repository.kt:156)
07-02 17:25:31.856 E/BackupEngine(15886): 	at com.buzbuz.smartautoclicker.feature.backup.domain.BackupRepository$restoreScenarioBackup$1$1$2.invokeSuspend(BackupRepository.kt:123)
```

## Root Cause / Hypothesis
- **Root Cause**: In [CompatDeserializer.kt:deserializeConditionNumberDetected](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/database/src/main/java/com/buzbuz/smartautoclicker/core/database/serialization/compat/CompatDeserializer.kt#L313-L341), the `threshold` field is not parsed from `jsonCondition` and is not set in the constructed `ConditionEntity`. 
- Consequently, `ConditionEntity.threshold` is deserialized as `null`. When `toDomainNumberCondition` tries to assert it is non-null (`threshold = threshold!!` at line 195), it crashes with a `NullPointerException`.

## Suggested Fix
Modify `CompatDeserializer.deserializeConditionNumberDetected` to extract the `threshold` field from the JSON object and pass it to the `ConditionEntity` constructor, similar to other screen conditions (e.g., color and text conditions).
