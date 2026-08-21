/*
 * Copyright (C) 2026 Kevin Buzeau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.buzbuz.smartautoclicker.core.processing.domain

/** Receives synchronous condition timings from the processing hot path. */
interface ConditionProfiler {

    /** Record one completed condition check. Implementations must return quickly and must not allocate per call. */
    fun recordConditionCheck(conditionId: Long, durationNs: Long, fulfilled: Boolean)
}
