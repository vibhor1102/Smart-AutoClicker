# Smart AutoClicker - Feature & Bugfix Roadmap

This roadmap compiles findings from the analysis of 11 distinct forks of the `Smart-AutoClicker` codebase. It outlines proposed improvements, bugfixes, and features, comparing them against the up-to-date state of your maintained fork and providing a middle ground of both user-friendly and technical context.

---

## 1. Easy to Implement Features, Bugfixes, and Suggestions

These are low-effort, high-impact improvements, layout adjustments, and critical bugfixes that can be integrated quickly.

### 🐛 Critical Bugfixes
*   **Foreground Service Notification Cleanup (`ServiceNotificationController.kt`)**:
    *   *Detail:* Properly dismisses the persistent notification (`NotificationManager.cancel`) when the service is destroyed. Currently, on some Android versions, the notification stays in the status bar even after the service is stopped.
*   **Copy Scenario Dialog Crash Fix (`dialog_base_copy.xml` layout)**:
    *   *Detail:* Restores missing `android:layout_width` and `android:layout_height` parameters inside the `<include>` tag for `@layout/include_dialog_search_top_bar`. Defining an ID on an `<include>` tag without layout dimensions triggers a runtime `InflateException` crash on some devices.
*   **Event Error Mismatch Fix (`EditionState.kt` / priority sorting)**:
    *   *Detail:* Aligns the invalid item indicators list (`itemValidity`) with reordered/sorted events. Because events are sorted by priority in the scenario editor list flows, the parallel boolean validity list gets misaligned, resulting in error badges (red icons) appearing on the wrong events in the UI.

### 🎨 UI & UX Enhancements
*   **Action Number Indicators on Target Pins (`ClickBriefRenderer` / `SwipeBriefRenderer`)**:
    *   *Detail:* Draws the execution number (1, 2, 3, etc.) directly inside the floating target circles (rendered overlay pins) so you can track action execution order at a glance.
*   **Target Pin Long-Press Quick Action Menu (`ItemBriefView` / gesture detection)**:
    *   *Detail:* Incorporates a gesture listener (`GestureDetector`) on target pins to detect long-presses, opening an overlay configuration menu immediately without switching screens or blocking the background app.
*   **Compact Overlay Menu Card (`overlay_menu.xml` adjustments)**:
    *   *Detail:* Makes the floating overlay menu less obstructive by reducing button dimensions (from 40dp to 32dp width), adding 10% transparency (`android:alpha="0.9"`), and starting it at the left edge of the screen (`X = 0`) by default.
*   **Manual Coordinates Crop Input (`DetectionAreaCoordinatesDialog`)**:
    *   *Detail:* Introduces a coordinates entry dialog to let users manually type in precise bounding crop coordinates (Left, Top, Right, Bottom) for image matching, instead of relying only on touch drag handles.
*   **BottomSheet Drag-to-Dismiss (`OverlayDialog.kt`)**:
    *   *Detail:* Enables sliding down to close configuration sheets by setting `isDraggable = true` in the base overlay dialog wrapper.
*   **Reduce Main Menu UI Lag (`MainMenu` layout transitions)**:
    *   *Detail:* Removes sluggish layout transitions (`animateLayoutChanges` wrappers) when toggling play/stop button states to prevent delay or lag during rapid clicks.

### ⚙️ Backend, Logic & CI
*   **Dynamic Accent Text Contrast (`ColorUtils.calculateLuminance`)**:
    *   *Detail:* Calculates whether button text should be black or white depending on the background accent color's luminance (using `ColorUtils.calculateLuminance(color) < 0.5`), ensuring high accessibility and visibility.
*   **Group Event Prefix Toggles (database name prefixes)**:
    *   *Detail:* Enables turning event groups on or off in bulk using name prefix matches (e.g. toggle all actions starting with a certain string).
*   **Overwrite Duplicate Scenario Imports (`OnConflictStrategy.REPLACE`)**:
    *   *Detail:* Configures Room database scenario DAOs to overwrite duplicate records (`OnConflictStrategy.REPLACE` instead of `IGNORE`) on scenario imports or sync.
*   **Gradle Configuration Cache (`gradle.properties`)**:
    *   *Detail:* Enables the Gradle configuration cache (`org.gradle.configuration-cache=true`) to speed up compilation times for developers.

---

## 2. Features Requiring Moderate Development Effort

These features require deeper changes to layouts, database structures, or core engine flows, but offer significant utility.

### 📸 Overlay & Matching Enhancements
*   **Zoomable Screenshot Crop View (`ConditionSelectorView` canvas)**:
    *   *Detail:* Upgrades the crop view selector by capturing screenshots and rendering them on a zoomable/pannable canvas, allowing pixel-perfect bounding box selections.
