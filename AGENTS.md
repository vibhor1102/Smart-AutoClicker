# Repository Instructions

Vibhor's maintained fork of Klick'r / Smart AutoClicker.

## Branch Architecture

`main` is a **rebase-based patch stack** on `upstream/master`. Run
`git log --oneline upstream/master..main` to see the fork delta. Commits
are ordered from least to most conflict-prone; the version bump is always
last.

`main` is release-only. **Development** happens on `dev`, `dev/*`, or
`feature/*` branches off `main`. Squash into one clean commit on `main` when
done.

**Upstream PRs** get their own `feature/<name>` branch based on
`upstream/master` (no fork infra/branding).

**Syncing upstream:**
```
git fetch upstream
git rebase --onto upstream/master <old-upstream-base> main
git push --force-with-lease origin main
```
Drop any commits already merged upstream during the rebase.

## Environment

- Java 21, Android SDK at `C:\Users\Vibhor\AppData\Local\Android\Sdk`.
- PowerShell from repo root on Windows.

## Build & Release

- Do not run routine Gradle verification locally; CI runs debug compile on push/PR.
- Use `.github/workflows/debug-apk.yml` for hosted patched debug APKs.
- Releases via `.github/workflows/release.yml` (manual trigger from `main` only).
- Do not commit keystores or signing secrets.
- GitHub releases restore signing secrets, verify the signed F-Droid APK, and
  publish artifacts. Upstream Play Store/nightly/test-report workflows are retired.

## Versioning

- Tags: `v<upstream>-patched.<N>` (e.g. `v3.5.1-patched.10`).
- `versionName` matches tag without `v`; increment `versionCode` per release.
- Version bump commit is always the last commit on `main`.

## Debugging

- Android 14 device available; use `platform-tools\adb.exe`.
- Build debug APKs locally only when device debugging requires it.
- Use debug builds only (separate `.debug` package); leave the main Patched app untouched.
- Set up adb/logcat yourself; the human only interacts with UI.
- Minimize test requests to risky areas and do not run full test suites unless asked.
