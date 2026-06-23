# Smart AutoClicker - Feature & Bugfix Roadmap

This roadmap compiles findings from the analysis of 11 distinct forks of the `Smart-AutoClicker` codebase. It categorizes proposed improvements, bugfixes, and features into four tiers based on their actionability, development effort, and general value to the Klick'r application.

---

## 1. Easy to Implement Features, Bugfixes, and Suggestions

These are low-effort, high-impact improvements, layout adjustments, and critical bugfixes that can be integrated quickly.

### 🐛 Critical Bugfixes
*   **NativeDetector Bitmap Recycling Crash Fix** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* In `NativeDetector.kt` during template matching, if the region of interest matches the full screen, `screenRegion` is assigned directly to the source `screen` bitmap. Unconditionally calling `screenRegion.recycle()` destroys the active source bitmap, causing crashes on subsequent loops.
    *   *Fix:* Check object identities: `if (screenRegion !== screen) screenRegion.recycle()`.
*   **Foreground Service Notification Cleanup** ([YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Detail:* Explicitly call `notificationManager.cancel(NotificationIds.FOREGROUND_SERVICE_NOTIFICATION_ID)` inside the `destroyNotification()` routine of `ServiceNotificationController.kt`. This prevents persistent/ghost foreground notifications from lingering on some Android versions.
*   **Try Menu Key-Consumption Fix** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Detail:* Prevent try-matching overlays (`TryImageConditionOverlayMenu` / `TryEventOverlayMenu`) from incorrectly consuming device key events (e.g. volume keys) unless the event matches the specified scenario stop key.
*   **CopyDialog Layout Inflation Fix** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Restore missing layout constraint parameters in `<include>` layouts within `dialog_base_copy.xml` to prevent runtime inflation crashes.

### 🎨 UI & UX Enhancements
*   **Node Index on Pins** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Render the action order/index directly inside the circle target pin overlays (in `ClickBriefRenderer` / `SwipeBriefRenderer`) so users can track action execution order at a glance.
*   **Long-Press Context Menu on Pins** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Attach a `GestureDetector` to target pins so that a long-press immediately pops up editing choices/menus, bypassing full screen dialog transitions.
*   **Overlay Menu Card Adjustments** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Set `android:alpha="0.9"` on the overlay menu layout card for a semi-transparent look. Realign default starting coordinates to `X = 0` (left edge) to minimize visual obstruction. Reduce margins and button width from 40dp to 32dp for a more compact design.
*   **Numerical Coordinates Crop Input** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Implement `DetectionAreaCoordinatesDialog` to let users manually input bounding crop coordinates (Left, Top, Right, Bottom) rather than relying solely on touch drag handles.
*   **BottomSheet Drag-to-Dismiss** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Detail:* Configure Hilt/Material bottom sheets with `isDraggable = true` and map sheet dismissal to the overlay's back navigation.
*   **Disable Layout Transitions to Prevent Rapid Click UI Jank** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Remove `animateLayoutChanges` wrappers around play/stop actions in main menus, directly handling UI states via `.isEnabled = true` to avoid jank or double-click lag.

### ⚙️ Backend, Logic & CI
*   **Luminance-Based Contrast Contrast Utility** ([bguf6/Smart](https://github.com/bguf6/Smart)):
    *   *Detail:* Use `if (ColorUtils.calculateLuminance(color) < 0.5) Color.WHITE else Color.BLACK` to dynamically determine readable text color for overlay items or custom accent buttons.
*   **Prefix Event Toggles** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Support enabling or disabling groups of events in the execution engine by matching string prefixes (e.g. toggle all actions beginning with `Auto_`).
*   **OnConflictStrategy.REPLACE for Scenario Imports** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Configure Room DAOs to overwrite duplicate records (`OnConflictStrategy.REPLACE`) on imports rather than ignoring them.
*   **Gradle Configuration Cache** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Enable `org.gradle.configuration-cache=true` in `gradle.properties` to improve iteration and build execution times.

---

## 2. Features Requiring Moderate Development Effort

These features require deeper changes to layouts, database structures, or core engine flows, but are highly viable and offer significant utility.

### 📸 Overlay & Matching Enhancements
*   **Overlay-Based Action & Condition Testing** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Migrate "Try Action" and "Try Condition" validation flows from screen-blocking dialogs to lightweight overlays (`TryEventOverlayMenu` and `TryImageConditionOverlayMenu`). This allows developers to test image recognition live without obscuring the background app.
*   **Zoomable Bounding Box Crop Selector** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Update the `ConditionSelectorView` to capture a screenshot and support gesture panning/zooming while locking coordinates in absolute screen boundaries.
*   **Draggable Target Coordinates** ([YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Detail:* Add `ClickPositionHandleOverlay` to let users drag circular target pins around the screen to adjust click locations in real time, rather than deleting and re-clicking to reposition.
*   **Offset-Repeat & Split-Screen Detection** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Implement logic to matching conditions shifted iteratively by a translation vector `(dx, dy)` up to `N` times (useful for scanning grids/lists). Click coordinates are translated dynamically by the match offset.
*   **Multiple Template Matching** ([abewartech/Smart-AutoClicker](https://github.com/abewartech/Smart-AutoClicker)):
    *   *Detail:* Leverage JNI and OpenCV (`Detector::detectConditionMultiple`) to locate all occurrences of a template on screen meeting the threshold (by invalidating matched regions recursively), and execute actions on all matching coordinates.

### 🧠 Logic, Scripting & Engine Changes
*   **Collapsible Overlay Menu** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Implement a collapsing floating handle (16dp x 40dp) for the overlay menu. Short clicking the move icon toggles layout visibility (`GONE` / `VISIBLE`), while long pressing handles dragging.
*   **Bypass Protobuf Serialization for Debug Logging** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Replace the Protobuf Gradle build plugins and runtime serializations inside `:feature:smart-debugging` with standard Room databases and Kotlin data structures. This reduces size and eliminates compilation version conflicts.
*   **Traditional Coordinate Clicker (Dumb Mode)** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Port a coordinate-based clicker mode (DumbEngine) for users who want to run simple, non-image-matching macros without the resource overhead of image matching.
*   **Delay & Location Randomization** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Add database columns and execute delays (`delay_before`, `delay_after`) and click coordinate randomization (`randomize_radius`) to bypass macro-detection/bot-detection scripts.
*   **Clipboard-Based Fast Typing / Text Injection** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker) / [muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Speed up text entry by copying target strings directly to the system clipboard and executing paste commands, rather than simulating character-by-character keyboard inputs.
*   **Media-Projection Screenshot Action** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Create an action type that captures a screenshot of the display (requiring media projection permissions) and writes it to storage to audit failure points.

---

## 3. Options for Consideration

These features are either highly complex, require external dependencies, or introduce architectural trade-offs that may not align with a lightweight clicker.

*   **Autonomous LLM Agent Integration** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Scrapes active views using accessibility APIs (`AccessibilityParser.kt`), structures them into JSON format, and calls OpenAI's GPT-4o (`OpenAIClient.kt`) to determine actions (tap, scroll, type, back) to achieve high-level goals.
    *   *Trade-off:* High latency, requires internet connection, API costs, and safety concerns. Could be introduced as an experimental opt-in setting.
*   **Tasker Plugin Integration** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Expose scenario broadcast endpoints to integrate with Tasker tasks, allowing Klick'r to start scenarios based on system triggers or execute shell scripts.
    *   *Trade-off:* Relies heavily on Tasker app installation.
*   **Screen & Battery State Event Triggers** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Detail:* Add triggers on system broadcasts like charger connected, battery low, screen off, or target app launched (`TYPE_WINDOW_STATE_CHANGED`).
    *   *Trade-off:* Shifts Klick'r from an image clicker to a system automation tool (overlapping with Tasker/Llama).
*   **Base64 Keystore Restoration in CI** ([techted89/Smart-AutoClicker](https://github.com/techted89/Smart-AutoClicker)):
    *   *Detail:* Decode release signing keys directly from GitHub Base64 secrets instead of decrypting GPG keys.
    *   *Trade-off:* Simpler CI workflow, but slightly lower safety than GPG.
*   **Slack/Webhook Reporting** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Send macro completion/failure notifications, device parameters, or logs directly to a webhook.
    *   *Trade-off:* Niche utility suited only for device farms or emulator deployments.

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
