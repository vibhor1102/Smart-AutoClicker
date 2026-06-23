# Smart AutoClicker - Feature & Bugfix Roadmap

This roadmap compiles findings from the analysis of 11 distinct forks of the `Smart-AutoClicker` codebase. It outlines proposed improvements, bugfixes, and features, tailored to be relevant to your maintained fork.

---

## 1. Easy to Implement Features, Bugfixes, and Suggestions

These are low-effort, high-impact improvements, layout adjustments, and critical bugfixes that can be integrated quickly.

### 🐛 Critical Bugfixes
*   **Foreground Service Notification Cleanup** ([YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Detail:* Properly dismisses the persistent notification when the auto-clicker service is turned off. Currently, on some Android versions, the notification stays in the status bar even after the service is stopped.
*   **Copy Scenario Dialog Crash Fix** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Fixes a layout error that causes the application to crash whenever you attempt to open the dialog to copy a scenario.
*   **Event Error Mismatch Fix** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Fixes a bug in the scenario editor where reordering events causes red error icons (badges) to display on the wrong events.

### 🎨 UI & UX Enhancements
*   **Action Number Indicators on Target Pins** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Draws the execution number (1, 2, 3, etc.) directly inside the floating target circles on the screen so you can track action execution order at a glance.
*   **Target Pin Long-Press Quick Action Menu** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Long-pressing a target pin on the screen opens an overlay settings menu immediately, letting you configure actions faster without switching screens.
*   **Compact Overlay Menu Card** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Makes the floating overlay menu less obstructive by reducing button sizes, adding 10% transparency, and starting it at the left edge of the screen by default.
*   **Manual Coordinates Crop Input** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Allows you to type in exact numerical coordinates (Left, Top, Right, Bottom) to define image search areas, instead of relying only on touch drag handles.
*   **BottomSheet Drag-to-Dismiss** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Detail:* Enables sliding down on configuration bottom sheets to close them, improving navigation feel.
*   **Reduce Main Menu UI Lag** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Removes sluggish layout transitions when toggling play/stop buttons to prevent delay or lag during rapid clicks.

### ⚙️ Backend, Logic & CI
*   **Dynamic Accent Text Contrast** ([bguf6/Smart](https://github.com/bguf6/Smart)):
    *   *Detail:* Calculates whether button text should be black or white depending on the background color's brightness, ensuring high visibility.
*   **Group Event Prefix Toggles** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Enables turning event groups on/off in bulk using prefix names (e.g. toggle all actions starting with "Farming_").
*   **Overwrite Duplicate Scenario Imports** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Automatically overwrites older local scenarios with newer versions when importing from external sources.
*   **Gradle Configuration Cache** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Speeds up project compilation time for developers.

---

## 2. Features Requiring Moderate Development Effort

These features require deeper changes to layouts, database structures, or core engine flows, but offer significant utility.

### 📸 Overlay & Matching Enhancements
*   **Zoomable Screenshot Crop View** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Improves the target image selector by letting you pinch-zoom and pan screenshots to capture pixel-perfect templates.
*   **Draggable Target Coordinates** ([YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Detail:* Allows you to drag target pin indicators around the screen in real-time to adjust click locations.
*   **Grid/List Offset-Repeat Matching** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Repeats an image match shifted by a coordinate offset (e.g., checking items in a vertical list or a grid) and translates clicks/swipes dynamically.
*   **Find All Occurrences on Screen** ([abewartech/Smart-AutoClicker](https://github.com/abewartech/Smart-AutoClicker)):
    *   *Detail:* Extends image detection to locate all occurrences of a target image on screen simultaneously (e.g., clicking all elements of a certain type).

### 🧠 Logic, Scripting & Engine Changes
*   **Collapsible Overlay Menu** ([timo33666/Klickr](https://github.com/timo33666/Klickr)):
    *   *Detail:* Allows folding the floating menu into a small vertical handle to free up screen space when not in use.
*   **Replace Protobuf Storage** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Replaces complex serialization libraries in the debug logging module with standard databases, decreasing APK size and simplifying builds.
*   **Bot-Detection Bypass (Randomization & Delays)** ([wchen17/Smart-AutoClicker](https://github.com/wchen17/Smart-AutoClicker)):
    *   *Detail:* Adds randomized click delays and coordinate offsets (radius) to bypass anti-cheat/anti-macro detection.
*   **Clipboard-Based Fast Typing** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker) / [muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Pastes text input directly using the clipboard instead of simulating character-by-character keyboard entries, speeding up typing.
*   **Screenshot Actions** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Adds a step action that takes a screenshot and saves it to log failures or document macro runs.

---

## 3. Options for Consideration

These features are either highly complex, require external dependencies, or introduce architectural trade-offs that may not align with a lightweight clicker.

*   **Autonomous AI Agent** ([Menwitz/TaskEngineV1](https://github.com/Menwitz/TaskEngineV1)):
    *   *Detail:* Connects to OpenAI (GPT-4o) using screen layouts to execute taps, swipes, and typing automatically based on a natural language goal (e.g., "open browser and search for x").
*   **Tasker Plugin Integration** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Detail:* Connects scenario triggers with Tasker to launch macros based on system state.
*   **Screen & Battery State Event Triggers** ([K-S-D-M/Smart-AutoClicker](https://github.com/K-S-D-M/Smart-AutoClicker)):
    *   *Detail:* Starts macros automatically when the screen turns on/off, battery level changes, or a specific app is opened.
*   **Webhook Reporting** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Detail:* Sends execution completion/failure reports to webhooks (like Discord or custom endpoints).

---

## 4. Ideas Most Likely to be Discardable

These ideas are too specific, duplicate existing features, or introduce poor architectural patterns.

*   **Hardcoded API Endpoints and Messenger Redirects** ([muslimmuda15/Smart-AutoClicker](https://github.com/muslimmuda15/Smart-AutoClicker)):
    *   *Reason:* Custom server links and messenger fallbacks are specific to the fork author's setup and should be configurable rather than hardcoded.
*   **Shizuku-Based Native Touch Daemon** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Reason:* Running a background daemon to inject low-level touch events via Shizuku is overly complex and only needed for games that actively block Android's accessibility service.
*   **Hardcoded Personalization Color Options** ([bguf6/Smart](https://github.com/bguf6/Smart)):
    *   *Reason:* Changing colors programmatically in individual menus makes the app look inconsistent; system themes (like Android DayNight or dynamic colors) should be used instead.
*   **Bypassing Trial Constraints & Paywalls** ([LeQuangMien10/Smart-AutoClicker](https://github.com/LeQuangMien10/Smart-AutoClicker) / [YanqingQQ/Smart-AutoClicker](https://github.com/YanqingQQ/Smart-AutoClicker)):
    *   *Reason:* Removing billing restrictions is redundant. The clean F-Droid flavor in this repository is already hardcoded to be fully unlocked out of the box.
*   **Hardcoded Development Paths / Disabling NDK versioning** ([abewartech/Smart-AutoClicker](https://github.com/abewartech/Smart-AutoClicker)):
    *   *Reason:* Custom developer paths and disabling NDK builds break compatibility for other developers compiling the app.
*   **Pokémon GO Niche Automations** ([nicospz/Smart-AutoClicker](https://github.com/nicospz/Smart-AutoClicker)):
    *   *Reason:* Specific coordinate flows for feeding Pokémon buddies are too narrow for a general-purpose utility clicker.
