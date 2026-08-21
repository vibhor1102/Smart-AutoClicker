/* Copyright (C) 2026 Kevin Buzeau */
package com.buzbuz.smartautoclicker.core.smart.debugging.data.mapping

import com.buzbuz.smartautoclicker.core.smart.debugging.ConditionProfileMessageKt.conditionProfileEntry
import com.buzbuz.smartautoclicker.core.smart.debugging.conditionProfileMessage
import com.buzbuz.smartautoclicker.core.smart.debugging.debugReportMessage
import com.buzbuz.smartautoclicker.core.smart.debugging.domain.model.report.ConditionProfile
import com.buzbuz.smartautoclicker.core.smart.debugging.DebugReportMessage as ProtoDebugReportMessage

internal fun List<ConditionProfile>.toProtobuf(): ProtoDebugReportMessage =
    debugReportMessage {
        conditionProfileMessage = conditionProfileMessage {
            entries.addAll(this@toProtobuf.map { profile ->
                conditionProfileEntry {
                    conditionId = profile.conditionId
                    checkCount = profile.checkCount
                    fulfilledCount = profile.fulfilledCount
                    totalDurationNs = profile.totalDurationNs
                    minDurationNs = profile.minDurationNs
                    maxDurationNs = profile.maxDurationNs
                }
            })
        }
    }
