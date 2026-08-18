# Disable Keep executing for all patched-app scenarios

- User explicitly requested that every scenario event currently using Keep executing stop using it.
- Target package: `com.buzbuz.smartautoclicker.patched`.
- Completed: took recoverable backups, then set `event_table.keep_detecting` from `1` to `0` for all affected events.

## Result

- 53 events changed across all scenarios, including events that were themselves disabled (`enabled_on_start=0`). The selection was only `keep_detecting=1`; it did not filter by enabled state.
- Verification after replacement: 0 events remain with `keep_detecting=1`; 96 events now store `keep_detecting=0`.
- The patched app was force-stopped before the replacement to close the database cleanly. It remains stopped so it will reload the updated database on next launch.

## Recovery copies

- Local pre-change snapshots: `before-click_database` (with matching WAL/SHM) and `before-stopped-click_database`.
- Local changed/verified snapshots: `click_database-keep-detecting-disabled` and `after-click_database`.
- On-device backup: `files/click_database-before-disable-keep-detecting-20260815.db` inside the patched app's private storage.
