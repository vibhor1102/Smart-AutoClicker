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
package com.buzbuz.smartautoclicker.core.common.overlays.menu.implementation.common

import android.os.Build
import android.view.View

import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.robolectric.annotation.Config

/** Test the [OverlayMenuAnimations] class. */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class OverlayMenuAnimationsTests {

    @Test
    fun startShowAnimation_whenAnimationEndIsNotCalled_forcesCompletion() {
        val animations = OverlayMenuAnimations()
        val postedRunnables = mutableListOf<Runnable>()
        val view = mockViewWithPostDelayed(postedRunnables)
        var animationEndCount = 0

        animations.startShowAnimation(view) { animationEndCount++ }

        assertTrue(animations.showAnimationIsRunning)

        postedRunnables.single().run()
        postedRunnables.single().run()

        assertFalse(animations.showAnimationIsRunning)
        assertEquals(1, animationEndCount)
    }

    @Test
    fun startHideAnimation_whenAnimationEndIsNotCalled_forcesCompletion() {
        val animations = OverlayMenuAnimations()
        val postedRunnables = mutableListOf<Runnable>()
        val view = mockViewWithPostDelayed(postedRunnables)
        var animationEndCount = 0

        animations.startHideAnimation(view) { animationEndCount++ }

        assertTrue(animations.hideAnimationIsRunning)

        postedRunnables.single().run()
        postedRunnables.single().run()

        assertFalse(animations.hideAnimationIsRunning)
        assertEquals(1, animationEndCount)
    }

    private fun mockViewWithPostDelayed(postedRunnables: MutableList<Runnable>): View {
        val view = Mockito.mock(View::class.java)
        Mockito.doAnswer { invocation ->
            postedRunnables.add(invocation.getArgument(0))
            true
        }.`when`(view).postDelayed(Mockito.any(Runnable::class.java), anyLong())

        return view
    }
}
