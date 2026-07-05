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
package com.buzbuz.smartautoclicker.core.ui.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.buzbuz.smartautoclicker.core.ui.R

fun copyAutomationIntentToClipboard(context: Context, scenarioId: Long, isSmart: Boolean) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = """
        Package: ${context.packageName}
        Class: com.buzbuz.smartautoclicker.scenarios.ThirdPartyLauncherActivity
        Action: com.buzbuz.smartautoclicker.action.START_SCENARIO
        Extras:
          - "com.buzbuz.smartautoclicker.EXTRA_SCENARIO_ID" (Long): $scenarioId
          - "com.buzbuz.smartautoclicker.EXTRA_IS_SMART_SCENARIO" (Boolean): $isSmart
    """.trimIndent()

    val clip = ClipData.newPlainText("Smart AutoClicker Automation Details", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, R.string.toast_copied_automation_intent, Toast.LENGTH_SHORT).show()
}
