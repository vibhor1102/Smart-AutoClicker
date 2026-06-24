# Smart AutoClicker - Feature & Bugfix Roadmap

This roadmap compiles findings from the analysis of 11 distinct forks of the `Smart-AutoClicker` codebase. It outlines proposed improvements, bugfixes, and features, comparing them against the up-to-date state of your maintained fork and providing a middle ground of both user-friendly and technical context.

---

## 1. Easy to Implement Features, Bugfixes, and Suggestions

These are low-effort, high-impact improvements, layout adjustments, and critical bugfixes that can be integrated quickly.

### 🐛 Critical Bugfixes
*   **Foreground Service Notification Cleanup** ([YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Detail:* Properly dismisses the active foreground notification (`notificationManager.cancel(NotificationIds.FOREGROUND_SERVICE_NOTIFICATION_ID)`) and resets `notificationState = null` inside [ServiceNotificationController.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/android/src/main/java/com/buzbuz/smartautoclicker/core/android/notification/ServiceNotificationController.kt) when the service is destroyed. Currently, on some Android versions, the notification leaks in the status bar even after the service is stopped.
*   **Event Error Mismatch Fix** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Aligns the invalid item indicators list (`itemValidity`) with reordered/sorted events in [EditionState.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/feature/smart-config/src/main/java/com/buzbuz/smartautoclicker/feature/smart/config/domain/EditionState.kt#L80-L83). Currently, only `value` list is sorted, leaving `itemValidity` unsorted, causing red error badges to appear on the wrong events in the scenario editor list UI.

### 🎨 UI & UX Enhancements
*   **Action Number Indicators on Target Pins** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Draws the execution number (1, 2, 3, etc.) directly inside the floating target circles by passing the action index to `ClickDescription` / `SwipeDescription` and drawing it using `canvas.drawText` inside [ClickBriefRenderer.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/ui/src/main/java/com/buzbuz/smartautoclicker/core/ui/views/itembrief/renderers/ClickBriefRenderer.kt) and `SwipeBriefRenderer.kt`.
*   **Target Pin Long-Press Quick Action Menu** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Incorporates a gesture listener (`GestureDetector` on [ItemBriefView.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/ui/src/main/java/com/buzbuz/smartautoclicker/core/ui/views/itembrief/ItemBriefView.kt)) to detect long-presses. Exposes selective hit-testing `isPositionOverTarget` from the renderers on `ACTION_DOWN` to intercept touches only when directly over floating target pins, allowing background touches to pass through unobstructed.
*   **Compact Overlay Menu Card** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Shrinks the floating overlay menu (`overlay_menu.xml`) by reducing the button size style `overlay_menu_btn_size` from `48dp` to `36dp` or `40dp` in [dimens.xml](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/ui/src/main/res/values/dimens.xml#L65). Starts the menu at the left or right edge of the screen by default in [OverlayMenu.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/overlays/src/main/java/com/buzbuz/smartautoclicker/core/common/overlays/menu/OverlayMenu.kt#L741) instead of centering it, and applies dynamic transparency (e.g. `alpha = 0.4f`) when idle.
*   **Manual Coordinates Crop Input** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Introduces a coordinates entry dialog (`DetectionAreaCoordinatesDialog`) with numeric text input fields (`Left`, `Top`, `Right`, `Bottom`) to let users manually type in precise bounding crop coordinates for image matching in [ImageConditionDialog.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/feature/smart-config/src/main/java/com/buzbuz/smartautoclicker/feature/smart/config/ui/condition/screen/image/ImageConditionDialog.kt) instead of relying solely on touch drag handles.
*   **Collapsible Overlay Menu** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Adds a collapsible floating handle button to the overlay menu card. Toggling the button collapses all other buttons to `View.GONE`, leveraging the existing `OverlayMenuResizeController` in [OverlayMenu.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/overlays/src/main/java/com/buzbuz/smartautoclicker/core/common/overlays/menu/OverlayMenu.kt) and `animateLayoutChanges` to automatically shrink and animate the floating window bounds.
*   **Reduce Main Menu UI Lag** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Removes sluggish layout transitions (`animateLayoutChanges` wrappers in `MainMenu`) when toggling play/stop button states to prevent delay or lag during rapid clicks.
*   **Dynamic Accent Text Contrast** ([bguf6/Smart](https://github.com/bguf6/Smart)):
    *   *Detail:* Evaluates dynamic color luminance programmatically using `ColorUtils.calculateLuminance(color) < 0.5` in core UI style utilities. Dynamically forces white or black text/icon colors over dynamically generated dynamic background or accent colors (e.g., Material You themes) to ensure WCAG readability.

### ⚙️ Backend, Logic & CI
*   **Replace Protobuf Storage** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Replaces Google Protobuf serialization in the `:core:smart:debugging` module (used for short-lived session logging) with `kotlinx.serialization` (JSON or CBOR/ProtoBuf format), which is already configured and used in the project. Allows deleting the custom `buzbuz-protobuf` Gradle plugin and removes the heavy Google Protobuf runtime library from dependencies, saving APK size.
*   **Media-Projection Screenshot Action** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Introduces a new `SCREENSHOT` action type executed inside [ActionExecutor.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/processing/src/main/java/com/buzbuz/smartautoclicker/core/processing/data/processor/ActionExecutor.kt). Triggers screenshot retrieval directly from the active media-projection stream via `DisplayRecorder.takeScreenshot()` and compresses/saves it to storage, allowing automated macro execution audits.

---


## 2. Features Requiring Moderate Development Effort

These features require deeper changes to layouts, database structures, or core engine flows, but offer significant utility.

### 📸 Overlay & Matching Enhancements
*   **Zoomable Detection Area Selector (IN_AREA Picker)** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Integrates the existing `CaptureComponent` (responsible for panning/zooming gestures on screenshot canvases in `ImageSelectorView`) into the area selector overlay [AreaSelectorView.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/ui/src/main/java/com/buzbuz/smartautoclicker/core/ui/views/areaselector/AreaSelectorView.kt). Uses coordinate translation `selector.getSelectionArea(captureArea, zoomLevel)` to map selection coordinates back to screen-relative space, allowing pixel-perfect bounding box selections.
*   **Draggable Target Coordinates** ([YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Detail:* Upgrades [ItemBriefView.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/ui/src/main/java/com/buzbuz/smartautoclicker/core/ui/views/itembrief/ItemBriefView.kt)'s touch listener. On `ACTION_DOWN`, checks if the touch is within a coordinate pin target threshold (e.g. 48dp). If true, it enters a dragging state and updates coordinates dynamically on `ACTION_MOVE`, while keeping the warp-on-touch functionality when clicking outside the pin.
*   **Grid/List Offset-Repeat Matching** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Adds an offset vector configuration `(dx, dy)` and repeat count `N` to image search event verification (in `ConditionsVerifier.kt`). Automatically repeats template matching checks shifted by the offset vector to scan grids/lists and translates click/swipe positions dynamically. Requires an `EventEntity` Room database schema upgrade.
*   **Find All Occurrences on Screen** ([abewartech/Smart-AutoClicker](https://github.com/abewartech/Smart-AutoClicker)):
    *   *Detail:* Extends JNI and OpenCV matching layers (`detectConditionMultiple`) to return a flattened double array of all matches on the screen (by recursively invalidating matched regions in the C++ layer). Updates Kotlin verifiers and modifies `ActionExecutor` to execute gestures on all matched coordinates simultaneously or sequentially.

### 🧠 Logic, Scripting & Engine Changes
*   **Group Event Prefix Toggles** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Extends the event toggling system inside [ActionExecutor.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/smart/processing/src/main/java/com/buzbuz/smartautoclicker/core/processing/data/processor/ActionExecutor.kt) and `EventsState` to enable enabling/disabling groups of events by matching name prefixes (e.g., toggle all starting with a certain string). Requires a minor database migration to add prefix toggle columns to the action entities.
*   **Overwrite Duplicate Scenario Imports** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Implements programmatic name conflict checks in the scenario import repository layer ([BackupRepository.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/feature/backup/src/main/java/com/buzbuz/smartautoclicker/feature/backup/domain/BackupRepository.kt)). Instead of auto-generating database IDs and creating duplicates, the app will update child tables (events, conditions, counters) under the existing parent scenario ID or prompt the user with Overwrite / Rename options.
*   **Bot-Detection Bypass (Delays & coordinate randomization)** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Extends the global 5px / 5ms coordinate and timing offset randomized bounds in `RandomizerConfig.kt` to action-specific configurations. Adds `randomize_radius`, `delay_before`, and `delay_after` columns to `ActionEntity` with Room database migrations, applying randomized delays and offsets dynamically inside `ActionExecutor`.

---

## 3. Options for Consideration

These features are either highly complex, require external dependencies, or introduce architectural trade-offs that may not align with a lightweight clicker.

*   **Tasker Plugin Integration** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Suggests integrating external remote control. Because the codebase already exposes exported broadcast receivers in [NotificationActionsReceiver.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/feature/notifications/src/main/java/com/buzbuz/smartautoclicker/feature/notifications/receivers/NotificationActionsReceiver.kt) for Play/Pause/Stop actions and custom broadcast trigger receivers in `BroadcastsState`, a full Tasker plugin library integration is unnecessary. Instead, document these existing broadcast API endpoints for users. Smart scenario starts are restricted by Android's mandatory user-consent prompt for MediaProjection.
*   **Screen & Battery State Event Triggers** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Detail:* Triggers scenarios or event conditions when battery levels change or screen states transition. Predefined broadcast actions are already supported via `ON_BROADCAST_RECEIVED` in [BroadcastActions.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/android/src/main/java/com/buzbuz/smartautoclicker/core/android/intent/actions/BroadcastActions.kt#L22-L95). To fully satisfy the proposal, the system should support value-based level comparisons (e.g. Battery < 20%) by querying the system services inside `ConditionsVerifier`.
*   **Webhook Reporting** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Transmits macro completion or failure reports to a user-configured Webhook URL (e.g. Discord, Slack). This requires adding a user-configurable network reporting manager that hooks into session state changes in `DebugEngine.kt`, sending screenshots or JSON session logs.

---

## 4. Discarded

*   **BottomSheet Drag-to-Dismiss** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Reason:* Setting `isDraggable = true` in [OverlayDialog.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/overlays/src/main/java/com/buzbuz/smartautoclicker/core/common/overlays/dialog/OverlayDialog.kt#L114-L118) bypasses concrete unsaved modification checks and prompts in child classes (e.g., `ClickDialog.kt`), leading to potential data loss when swiping down. Additionally, enabling dragging creates vertical scrolling conflicts on form fields and lists.
*   **Clipboard-Based Fast Typing** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker) / [muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Reason:* The codebase already implements this in [TextExecutor.kt](file:///c:/Users/Vibhor/Scripts/Smart-AutoClicker/core/common/actions/src/main/java/com/buzbuz/smartautoclicker/core/common/actions/text/TextExecutor.kt#L31-L113) by attempting direct text insertion using `ACTION_SET_TEXT`, falling back immediately to `ACTION_PASTE` via clipboard copy-pasting for nodes that do not support direct injection. Key-by-key typing simulation is fragile and unnecessary.