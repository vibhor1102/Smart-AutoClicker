# Rename field hidden when the keyboard opens

## Reproduction observed

### Intent action

1. Open an existing Intent action for editing.
2. Focus the action's Name field to rename it.
3. Leave the on-screen keyboard open.

### Scenario

1. Open an existing Scenario for editing.
2. Focus the Scenario name field to rename it.
3. Leave the on-screen keyboard open.

## Actual result

When the keyboard opens, the available overlay height shrinks but the editor's navigation/content area does not adapt correctly. The section below the name field moves over it, leaving the editable text almost entirely hidden.

- In the Intent editor, the **Simple / Advanced** selector overlaps the Name field.
- In the Scenario editor, the **Config** navigation row overlaps the Scenario name field.
- The issue therefore appears to affect the shared editor layout during text entry, rather than only the Intent action editor.

## Expected result

The focused name field should remain fully visible above the keyboard. The content below it should move, resize, or become scrollable without overlapping the field.

## Evidence

- `screen.png`: Intent action rename reproduction.
- `screen-current-rename.png`: Scenario rename reproduction.
- Debug package: `com.buzbuz.smartautoclicker.debug`.
- Build branch/commit: `dev-4.0.0-beta03-fixes` at `f5e34dc8215a741ac19d1640f970b698ca1984d4`.
- Installed manifest version: `versionName=4.0.0-beta02`, `versionCode=20087` (the development branch had not updated the displayed version name).
- Device: Android 14, portrait orientation, on-screen keyboard visible.

## Tooling caveat

After each screenshot, an Android UI-hierarchy dump was run. That inspection command temporarily interfered with Klick'r's accessibility overlay and caused it to close. The closure was a debugging-tool side effect and is not part of this app bug. Future captures of active Klick'r overlays should avoid UI-hierarchy dumps.

## Issue status

Reproduces consistently in at least two editors. Keep as temporary evidence until related keyboard/editor layout problems are grouped for upstream reporting.
