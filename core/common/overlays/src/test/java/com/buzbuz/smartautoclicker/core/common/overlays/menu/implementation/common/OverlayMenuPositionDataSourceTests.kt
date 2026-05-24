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

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.`when` as mockWhen

/** Test the [OverlayMenuPositionDataSource] class. */
class OverlayMenuPositionDataSourceTests {

    @Mock private lateinit var mockContext: Context
    @Mock private lateinit var mockSharedPrefs: SharedPreferences

    private lateinit var dataSource: OverlayMenuPositionDataSource

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        mockWhen(mockContext.getSharedPreferences(OverlayMenuPositionDataSource.PREFERENCE_NAME, Context.MODE_PRIVATE))
            .thenReturn(mockSharedPrefs)

        dataSource = OverlayMenuPositionDataSource(mockContext)
    }

    @Test
    fun loadLandscapePosition_withZeroX_returnsSavedPosition() {
        mockSavedLandscapePosition(x = 0, y = 42)

        val position = dataSource.loadMenuPosition(Configuration.ORIENTATION_LANDSCAPE)

        assertEquals(0, position?.x)
        assertEquals(42, position?.y)
    }

    @Test
    fun loadLandscapePosition_withZeroY_returnsSavedPosition() {
        mockSavedLandscapePosition(x = 84, y = 0)

        val position = dataSource.loadMenuPosition(Configuration.ORIENTATION_LANDSCAPE)

        assertEquals(84, position?.x)
        assertEquals(0, position?.y)
    }

    @Test
    fun loadLandscapePosition_withZeroXAndY_returnsSavedPosition() {
        mockSavedLandscapePosition(x = 0, y = 0)

        val position = dataSource.loadMenuPosition(Configuration.ORIENTATION_LANDSCAPE)

        assertEquals(0, position?.x)
        assertEquals(0, position?.y)
    }

    @Test
    fun loadLandscapePosition_withMissingKeys_returnsNull() {
        mockWhen(mockSharedPrefs.contains(OverlayMenuPositionDataSource.PREFERENCE_MENU_X_LANDSCAPE_KEY))
            .thenReturn(false)
        mockWhen(mockSharedPrefs.contains(OverlayMenuPositionDataSource.PREFERENCE_MENU_Y_LANDSCAPE_KEY))
            .thenReturn(false)

        assertNull(dataSource.loadMenuPosition(Configuration.ORIENTATION_LANDSCAPE))
    }

    private fun mockSavedLandscapePosition(x: Int, y: Int) {
        mockWhen(mockSharedPrefs.contains(OverlayMenuPositionDataSource.PREFERENCE_MENU_X_LANDSCAPE_KEY))
            .thenReturn(true)
        mockWhen(mockSharedPrefs.contains(OverlayMenuPositionDataSource.PREFERENCE_MENU_Y_LANDSCAPE_KEY))
            .thenReturn(true)
        mockWhen(mockSharedPrefs.getInt(OverlayMenuPositionDataSource.PREFERENCE_MENU_X_LANDSCAPE_KEY, 0))
            .thenReturn(x)
        mockWhen(mockSharedPrefs.getInt(OverlayMenuPositionDataSource.PREFERENCE_MENU_Y_LANDSCAPE_KEY, 0))
            .thenReturn(y)
    }
}
