# Migration 19 to 20 enum-case filter

## Status

WIP — recorded separately from the unit-test quality work. No production code change has been made in this branch.

## Environment

- Branch: `wip/unit-test-gap-analysis`
- Database migration: `Migration19to20`
- Upstream issue: #990, item 9 (case-sensitivity in migration enum deserialization)
- Related maintainer commit: `0bfb4aba9c145f7c0f1daa94c0a3613fa47584b2` (`[#955] Fix various issues`)

## Reproduction

1. Create a version-19 database containing a `CHANGE_COUNTER` action and an `ON_COUNTER_REACHED` condition.
2. Change their persisted `type` values to lowercase: `change_counter` and `on_counter_reached`.
3. Run `Migration19to20`.
4. The counter values are not copied to their new numeric columns.

## Expected and observed

Expected: the migration should preserve the counter values for the case variants that the upstream fix intends to accept.

Observed: the test `migrate_counters_new_type_acceptsLowercasePersistedEnums` fails. The value is absent after migration.

## Confirmed cause

Commit `0bfb4ab` changed Kotlin enum conversion to `type.uppercase()`, but both migration readers first use SQL clauses that match only the uppercase enum spelling. SQLite therefore excludes lowercase rows before the Kotlin conversion can run.

## Suggested amendment

Make the two migration reader queries case-insensitive (for example, compare `UPPER(type)` with the enum name), then retain the lowercase migration regression test. Confirm the condition and action paths both preserve the values.

## Rejected interpretation

This is not a duplicate test-only failure: the current test describes the behaviour the upstream fix explicitly attempted to support. The implementation is incomplete.
