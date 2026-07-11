# Capture image/color condition after overlay toggle

- Status: WIP source investigation.
- Report: after the selector hides the touch-capture overlay, screen content may change. Restoring capture/select mode still uses the pre-hide screenshot.
- Expected: restore should capture a fresh screenshot before crop/pixel selection.
- Secondary UX report: an intermediate crop-icon step may now be redundant because the selector itself can hide/re-enter capture mode.
## Source findings

- Both image and color pickers use the generic `OverlayMenu` eye control. It only toggles `screenOverlayView.visibility`; it does not notify the picker to refresh data.
- Image: after the first capture, `CaptureMenu` holds the bitmap inside `ImageSelectorView`. Restoring the overlay from its `ADJUST` state only makes that same view visible.
- Color: `ColorCaptureViewModel.pixelSelectionState` stores the bitmap. Restoring the overlay changes no UI state, so `PixelSelectorView` receives the old bitmap again.
- The image path has behaved this way since the eye control was changed in 2023, well before 4.0 beta releases.
- The color picker was added 2026-05-08 and already had this behavior in 4.0.0-beta01; beta03 did not add it. Beta03 only added tutorial-monitoring lines around the color capture code.
- Proposed narrow behavior: selector-specific handling of eye-toggle restoration should transition back into the existing screenshot-capturing state, wait for the menu/overlay to disappear, then deliver the fresh bitmap and restore selection mode. Preserve the current crop/pixel position where appropriate.
- The capture-button step is not purely legacy: it gives the person time to navigate the underlying app to the desired screen before Klick'r takes its first snapshot. Removing it means choosing a new automatic initial-capture moment and needs a product decision; it is separate from the stale-screenshot defect.
