# Repository Instructions

This repository is Vibhor's maintained fork of Klick'r / Smart AutoClicker.
Keep changes easy to rebase or merge from upstream unless the task is explicitly
fork-only.

## Environment

- Use Java 21 for Gradle and Android builds.
- The local Android SDK is expected at `C:\Users\Vibhor\AppData\Local\Android\Sdk`.
- Prefer the SDK `platform-tools\adb.exe` when doing device work.
- Use PowerShell from the repository root on Windows.

## Build Tasks

- Do not run Gradle builds or compilations locally due to severe resource constraints.
- All verification compilations are run on GitHub Actions via `.github/workflows/verify.yml` automatically on push/PR.

## Signing

- Do not commit keystores, encrypted keystore blobs, passwords, or local signing
  property files.
- The public fork release is signed by GitHub Actions from repository secrets.
- The old local `smartautoclicker/smartautoclicker.jks` is retired and should not
  be treated as a release key.
- The tracked Play Store `google-services.json` is upstream/Play Store config and
  is not used for the public F-Droid fork release workflow.

## Versioning

- Preserve the upstream base version and add a fork patch suffix.
- Format tags as `v<upstream-version>-patched.<fork-patch>`, for example
  `v3.5.1-patched.5`.
- Match Android `versionName` to the tag without the leading `v`, for example
  `3.5.1-patched.5`.
- Increment `versionCode` for every public fork release.
- When upstream advances, update the base version first, then restart the fork
  patch counter for that base if appropriate.

## Releases

- Releases must be built and published using GitHub Actions compute rather than locally.
- Use `.github/workflows/release.yml` for public fork releases.
- Trigger it manually from GitHub Actions with the tag, release name, and notes.
- The workflow builds on GitHub-hosted runners, restores the release keystore
  from secrets, verifies the signed F-Droid release APK, creates the public
  GitHub release, and uploads APK/AAB assets.
- Upstream Play Store, nightly obfuscation, and test-report workflows are retired
  in this fork. Add them back only if the fork intentionally needs that lane.

## Upstream Hygiene

- Keep fork-only release, signing, and personalization changes isolated from
  upstream bugfix work.
- Before opening upstream PRs, check upstream contribution guidance and avoid
  bundling fork release infrastructure into upstream-facing branches.
