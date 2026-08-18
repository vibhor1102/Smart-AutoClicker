# 32-second reproduction

- User changed the `Battle Machine or Flying Machine` wait to 32 seconds, then ran the scenario again.
- Current database confirms `pauseDuration=32100` ms for action 88 (`Wait`) in event 20.

## Report timeline

| Report time | Event | Frame |
|---:|---|---:|
| 0.695 s | Not Found Match (193) | 10 |
| 1.812 s | Not Found Match (193) | 30 |
| 2.898 s | Not Found Match (193) | 48 |
| 36.262 s | Battle Machine or Flying Machine (20) | 66 |
| 36.605 s | If Heroes Undeployed (27) | 66 |
| 38.325 s | Deploy Night Witch but everywhere (28) | 68 |
| 38.452 s | event 22 | 70 |

## Finding

- This confirms intended execution, not a skipped wait.
- The report creates the entry for event 20 only after *all* its actions finish. Its 32.1-second wait therefore appears in the long gap before event 20 is written: from 2.898 s/frame 48 to 36.262 s/frame 66 is 33.364 seconds, but only 18 frames advanced. The preceding `Not Found Match` iterations advance about 18–20 frames every 1.1 seconds, so frame 66 was captured around 4 seconds and event 20 then held that frame during its 32.1-second wait.
- The following event 27 occurs 343 ms later on the same frame because event 20's final action enables it, `keep_detecting=true` continues the same loop, and the engine does not request a new capture after a pause. This is expected under the present design.
- The first new frame after that processing loop is frame 68, used by event 28 at 38.325 seconds.

## Interpretation

- The report timestamp is an action-completion timestamp, not a detection timestamp. It cannot directly tell the exact instant event 20 first matched.
- A Wait is useful to delay actions/the scenario loop. It is not a “wait for a new screenshot” action. A separate design change would be needed if event 27 must be evaluated on a fresh screen state after the delay.
