# Smart AutoClicker - Feature & Bugfix Roadmap

This roadmap compiles findings from the analysis of 11 distinct forks of the `Smart-AutoClicker` codebase. It categorizes proposed improvements, bugfixes, and features into four tiers, comparing them against the current up-to-date state of **our maintained fork (`vibhor1102/Smart-AutoClicker`)** to determine their relevance.

---

## 1. Easy to Implement Features, Bugfixes, and Suggestions

These are low-effort, high-impact improvements, layout adjustments, and critical bugfixes that can be integrated quickly.

### 🐛 Critical Bugfixes
*   **[RELEVANT] Foreground Service Notification Cleanup** ([YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Detail:* Explicitly call `notificationManager.cancel(NotificationIds.FOREGROUND_SERVICE_NOTIFICATION_ID)` inside the `destroyNotification()` routine of `ServiceNotificationController.kt`. This prevents persistent/ghost foreground notifications from lingering on some Android versions.
    *   *Status:* Still missing in our codebase; the notification remains active on service destruction until manually cleared on some devices.
*   **[RELEVANT] CopyDialog Layout Inflation Fix** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Restore missing `android:layout_width` and `android:layout_height` parameters inside the `<include>` tag for `@layout/include_dialog_search_top_bar` within `dialog_base_copy.xml` (in `:core:common:overlays`). Specifying an ID on an `<include>` tag without layout dimensions causes runtime inflation crashes on some Android OS versions.
    *   *Status:* Still missing in our codebase; this is an active crash risk.
*   **[RELEVANT] Event Validity Alignment Bugfix** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Align event validity indices (`itemValidity`) with reordered/sorted lists in `EditionState.kt`. Because events are sorted by priority during scenario editor list state updates, the parallel boolean list of invalid item states gets misaligned, resulting in error badges appearing on the wrong events in the UI.
    *   *Status:* Still missing in our codebase; sorting event lists leaves parallel validity states out of sync.
*   **[IRRELEVANT] NativeDetector Bitmap Recycling Crash Fix** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Checking object identities: `if (screenRegion !== screen) screenRegion.recycle()` to avoid recycling the main source screen bitmap.
    *   *Status:* Irrelevant. Our codebase handles all cropping and template-matching in C++ OpenCV, passing crop boundaries directly to JNI without creating or recycling intermediate sub-bitmaps in Kotlin.
*   **[IRRELEVANT / ALREADY SOLVED] Try Menu Key-Consumption Fix** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Detail:* Prevent try-matching overlays (`TryImageConditionOverlayMenu` / `TryEventOverlayMenu`) from consuming device volume-down key events unless they match the scenario stop key.
    *   *Status:* Already solved upstream in our codebase. Both classes already return `false` if `!keyEvent.isStopScenarioKey()`.

### 🎨 UI & UX Enhancements
*   **[RELEVANT] Node Index on Pins** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Render the action order/index directly inside the circle target pin overlays (in `ClickBriefRenderer` / `SwipeBriefRenderer`) so users can track action execution order at a glance.
    *   *Status:* Still missing; our renderer currently only draws solid circles.
*   **[RELEVANT] Long-Press Context Menu on Pins** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Attach a `GestureDetector` to target pins so that a long-press immediately pops up editing choices/menus, bypassing full screen dialog transitions.
    *   *Status:* Still missing; `ItemBriefView` currently handles all touch events as simple, non-differentiated coordinates.
*   **[RELEVANT] Overlay Menu Card Adjustments** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Set `android:alpha="0.9"` on the overlay menu layout card for a semi-transparent look. Realign default starting coordinates to `X = 0` (left edge) to minimize visual obstruction. Reduce margins and button width from 40dp to 32dp for a more compact design.
    *   *Status:* Still missing; menus use full opacity, default center alignment, and standard margins.
*   **[RELEVANT] Numerical Coordinates Crop Input** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Implement `DetectionAreaCoordinatesDialog` to let users manually input bounding crop coordinates (Left, Top, Right, Bottom) rather than relying solely on touch drag handles.
    *   *Status:* Still missing; our crop picker does not support manual coordinates entry.
*   **[RELEVANT] BottomSheet Drag-to-Dismiss** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Detail:* Configure bottom sheets with `isDraggable = true` in `OverlayDialog.kt`.
    *   *Status:* Still missing; `isDraggable = false` is currently hardcoded in `OverlayDialog.kt`.
*   **[RELEVANT] Disable Layout Transitions to Prevent Rapid Click UI Jank** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Remove `animateLayoutChanges` wrappers around play/stop actions in main menus, directly handling UI states via `.isEnabled = true` to avoid jank or double-click lag.
    *   *Status:* Still missing; layout transitions are still present.

### ⚙️ Backend, Logic & CI
*   **[RELEVANT] Luminance-Based Contrast Contrast Utility** ([bguf6/Smart](https://github.com/bguf6/Smart)):
    *   *Detail:* Use `if (ColorUtils.calculateLuminance(color) < 0.5) Color.WHITE else Color.BLACK` to dynamically determine readable text color for overlay items or custom accent buttons.
    *   *Status:* Still missing; contrast values are not calculated dynamically.
*   **[RELEVANT] Prefix Event Toggles** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Support enabling or disabling groups of events in the execution engine by matching string prefixes (e.g. toggle all actions beginning with `Auto_`).
    *   *Status:* Still missing.
*   **[RELEVANT] OnConflictStrategy.REPLACE for Scenario Imports** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Configure Room DAOs to overwrite duplicate records (`OnConflictStrategy.REPLACE`) on imports rather than ignoring them.
    *   *Status:* Still missing.
*   **[RELEVANT] Gradle Configuration Cache** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Enable `org.gradle.configuration-cache=true` in `gradle.properties` to improve iteration and build execution times.
    *   *Status:* Still missing.

---

## 2. Features Requiring Moderate Development Effort

These features require deeper changes to layouts, database structures, or core engine flows, but are highly viable and offer significant utility.

### 📸 Overlay & Matching Enhancements
*   **[RELEVANT] Zoomable Bounding Box Crop Selector** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Update the `ConditionSelectorView` to capture a screenshot and support gesture panning/zooming while locking coordinates in absolute screen boundaries.
    *   *Status:* Still missing.
*   **[RELEVANT] Draggable Target Coordinates** ([YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Detail:* Add `ClickPositionHandleOverlay` to let users drag circular target pins around the screen to adjust click locations in real time, rather than deleting and re-clicking to reposition.
    *   *Status:* Still missing.
*   **[RELEVANT] Offset-Repeat & Split-Screen Detection** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Implement logic to matching conditions shifted iteratively by a translation vector `(dx, dy)` up to `N` times (useful for scanning grids/lists). Click coordinates are translated dynamically by the match offset.
    *   *Status:* Still missing.
*   **[RELEVANT] Multiple Template Matching** ([abewartech/Smart-AutoClicker](https://github.com/abewartech/Smart-AutoClicker)):
    *   *Detail:* Leverage JNI and OpenCV (`Detector::detectConditionMultiple`) to locate all occurrences of a template on screen meeting the threshold (by invalidating matched regions recursively), and execute actions on all matching coordinates.
    *   *Status:* Still missing.
*   **[IRRELEVANT / ALREADY SOLVED] Overlay-Based Action & Condition Testing** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Migrate "Try Action" and "Try Condition" validation flows from screen-blocking dialogs to lightweight overlays.
    *   *Status:* Already solved. Our codebase already utilizes overlay-based try menus (`TryEventOverlayMenu` and `TryImageConditionOverlayMenu`) which run validation in real time on top of target apps.

### 🧠 Logic, Scripting & Engine Changes
*   **[RELEVANT] Collapsible Overlay Menu** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Implement a collapsing floating handle (16dp x 40dp) for the overlay menu. Short clicking the move icon toggles layout visibility (`GONE` / `VISIBLE`), while long pressing handles dragging.
    *   *Status:* Still missing.
*   **[RELEVANT] Bypass Protobuf Serialization for Debug Logging** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Replace the Protobuf Gradle build plugins and runtime serializations inside `:feature:smart-debugging` with standard Room databases and Kotlin data structures. This reduces size and eliminates compilation version conflicts.
    *   *Status:* Still missing; our debug module (`core/smart/debugging`) still uses `.proto` schemas and the protobuf generator plugin.
*   **[RELEVANT] Delay & Location Randomization** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Add database columns and execute delays (`delay_before`, `delay_after`) and click coordinate randomization (`randomize_radius`) to bypass macro-detection/bot-detection scripts.
    *   *Status:* Still missing; `ActionEntity` lacks columns for delays or randomization radius.
*   **[RELEVANT] Clipboard-Based Fast Typing / Text Injection** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker) / [muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Speed up text entry by copying target strings directly to the system clipboard and executing paste commands, rather than simulating character-by-character keyboard inputs.
    *   *Status:* Still missing.
*   **[RELEVANT] Media-Projection Screenshot Action** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Create an action type that captures a screenshot of the display (requiring media projection permissions) and writes it to storage to audit failure points.
    *   *Status:* Still missing.
*   **[IRRELEVANT / ALREADY SOLVED] Traditional Coordinate Clicker (Dumb Mode)** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Port a coordinate-based clicker mode (DumbEngine) for users who want to run simple macros.
    *   *Status:* Already solved. Our codebase already features a complete coordinate-based macro module (`core:dumb` and `:feature:dumb-config`), leaving this fork suggestion fully redundant.

---

## 3. Options for Consideration

These features are either highly complex, require external dependencies, or introduce architectural trade-offs that may not align with a lightweight clicker.

*   **[RELEVANT] Autonomous LLM Agent Integration** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Scrapes active views using accessibility APIs, structures them into JSON format, and calls OpenAI's GPT-4o to determine actions (tap, scroll, type, back) to achieve high-level goals.
    *   *Status:* Still missing (remains a highly experimental proposal).
*   **[RELEVANT] Tasker Plugin Integration** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Expose scenario broadcast endpoints to integrate with Tasker tasks.
    *   *Status:* Still missing.
*   **[RELEVANT] Screen & Battery State Event Triggers** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Detail:* Add triggers on system broadcasts like charger connected, battery low, screen off, or target app launched.
    *   *Status:* Still missing.
*   **[RELEVANT] Slack/Webhook Reporting** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Send macro completion/failure notifications, device parameters, or logs directly to a webhook.
    *   *Status:* Still missing.
*   **[ALREADY SOLVED IN THIS FORK] Base64 Keystore Restoration in CI** ([techted89/Smart-AutoClicker](https://github.com/techted89/Smart-AutoClicker)):
    *   *Detail:* Decode release signing keys directly from GitHub Base64 secrets instead of decrypting GPG keys.
    *   *Status:* Already implemented. Our repository already utilizes base64-encoded keystore secrets (`DEBUG_KEYSTORE_BASE64` and release signing configurations) in its CI workflows.

---

## 4. Ideas Most Likely to be Discardable

These ideas are too specific, duplicate existing features, or introduce poor architectural patterns.

*   **Hardcoded API Endpoints and messenger redirects** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Reason:* Custom APIs (`oleholeh.store`) and hardcoded fallback package links for WhatsApp/Telegram are store-specific and should be replaced by generic endpoint configuration.
*   **Shizuku-Based Native Touch Daemon (`gesture-helper`)** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Reason:* Running a background C++ daemon writing directly to `/dev/uinput` via Shizuku root bypass is overly complex. It is only useful to bypass anti-cheat systems in specific secure environments.
*   **Hardcoded Personalization Color Options** ([bguf6/Smart](https://github.com/bguf6/Smart)):
    *   *Reason:* Overriding colors programmatically in individual views (rather than using standard Android DayNight themes) is an anti-pattern.
*   **Bypassing Trial Constraints & Paywalls** ([LeQuangMien10/Smart-AutoClicker](https://github.com/LeQuangMien10/Smart-AutoClicker) / [YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Reason:* Bypassing Play Store billing restrictions is redundant. The clean F-Droid flavor in this repository is already hardcoded to `UserBillingState.PURCHASED`, leaving it fully unlocked by default.
*   **Hardcoded Development Paths / Disabling NDK versioning** ([abewartech/Smart-AutoClicker](https://github.com/abewartech/Smart-AutoClicker)):
    *   *Reason:* Local Java home paths (`jbr` references) and commented out NDK compiler parameters break deterministic building across different development systems.
*   **Throwlet Game-Specific Automations** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Reason:* Hardcoded coordinate flows for Pokémon GO buddy feeding are too narrow for a general-purpose automation application.
