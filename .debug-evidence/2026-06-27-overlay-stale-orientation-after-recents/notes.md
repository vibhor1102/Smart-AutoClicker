# Stale overlay orientation after leaving Recents

## Reproduction observed

1. Open Android's Recents screen while the phone is in an intermediate portrait/landscape transition state.
2. Klick'r continues working while Recents is open.
3. Close Recents so the phone returns to full portrait orientation.

## Actual result

The system and launcher return to portrait, but Klick'r's overlay retains a landscape-style layout. Overlay content is clipped and extends beyond the right edge of the portrait screen.

## Captured device state

- Physical display: 1080 x 2400, rotation 0 (portrait).
- Klick'r virtual display: 1080 x 2400, rotation 0 (portrait).
- Debug package/process: `com.buzbuz.smartautoclicker.debug`.
- The virtual display had already received the correct portrait dimensions when evidence was captured. This points to stale layout state inside the overlay UI rather than stale Android or virtual-display dimensions.
- Screenshot: `screen.png`.

## Issue status

Observed once through an unusual Recents/orientation transition. The original state was reached accidentally and could not be reproduced afterward despite further attempts.

Do **not** file an upstream issue from this evidence. Preserve it locally in case a reliable reproduction is found later or another report provides the missing trigger.

## Build context

- Installed debug APK was built from `dev-4.0.0-beta03-fixes` at commit `f5e34dc8215a741ac19d1640f970b698ca1984d4`.
- The installed package reports `versionName=4.0.0-beta02`, `versionCode=20087`; the development branch had not updated the displayed version name.
