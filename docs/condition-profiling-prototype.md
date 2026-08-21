# Condition profiling prototype

This prototype collects fixed-size aggregate timings for every condition check. Timing is disabled by default.

## ADB controls

Enable profiling before starting a scenario:

```text
adb shell settings put global smart_autoclicker_condition_profiling 1
```

Disable profiling:

```text
adb shell settings put global smart_autoclicker_condition_profiling 0
```

Show the current value:

```text
adb shell settings get global smart_autoclicker_condition_profiling
```

The setting is read once when detection starts. Restart detection after changing it.

## Retrieve the last profile

After detection stops, dump the CSV from the upstream debug package:

```text
adb exec-out run-as com.buzbuz.smartautoclicker.debug cat cache/ConditionProfile.csv
```

When the normal debug report is enabled, the same aggregate data is also appended to `DebugReportMessages.pb` as a
`ConditionProfileMessage`. CSV is a command-line view of the same end-of-session snapshot, not a separate collector.

Each row contains the condition database ID, check and fulfilment counts, total/minimum/maximum duration in
nanoseconds, and an integer average derived from the total and count.
