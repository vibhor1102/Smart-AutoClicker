# Smart AutoClicker Bug Triage: Scenario Sorting Behavior

## Environment
- Branch: `dev-4.0.0-beta03-fixes`
- Package Name (Debug): `com.buzbuz.smartautoclicker.patched.debug` or `com.buzbuz.smartautoclicker.debug`
- Device: Android 14

## Scenario Sorting Investigation Goals
1. Understand the sorting modes available in the main scenario selection screen.
2. Determine what triggers a bump in the usage count or timestamp of a scenario (is it opening it, or actually running it?).
3. Determine when the scenario list sorting refreshes (does it refresh immediately, on screen return, etc.?).
4. Identify any bugs in the current implementation.

## Evidence files
- [ScenarioDao.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/database/src/main/java/com/buzbuz/smartautoclicker/core/database/dao/ScenarioDao.kt)
- [DumbScenarioDao.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/dumb/src/main/java/com/buzbuz/smartautoclicker/core/dumb/data/database/DumbScenarioDao.kt)
- [ScenarioAdapter.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/smartautoclicker/src/main/java/com/buzbuz/smartautoclicker/scenarios/list/adapter/ScenarioAdapter.kt)

## Hypotheses & Findings

### 1. What triggers a scenario usage bump ("markAsUsed")?
**Finding:** **Opening the scenario** triggers the usage bump (incrementing the use count and updating the last used timestamp). Running the scenario (clicking Play on the overlay) is not required.
- **For Dumb Scenarios:** When the scenario is opened, `LocalService.startDumbScenario` initializes the engine via `dumbEngine.init(dumbScenario)`, which launches a background coroutine: `dumbRepository.markAsUsed(dumbScenario.id)`.
- **For Smart Scenarios:** When the scenario is opened, `LocalService.startSmartScenario` calls `smartProcessingRepository.setScenarioId(scenario.id, markAsUsed = true)`. This propagates to `SmartProcessingRepositoryImpl`, which launches a background coroutine: `scenarioRepository.markAsUsed(identifier)`.

### 2. How the list is sorted?
**Finding:** Sorted inside `FilteredScenarioListUseCase.kt` via the `sortAndFilter` method:
- **Search Mode:** If a search query is active, the list is filtered but NOT sorted by the selected sort settings (it fallback to alphabetical sort by name ASC).
- **Sort Modes:**
  - `NAME`: Alphabetically ascending or descending.
  - `RECENT`: Descending (most recent first) or ascending (oldest first) based on `lastStartTimestampMs`. Scenarios never used have a timestamp of `0L` (shown as "Never used").
  - `MOST_USED`: Descending (most used first) or ascending (least used first) based on `startCount`.

### 3. When and how the list sorting is refreshed?
**Finding:** 
- The list query in Room for both smart and dumb scenarios includes `@Relation` to their respective stats tables. Therefore, any update to the stats tables automatically triggers Room's Flow to emit a new list.
- However, since `ScenarioActivity` calls `finish()` immediately when a scenario is successfully opened (`handleScenarioStartResult`), the main activity is destroyed.
- Therefore, the user does not see the list reordered in real-time. When they close/stop the scenario and open the app again, the new `ScenarioActivity` is created, queries the database, and displays the list in the updated sorted order.

---

## Bugs Identified

### Bug A: Database query in `ScenarioDao` and `DumbScenarioDao` uses wrong column for retrieving scenario stats (CRITICAL)
In [ScenarioDao.kt:L106-107](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/database/src/main/java/com/buzbuz/smartautoclicker/core/database/dao/ScenarioDao.kt#L106-107):
```kotlin
    @Query("SELECT * FROM $SCENARIO_USAGE_TABLE WHERE id=:scenarioId")
    suspend fun getScenarioStats(scenarioId: Long): ScenarioStatsEntity?
```
And in [DumbScenarioDao.kt:L121-122](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/dumb/src/main/java/com/buzbuz/smartautoclicker/core/dumb/data/database/DumbScenarioDao.kt#L121-122):
```kotlin
    @Query("SELECT * FROM dumb_scenario_stats_table WHERE id=:scenarioId")
    suspend fun getScenarioStats(scenarioId: Long): DumbScenarioStatsEntity?
```

**The Bug:**
In both `ScenarioStatsEntity` and `DumbScenarioStatsEntity`, `id` is the auto-generated primary key of the stats row. The scenario's actual database ID is stored in the `scenario_id` (or `dumb_scenario_id`) column.
By querying `WHERE id = :scenarioId`, the DAO queries the stats table using the scenario's ID against the stats row's auto-generated primary key instead of the corresponding foreign key column.
This leads to:
1. **Wrong Stats Updates:** Toggling scenario with ID `X` fetches and updates the stats row where PK `id = X`. If that row exists, it updates it, meaning stats of another scenario get modified.
2. **Duplicate Rows / Reset Stats:** If no row in stats has PK `id = X`, it inserts a new stats row. But next time the scenario is opened, it still looks for PK `id = X`, fails to find it (since the new row was inserted with a different auto-generated PK), and inserts yet another row.

**Fix:**
Change the queries to query by the correct foreign key columns:
- For `ScenarioDao.kt`: `WHERE scenario_id = :scenarioId`
- For `DumbScenarioDao.kt`: `WHERE dumb_scenario_id = :scenarioId`

---

### Bug B: DiffUtil Content Comparison Workaround in `ScenarioAdapter`
In [ScenarioAdapter.kt:L134-136](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/smartautoclicker/src/main/java/com/buzbuz/smartautoclicker/scenarios/list/adapter/ScenarioAdapter.kt#L134-136):
```kotlin
    override fun areContentsTheSame(oldItem: ScenarioListUiState.Item, newItem: ScenarioListUiState.Item): Boolean =
        if (oldItem is ScenarioListUiState.Item.SortItem && newItem is ScenarioListUiState.Item.SortItem) true
        else oldItem == newItem
```

**The Bug:**
This always returns `true` when comparing two `SortItem`s. This means `DiffUtil` tells the list adapter that the sort/filter header did not change content when the user toggles a sorting option.
While the views (chips/buttons) visually update immediately when clicked by the user (because Android's View framework handles the checked state change locally upon click), this means:
- If sorting settings are changed programmatically, imported, or reset, the sorting header views in the RecyclerView will NOT reflect the updated settings because the item is never rebound.
- The reason this workaround was implemented is likely a bug in [SortViewHolder.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/smartautoclicker/src/main/java/com/buzbuz/smartautoclicker/scenarios/list/adapter/SortViewHolder.kt) where `onBind` calls `addOnButtonCheckedListener` without removing the old one first, which would result in duplicate listeners if the view was rebound.
