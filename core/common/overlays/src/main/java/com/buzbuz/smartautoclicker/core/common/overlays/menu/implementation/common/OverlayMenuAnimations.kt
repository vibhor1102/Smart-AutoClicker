/*
 * Copyright (C) 2024 Kevin Buzeau
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
package com.buzbuz.smartautoclicker.core.common.overlays.menu.implementation.common

import android.util.Log
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator

import androidx.core.view.children
import com.buzbuz.smartautoclicker.core.base.Dumpable

import com.buzbuz.smartautoclicker.core.base.extensions.setListener
import java.io.PrintWriter

internal class OverlayMenuAnimations : Dumpable {

    /** Animation for showing the menu. */
    private val showOverlayMenuAnimation: Animation = AlphaAnimation(0f, 1f).apply {
        duration = SHOW_ANIMATION_DURATION_MS
        interpolator = DecelerateInterpolator()
    }
    /** Animation for showing the overlayView. */
    private val showOverlayViewAnimation: Animation = AlphaAnimation(0f, 1f).apply {
        duration = SHOW_ANIMATION_DURATION_MS
        interpolator = DecelerateInterpolator()
    }
    var showAnimationIsRunning: Boolean = false
        private set
    private var showAnimationRequestId: Int = 0

    /** Animation for hiding the menu. */
    private val hideOverlayMenuAnimation: Animation = AlphaAnimation(1f, 0f).apply {
        duration = DISMISS_ANIMATION_DURATION_MS
        interpolator = DecelerateInterpolator()
    }
    /** Animation for showing the overlayView. */
    private val hideOverlayViewAnimation: Animation = AlphaAnimation(1f, 0f).apply {
        duration = DISMISS_ANIMATION_DURATION_MS
        interpolator = DecelerateInterpolator()
    }
    var hideAnimationIsRunning: Boolean = false
        private set
    private var hideAnimationRequestId: Int = 0

    fun startShowAnimation(view: View, overlayView: View? = null, onAnimationEnded: () -> Unit) {
        if (showAnimationIsRunning) return

        Log.d(TAG, "Start show animation on view ${view} with visibility ${view.visibility}")

        showAnimationIsRunning = true
        val requestId = ++showAnimationRequestId
        fun finishShowAnimation() {
            if (!showAnimationIsRunning || requestId != showAnimationRequestId) return

            showAnimationIsRunning = false
            onAnimationEnded()
        }

        showOverlayMenuAnimation.setListener(
            end = {
                Log.d(TAG, "Show animation ended")
                finishShowAnimation()
            }
        )

        if (hideAnimationIsRunning) {
            Log.d(TAG, "Hide animation is running, stopping it first.")
            hideOverlayMenuAnimation.cancel()
            hideOverlayViewAnimation.cancel()
            hideAnimationIsRunning = false
            hideAnimationRequestId++
        }

        view.measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY)
        view.startAnimation(showOverlayMenuAnimation)
        if (overlayView is ViewGroup && overlayView.childCount == 1) {
            overlayView.children.first().startAnimation(showOverlayViewAnimation)
        }
        view.postDelayed({
            if (showAnimationIsRunning && requestId == showAnimationRequestId) {
                Log.w(TAG, "Show animation timed out, forcing completion.")
                showOverlayMenuAnimation.cancel()
                showOverlayViewAnimation.cancel()
                finishShowAnimation()
            }
        }, SHOW_ANIMATION_TIMEOUT_MS)
    }

    fun startHideAnimation(view: View, overlayView: View? = null, onAnimationEnded: () -> Unit) {
        if (hideAnimationIsRunning) return

        Log.d(TAG, "Start hide animation")

        hideAnimationIsRunning = true
        val requestId = ++hideAnimationRequestId
        fun finishHideAnimation() {
            if (!hideAnimationIsRunning || requestId != hideAnimationRequestId) return

            hideAnimationIsRunning = false
            onAnimationEnded()
        }

        hideOverlayMenuAnimation.setListener(
            end = {
                Log.d(TAG, "Hide animation ended")
                finishHideAnimation()
            }
        )

        if (showAnimationIsRunning) {
            Log.d(TAG, "Show animation is running, stopping it first.")

            showOverlayMenuAnimation.cancel()
            showOverlayViewAnimation.cancel()
            showAnimationIsRunning = false
            showAnimationRequestId++
        }

        view.startAnimation(hideOverlayMenuAnimation)
        if (overlayView is ViewGroup && overlayView.childCount == 1) {
            overlayView.children.first().startAnimation(hideOverlayViewAnimation)
        }
        view.postDelayed({
            if (hideAnimationIsRunning && requestId == hideAnimationRequestId) {
                Log.w(TAG, "Hide animation timed out, forcing completion.")
                hideOverlayMenuAnimation.cancel()
                hideOverlayViewAnimation.cancel()
                finishHideAnimation()
            }
        }, DISMISS_ANIMATION_TIMEOUT_MS)
    }

    override fun dump(writer: PrintWriter, prefix: CharSequence) {
        writer.append(prefix)
            .append("showIsRunning=$showAnimationIsRunning; ")
            .append("hideIsRunning=$hideAnimationIsRunning; ")
            .println()
    }
}

/** Duration of the show overlay menu animation. */
private const val SHOW_ANIMATION_DURATION_MS = 250L
/** Duration of the dismiss overlay menu animation. */
private const val DISMISS_ANIMATION_DURATION_MS = 150L
/** Grace period for animations that never report their completion. */
private const val ANIMATION_TIMEOUT_GRACE_MS = 250L
private const val SHOW_ANIMATION_TIMEOUT_MS = SHOW_ANIMATION_DURATION_MS + ANIMATION_TIMEOUT_GRACE_MS
private const val DISMISS_ANIMATION_TIMEOUT_MS = DISMISS_ANIMATION_DURATION_MS + ANIMATION_TIMEOUT_GRACE_MS
/** Tag for logs */
private const val TAG = "OverlayMenuAnimations"
