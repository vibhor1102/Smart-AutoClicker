# Migration 19→20 still drops counter values when enum type casing differs

## Summary

The case-normalisation change in `Migration19to20` is incomplete. Lowercase persisted action and condition types are filtered out by SQL before `type.uppercase()` reaches `valueOf()`.

## Reproduction

1. Start with a database at version 19 containing a `CHANGE_COUNTER` action and an `ON_COUNTER_REACHED` condition.
2. Persist the `type` strings as `change_counter` and `on_counter_reached`.
3. Upgrade through `Migration19to20`.

## Expected

The values should be migrated into `counter_operation_value` and `counter_value`, respectively.

## Actual

Both rows are excluded by the migration's SQL readers because their `WHERE type = ...` clauses use exact uppercase matching. The new `uppercase()` calls are consequently never reached.

## Cause and suggested fix

This affects the case-handling intended by #955 / commit `0bfb4aba9c145f7c0f1daa94c0a3613fa47584b2`. Make the two SQL filters case-insensitive (or select the relevant rows without an exact-case filter), then keep a regression test covering both action and condition values.

I can provide the focused failing unit test if useful.
