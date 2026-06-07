/*
 * Copyright (C) 2026 Kevin Buzeau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.buzbuz.smartautoclicker.core.processing.domain

import android.graphics.Point
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManualClickRepository @Inject constructor() {

    private val _captureEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val captureEnabled: StateFlow<Boolean> = _captureEnabled.asStateFlow()

    private val lock = Any()
    private var captureRequested: Boolean = false
    private var capturePausedCount: Int = 0
    private var pendingTap: Point? = null

    fun setCaptureRequested(enabled: Boolean) {
        synchronized(lock) {
            captureRequested = enabled
            updateCaptureState()
        }
    }

    fun submitTap(position: Point) {
        synchronized(lock) {
            if (!_captureEnabled.value) return
            pendingTap = position
        }
    }

    fun consumeTap(): Point? =
        synchronized(lock) {
            pendingTap.also { pendingTap = null }
        }

    fun clear() {
        synchronized(lock) {
            pendingTap = null
            captureRequested = false
            capturePausedCount = 0
            updateCaptureState()
        }
    }

    suspend fun <T> withCapturePaused(block: suspend () -> T): T {
        val shouldPause = synchronized(lock) {
            captureRequested
        }
        if (!shouldPause) {
            return block()
        }

        val isFirstPause = synchronized(lock) {
            val first = capturePausedCount == 0
            capturePausedCount++
            updateCaptureState()
            first
        }

        if (isFirstPause) {
            delay(CAPTURE_PAUSE_DELAY_MS)
        }

        return try {
            block()
        } finally {
            val isLastResume = synchronized(lock) {
                capturePausedCount = (capturePausedCount - 1).coerceAtLeast(0)
                updateCaptureState()
                capturePausedCount == 0
            }
            if (isLastResume) {
                delay(CAPTURE_RESUME_DELAY_MS)
            }
        }
    }

    private fun updateCaptureState() {
        _captureEnabled.value = captureRequested && (capturePausedCount == 0)
    }
}

private const val CAPTURE_PAUSE_DELAY_MS = 50L
private const val CAPTURE_RESUME_DELAY_MS = 100L
