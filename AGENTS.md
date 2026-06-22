# Repository Instructions

Vibhor's maintained fork of Klick'r / Smart AutoClicker.

## Branch Architecture

`main` is a **rebase-based patch stack** on `upstream/master`. Run
`git log --oneline upstream/master..main` to see the fork delta. Commits
are ordered from least to most conflict-prone; the version bump is always
last.

`main` is release-only. Development happens on `dev`, `feature/*`,
`wip/*`, or purpose-named rebase branches, then lands on `main` as a clean
patch stack.

Prefer `feature/*` or `wip/*` for new local work; local branch `dev` already
exists, so avoid `dev/*` unless `dev` is retired.

**Upstream PRs** get their own `feature/<name>` branch based on
`upstream/master` (no fork infra/branding).
Use a sibling worktree if the main checkout is dirty but upstream PR work is
needed.

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
- Use `agents-md-maintainer` skill for AGENTS.md edits.
- Clear the dummy `GITHUB_TOKEN` environment variable before running GitHub CLI (`gh`) commands to use the keyring credentials (e.g., prepending `$env:GITHUB_TOKEN=$null;` in PowerShell).

## Build & Release

- **Restricted Local Gradle/Compilation:** Local compilation or building APKs is
  prohibited to prevent RAM exhaustion. Running individual unit tests locally is
  permitted only when scoped to a specific subproject test task, such as
  `./gradlew :core:common:overlays:testFDroidDebugUnitTest`, and run with
  `--no-daemon` so the JVM exits immediately. Background daemons and global
  sweeps are forbidden.
- **Verification Workflow:** Push changes to a branch on GitHub to trigger the
  `Verify Build` (`verify.yml`) workflow, which compiles the codebase and runs
  the complete unit test suite.
- **Patched Debug APKs:** Use the `Debug APK` (`debug-apk.yml`) workflow for
  building patched debug APKs. Trigger it via GitHub CLI:
  `gh workflow run debug-apk.yml -f abi=arm64-v8a` or via the web UI.
- Debug APK signing uses the `DEBUG_KEYSTORE_BASE64` secret and forces
  `ANDROID_USER_HOME` to the restored keystore directory. Do not replace this
  with a generated/cached debug keystore; `adb install -r` depends on stable
  debug signing.
- For single-ABI debug builds, `debug-apk.yml` uploads the selected ABI APK
  plus verification text files, including `debug-signing-certs.txt`.
- Unless explicitly scoped to upstream PR work, debug/release builds mean this
  fork's patched APKs, not upstream APKs.
- Verify hosted debug artifacts before install; patched debug package is
  `com.buzbuz.smartautoclicker.patched.debug`.
- Releases via `.github/workflows/release.yml` (manual trigger from `main` only).
- Do not commit keystores or signing secrets.
- GitHub releases restore signing secrets, verify the signed F-Droid APK, and
  publish artifacts. Upstream Play Store/nightly/test-report workflows are
  retired.
- Do not commit downloaded artifacts, generated obfuscation outputs, or
  temporary debug logging unless explicitly intended.

## Versioning

- Tags: `v<upstream>-patched.<N>` such as `v4.0.0-beta01-patched.1`.
- `versionName` matches the tag without `v`; increment `versionCode` per
  release.
- Version bump commit is always the last commit on `main`.

## Debugging

- I have an Android 14 device. For debugging, use wireless debugging; the phone's IP is `192.168.1.170`, but the port refreshes every time and must be requested from the user. Sometimes ADB connects automatically since devices are already paired. ADB is provided in the current Windows laptop.
- For debugging, only use debug builds, made using appropriate GitHub Actions,
  to ensure no local resource-limitation failures.
- Don't run actual tests yourself; let the human run the desired tests and
  interact with UI. Your job is to set it up with any adb commands and state
  that's required.
- The main Patched app contains real world usage automations, leave those and
  that app untouched. Use the debug app with separate package name for
  debugging.
- Only use debugging whenever required, and prefer only requesting specific
  tests for the risky parts, to keep human testing to a reasonable level.
- Set up adb/logcat yourself and retrieve logs as required. The human role is
  to interact with the UI and judge whether it's correct, or say it's "done" so
  logs can be checked.
