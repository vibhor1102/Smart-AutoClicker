# Orchestrator Agent Handoff: Roadmap Suggestion Deep Research

## Your Mission

You are an **Orchestrator Agent** responsible for performing deep research into each suggestion listed in the `roadmap.md` file on the `roadmap` branch of the Smart-AutoClicker project. Your goal is to **spawn one subagent per suggestion**, have each subagent thoroughly research its assigned suggestion (they're for research/exploration, with no file edits made by them), collect their findings, and then **update the roadmap.md** with refined descriptions and **re-categorize** each suggestion into the correct tier.

---

## Background Context

### How the Roadmap Was Created

1. **Fork Analysis**: 11 forked repositories of Klick'r / Smart-AutoClicker were analyzed by spawning one subagent per fork. Each subagent examined the fork's commit history to identify unique features, bugfixes, or enhancements added on top of the base upstream project.

2. **Initial Compilation**: The findings were compiled into a `roadmap.md` file on a dedicated `roadmap` branch, organizing suggestions into 4 tiers:
   - **#1**: Easy to implement, decent benefit
   - **#2**: Moderate development effort, significant utility
   - **#3**: Options for consideration (complex, external deps, or architectural trade-offs)
   - **#4**: Discard (irrelevant, already implemented, or harmful)

3. **Relevance Pass**: Since many forks were behind upstream, a second pass was done to check each suggestion against the **current state** of the maintained fork (which is fully up-to-date with upstream). Some suggestions were removed as irrelevant (e.g., fixes for bugs already resolved upstream).

4. **Description Refinement**: Descriptions were refined to balance **user-friendly explanations** with **technical details** (class names, file paths), since the document may also be shown to the main upstream maintainer.

### Key Discoveries Made During the Process

These are important findings that emerged during earlier analysis rounds:

- **"Zoomable Screenshot Crop View" → Renamed to "Zoomable Detection Area Selector (IN_AREA Picker)"**: The zoom/pan functionality **already exists** for the initial image capture screen (`ImageSelectorView.kt` / `CaptureComponent.kt`). The suggestion is specifically about the **detection area selection screen** (`ConditionAreaSelectorMenu.kt` / `AreaSelectorView.kt`), which is currently just a transparent rectangle over the live screen WITHOUT zoom capability.

- **"Copy Scenario Dialog Crash Fix" → Removed**: The copy dialog was completely rewritten in the 4.0.0 version, making the old crash fix irrelevant.

- **Several suggestions were removed** during the relevance pass because they fixed issues already resolved in upstream 4.x releases (e.g., notification channel fixes, database migration patches, etc.).

---

## Project Structure

- **Repository root**: `c:\Users\Vibhor\Scripts\Smart-AutoClicker`
- **Roadmap file**: `c:\Users\Vibhor\Scripts\Smart-AutoClicker\roadmap.md` (on `roadmap` branch)
- **Current branch**: You should work on the `roadmap` branch. Switch to it first: `git checkout roadmap`

### Key Codebase Areas to Know

| Area | Path | Purpose |
|------|------|---------|
| Accessibility Service | `feature/smart-debugging/` and `core/smart/processing/` | Main service, gesture/text execution |
| Overlay UI | `feature/smart-config/src/main/java/.../ui/` | Floating menu, overlay dialogs, bottom sheets |
| Overlay Menu | `feature/smart-config/.../MainMenu.kt` | The floating control panel |
| Image Matching | `core/smart/detection/` | OpenCV JNI-based image detection |
| Database | `core/smart/database/` | Room DAOs, entities, migrations |
| Domain Layer | `core/smart/domain/` | Business logic, repositories, edition state |
| Common UI | `core/common/ui/` | Shared views including `ImageSelectorView`, `CaptureComponent` |
| Area Selector | `feature/smart-config/.../areaselector/` | The IN_AREA detection area picker |
| Notifications | `core/common/android/` | `ServiceNotificationController.kt` |
| Import/Export | Look for backup/export related code in `core/smart/database/` | Scenario serialization |

---

## Your Workflow

### Step 1: Read the Roadmap
Switch to the `roadmap` branch and read `roadmap.md` to get the full list of suggestions (currently ~25 items across 3 tiers, plus any that should be moved to #4/Discard).

### Step 2: Design Subagent Tasks
For each suggestion, create a research subagent with a detailed prompt covering:

1. **Trace the origin**: Look at the forked repository mentioned in the suggestion. Search for the relevant commits/changes on GitHub.

2. **Verify against current codebase**: Search the current codebase to determine:
   - Is this already implemented (fully or partially)?
   - Has the relevant code changed so much that the suggestion no longer applies?
   - Are there similar/overlapping features already in place?

3. **Assess feasibility**: How hard would this be to implement? What files/modules would need changes? Are there architectural concerns?

4. **Assess need/benefit**: 
   - Does this solve a real user pain point?
   - How often would users encounter the issue or use this feature?
   - Does it align with the app's goal of being **intuitive and simple**?
   - Could it introduce complexity or confusion?

5. **Check environment changes**: Have Android API changes, library updates, or upstream refactors made this more or less relevant?

6. **Look for conflicts**: Would this suggestion conflict with other suggestions or existing features?

7. **Final recommendation**: Categorize into:
   - **#1**: Easy to implement and decent benefit
   - **#2**: Worth the effort (moderate complexity, good benefit)
   - **#3**: Complex or payoff uncertain — worth considering but not a priority
   - **#4**: Discard (irrelevant, already done, harmful, or too niche)

### Step 3: Execute Research

Spawn one subagent per suggestion and let them research in parallel. There are 24 suggestions currently after some further removal of undesirable ones, and spawn the 24 subagents for them altogether so that parallelism can be maximised.

### Step 4: Compile Results
After all subagents report back:
1. **Update descriptions** in `roadmap.md` with new findings (e.g., "partially exists", "blocked by X", "synergizes with Y").
2. **Re-categorize** suggestions that belong in a different tier based on research.
3. **Move truly irrelevant items** to a new **Section 4: Discarded** with a brief reason.
4. **Commit** the updated roadmap to the `roadmap` branch.

---

## Guiding Principles

> [!IMPORTANT]
> **The aim for the app is always to be intuitive and simple.** Any suggestion that adds significant user-facing complexity without proportional benefit should be downgraded or discarded.

- Prefer suggestions that fix real bugs or make existing workflows smoother.
- Features that add new paradigms (e.g., Tasker integration, webhooks, AI/LLM) should be scrutinized for whether they belong in a lightweight auto-clicker.
- Consider the upstream maintainer's perspective — would they accept this as a PR? Is it general-purpose or niche?
- Balance user-friendliness with technical accuracy in descriptions.

---

## The 25 Suggestions to Research

Read them from `roadmap.md` on the `roadmap` branch. Here's a quick index for reference:

### Tier 1 (Currently "Easy to Implement")
1. Foreground Service Notification Cleanup
2. Event Error Mismatch Fix
3. Action Number Indicators on Target Pins
4. Target Pin Long-Press Quick Action Menu
5. Compact Overlay Menu Card
6. Manual Coordinates Crop Input
7. BottomSheet Drag-to-Dismiss
8. Reduce Main Menu UI Lag
9. Dynamic Accent Text Contrast
10. Group Event Prefix Toggles
11. Overwrite Duplicate Scenario Imports
12. Gradle Configuration Cache

### Tier 2 (Currently "Moderate Development Effort")
13. Zoomable Detection Area Selector (IN_AREA Picker)
14. Draggable Target Coordinates
15. Grid/List Offset-Repeat Matching
16. Find All Occurrences on Screen
17. Collapsible Overlay Menu
18. Replace Protobuf Storage
19. Bot-Detection Bypass (Delays & coordinate randomization)
20. Clipboard-Based Fast Typing
21. Media-Projection Screenshot Action

### Tier 3 (Currently "Options for Consideration")
22. Tasker Plugin Integration
23. Screen & Battery State Event Triggers
24. Webhook Reporting

---

## Build & Test Constraints

The general constraints from AGENTS.md still apply

---

## Example Subagent Prompt Template

```
Research the "[SUGGESTION NAME]" suggestion from [FORK_URL].

Context: This is a suggestion found in a fork of Smart-AutoClicker (Klick'r). 
The suggestion proposes: [BRIEF DESCRIPTION]

Your research tasks:
1. Search GitHub/web for the fork's relevant commits implementing this change.
2. Search the current codebase (on the `roadmap` branch) to check:
   - Is this already implemented fully or partially?
   - Has the relevant code area changed significantly since the fork diverged?
   - Are there similar features that overlap with this suggestion?
3. Assess implementation feasibility:
   - What files/modules need changes?
   - How complex is the change?
   - Any architectural concerns?
4. Assess need/benefit:
   - What user problem does this solve?
   - How common is the problem?
   - Does it keep the app intuitive and simple?
5. Check for environment changes (Android API updates, library changes) 
   that affect relevance.
6. Final recommendation — categorize as:
   - #1: Easy to implement, decent benefit
   - #2: Worth the effort (moderate complexity, good benefit)
   - #3: Complex or payoff uncertain
   - #4: Discard (irrelevant, already done, harmful, or too niche)

Report your findings in a structured format with sections for each task above.
```
