# Triage Notes - Patched App Matching Failure

## Environment
* **App package name:** `com.buzbuz.smartautoclicker.patched.debug`
* **Device:** Android 14 (Wireless debugging active)
* **Status:** FIXED / PR Submitted (Upstream PR #954)

## Observed Behavior
* Earlier releases experienced matching failures after ~10 minutes, accompanied by `ColorMatcher: screenCroppedColorMat is empty after cropping.` errors.

## Initial Hypothesis
* The native library's `requiresCorrection` trial lock/anti-tamper logic (DRM Timebomb) triggers after 10 minutes.

## The Plot Twist (Investigation & Log Findings)
* We restored the native requiresCorrection check and added frame-by-frame debug logs under `KlickrDRM` tag to compile and test on device.
* **Findings:**
  - The DRM check successfully **diffused immediately** on the very first frame check of the process (`DRM DIFFUSED: Tag 'com.buzbuz.smartautoclicker.patched' matches key prefix.` log printed, setting `scalingTimeUpdateMs = -1`).
  - So the DRM did **not** arm and did not play any role in the matching failure.
  - However, the `screenCroppedColorMat is empty after cropping.` logs **still printed continuously** in portrait orientation and **stopped instantly** as soon as the display config correctly rotated/resized to landscape orientation.
  
## Actual Root Cause
The root cause is a coordinate scaling logic bug in `ScalingManager` when starting a landscape scenario while the screen is in portrait configuration:
1. In `ScalingManager.kt`, `.ensureMinSize()` is called before `.coerceIn()`. If coordinates are out of bounds (which landscape coordinates like `2000` are when clamped to portrait width `540`), `.coerceIn` clamps them to the edge, creating a **degenerate rectangle of width/height 0** (e.g. `Rect(540, 200, 540, 300)`).
2. The JNI boundary check in `isRoiValidForMatching` allows `0`-dimension rectangles to pass.
3. OpenCV fails to crop a `0`-width matrix, leading to empty cropped warnings and permanent matching failure.

---

## Suggested Fixes (Implemented in PR #954)
1. Swapped the scaling order in `ScalingManager.kt` to run `.coerceIn` first, and then `.ensureMinSize()`. This guarantees the scaled area has at least `1x1` size.
2. Hardened `isRoiValidForMatching` in C++ (`color_matcher.cpp`, `template_matcher.cpp`, `text_matcher.cpp`) to explicitly reject any degenerate rectangles where `width <= 0 || height <= 0`.
