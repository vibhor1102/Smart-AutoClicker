# Repository Instructions

Vibhor's maintained fork of Klick'r / Smart AutoClicker.

## Branch Architecture

`main` is a rebase-based patch stack on `upstream/master`. Run
`git log --oneline upstream/master..main` to see the fork delta.

`main` is release-only. Development happens on `dev`, `feature/*`,
`wip/*`, or purpose-named rebase branches, then lands on `main` as a clean
patch stack. Keep version bump commits last.

Use `feature/<name>` branches based on `upstream/master` for upstream PRs
and keep fork branding/workflows out of those branches.

## Environment

- Java 21, Android SDK at `C:\Users\Vibhor\AppData\Local\Android\Sdk`.
- PowerShell from repo root on Windows.
- Use `agents-md-maintainer` skill for AGENTS.md edits.

## Build & Release

- Local compilation/APK builds are restricted to avoid RAM exhaustion.
- Scoped unit tests are allowed with `--no-daemon`; avoid full local sweeps.
- Use GitHub `Verify Build` (`verify.yml`) for full compile/test coverage.
- Use GitHub `Debug APK` (`debug-apk.yml`) for patched debug APKs.
- Patched debug package is `com.buzbuz.smartautoclicker.patched.debug`.
- Release workflow is `.github/workflows/release.yml` and runs from `main`.
- Do not commit keystores, signing secrets, downloaded artifacts, or temporary
  debug logging.

## Versioning

- Tags: `v<upstream>-patched.<N>` such as `v4.0.0-beta01-patched.1`.
- `versionName` matches the tag without `v`; increment `versionCode` per
  release.

## Debugging

- Android 14 device available; use `platform-tools\adb.exe`.
- Use debug builds only for device testing; leave the main Patched app alone.
- If debug install hits a signature mismatch, uninstall only `.patched.debug`.
- Set up adb/logcat yourself; the human only interacts with UI.
