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
package com.buzbuz.smartautoclicker.scenarios

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.buzbuz.smartautoclicker.R
import com.buzbuz.smartautoclicker.core.display.recorder.showMediaProjectionWarning
import com.buzbuz.smartautoclicker.core.settings.domain.SettingsRepository

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThirdPartyLauncherActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SCENARIO_ID = "com.buzbuz.smartautoclicker.EXTRA_SCENARIO_ID"
        const val EXTRA_IS_SMART_SCENARIO = "com.buzbuz.smartautoclicker.EXTRA_IS_SMART_SCENARIO"
    }

    @Inject lateinit var settingsRepository: SettingsRepository

    private val viewModel: ThirdPartyLauncherViewModel by viewModels()
    private lateinit var projectionActivityResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Security check: strip potential spoofed referrer extras
        intent?.removeExtra(Intent.EXTRA_REFERRER)
        intent?.removeExtra(Intent.EXTRA_REFERRER_NAME)
        val callingPackage = referrer?.host

        val isEnabled = settingsRepository.isThirdPartyTriggerEnabled()
        val whitelist = settingsRepository.getThirdPartyWhitelist()

        if (!isEnabled || callingPackage == null || callingPackage !in whitelist) {
            val appLabel = try {
                val pm = packageManager
                pm.getApplicationLabel(pm.getApplicationInfo(callingPackage ?: "", 0)).toString()
            } catch (e: Exception) {
                callingPackage ?: "Unknown app"
            }
            Toast.makeText(this, getString(R.string.toast_third_party_denied, appLabel), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Parse scenario information
        val scenarioId = intent?.getLongExtra(EXTRA_SCENARIO_ID, -1) ?: -1L
        val isSmartScenario = intent?.getBooleanExtra(EXTRA_IS_SMART_SCENARIO, false) ?: false

        if (scenarioId == -1L) {
            Log.e(TAG, "Invalid scenario ID, finishing")
            finish()
            return
        }

        if (isSmartScenario) {
            onCreateSmartScenarioLauncher(scenarioId)
        } else {
            onCreateDumbScenarioLauncher(scenarioId)
        }
    }

    private fun onCreateDumbScenarioLauncher(scenarioId: Long) {
        viewModel.startPermissionFlowIfNeeded(
            activity = this,
            onMandatoryDenied = ::finish,
            onAllGranted = {
                viewModel.startDumbScenario(scenarioId)
                finish()
            }
        )
    }

    private fun onCreateSmartScenarioLauncher(scenarioId: Long) {
        projectionActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                finish()
                return@registerForActivityResult
            }

            viewModel.startSmartScenario(result.resultCode, result.data!!, scenarioId)
            finish()
        }

        viewModel.startPermissionFlowIfNeeded(
            activity = this,
            onMandatoryDenied = ::finish,
            onAllGranted = ::showMediaProjectionWarning
        )
    }

    private fun showMediaProjectionWarning() {
        projectionActivityResult.showMediaProjectionWarning(this, viewModel.isEntireScreenCaptureForced()) {
            finish()
        }
    }
}

private const val TAG = "ThirdPartyLauncher"