*   **Draggable Target Coordinates (`ClickPositionHandleOverlay`)**:
    *   *Detail:* Displays a draggable handle overlay in the editor allowing you to drag target pin coordinates directly around the screen in real-time.
*   **Grid/List Offset-Repeat Matching (`ANCHORED_REPEAT` replacement)**:
    *   *Detail:* Repeats an image match shifted by a coordinate offset vector `(dx, dy)` up to `N` times (useful for scanning grids or lists) and translates action clicks/swipes by the offset values dynamically.
*   **Find All Occurrences on Screen (OpenCV multiple template matching)**:
    *   *Detail:* Extends JNI and OpenCV matching layers (`detectConditionMultiple`) to locate all occurrences of a target image on screen simultaneously (by invalidating matched regions recursively), executing actions on all matching coordinates.

### 🧠 Logic, Scripting & Engine Changes
*   **Collapsible Overlay Menu (`OverlayMenu.kt` handles)**:
    *   *Detail:* Implements a collapsible floating handle (16dp x 40dp) for the overlay menu, toggling visibility of the other buttons when clicked to save screen space during macro execution.
*   **Replace Protobuf Storage (`feature/smart-debugging` database migration)**:
    *   *Detail:* Migrates the debug logging module (`core/smart/debugging`) from Google Protobuf serialization to standard Room databases, reducing APK size and avoiding build version conflicts.
*   **Bot-Detection Bypass (Delays & coordinate randomization)**:
    *   *Detail:* Adds database columns (`randomize_radius`, `delay_before`, `delay_after`) and executes randomized delays and click position offsets to bypass anti-cheat/anti-macro detection.
*   **Clipboard-Based Fast Typing (`ClipboardManager` bypass)**:
    *   *Detail:* Speeds up text injection actions by copying strings to the clipboard and executing paste actions, bypassing character-by-character keyboard simulation.
*   **Media-Projection Screenshot Action**:
    *   *Detail:* Adds a step action that triggers a screenshot using media projection APIs and saves it to storage to audit macro execution failures.

---

## 3. Options for Consideration

These features are either highly complex, require external dependencies, or introduce architectural trade-offs that may not align with a lightweight clicker.

*   **Autonomous AI Agent (`AccessibilityNodeInfo` OODA loop)**:
    *   *Detail:* Parses layouts via accessibility nodes (`AccessibilityParser.kt`), sends goals to OpenAI's GPT-4o, and executes parsed action events (tap, type, scroll) dynamically.
*   **Tasker Plugin Integration (broadcast receiver hooks)**:
    *   *Detail:* Exposes scenario hooks to Tasker to start or stop scenarios based on system-level conditions.
*   **Screen & Battery State Event Triggers (database/system broadcasts)**:
    *   *Detail:* Starts macros based on system state changes, such as screen on/off, battery levels, or target app launches.
*   **Webhook Reporting (HTTP execution logs)**:
    *   *Detail:* Transmits macro completion or failure reports to a user-configured Webhook URL (e.g. Discord, Slack).

---

## 4. Ideas Most Likely to be Discardable

These ideas are too specific, duplicate existing features, or introduce poor architectural patterns.

*   **Hardcoded API Endpoints and Messenger Redirects (`oleholeh.store`)**:
    *   *Reason:* Custom server links and messenger fallbacks are specific to the fork author's setup and should be configurable rather than hardcoded.
*   **Shizuku-Based Native Touch Daemon (`gesture-helper` uinput daemon)**:
    *   *Reason:* Running a background daemon to inject low-level touch events via Shizuku is overly complex and only needed for games that actively block Android's accessibility service.
*   **Hardcoded Personalization Color Options (`PersonalizationOptions.kt`)**:
    *   *Reason:* Changing colors programmatically in individual menus makes the app look inconsistent; system themes (like Android DayNight or dynamic colors) should be used instead.
*   **Bypassing Trial Constraints & Paywalls (`RevenueRepository.kt` hacks)**:
    *   *Reason:* Removing billing restrictions is redundant. The clean F-Droid flavor in this repository is already hardcoded to be fully unlocked out of the box.
*   **Hardcoded Development Paths / Disabling NDK versioning**:
    *   *Reason:* Custom developer paths and disabling NDK builds break compatibility for other developers compiling the app.
*   **Pokémon GO Niche Automations (`Throwlet` berry-feeding)**:
    *   *Reason:* Hardcoded coordinate flows for feeding Pokémon buddies are too narrow for a general-purpose utility clicker.
