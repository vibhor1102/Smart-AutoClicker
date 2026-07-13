package com.buzbuz.smartautoclicker.feature.smart.config.ui.action.sound

import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import com.buzbuz.smartautoclicker.core.common.overlays.base.viewModels
import com.buzbuz.smartautoclicker.core.common.overlays.dialog.OverlayDialog
import com.buzbuz.smartautoclicker.core.ui.bindings.dialogs.DialogNavigationButton
import com.buzbuz.smartautoclicker.core.ui.bindings.dialogs.setButtonEnabledState
import com.buzbuz.smartautoclicker.core.ui.bindings.fields.setLabel
import com.buzbuz.smartautoclicker.core.ui.bindings.fields.setOnTextChangedListener
import com.buzbuz.smartautoclicker.core.ui.bindings.fields.setText
import com.buzbuz.smartautoclicker.feature.smart.config.R
import com.buzbuz.smartautoclicker.feature.smart.config.databinding.DialogConfigActionSoundBinding
import com.buzbuz.smartautoclicker.feature.smart.config.di.ScenarioConfigViewModelsEntryPoint
import com.buzbuz.smartautoclicker.feature.smart.config.ui.action.OnActionConfigCompleteListener
import com.buzbuz.smartautoclicker.feature.smart.config.ui.common.dialogs.showCloseWithoutSavingDialog
import com.buzbuz.smartautoclicker.feature.smart.config.ui.common.starters.newSoundPickerStarterOverlay
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class SoundDialog(
    private val listener: OnActionConfigCompleteListener,
) : OverlayDialog(R.style.ScenarioConfigTheme) {

    private val viewModel: SoundViewModel by viewModels(
        entryPoint = ScenarioConfigViewModelsEntryPoint::class.java,
        creator = { soundViewModel() },
    )

    private lateinit var viewBinding: DialogConfigActionSoundBinding

    override fun onCreateView(): ViewGroup {
        viewBinding = DialogConfigActionSoundBinding.inflate(LayoutInflater.from(context)).apply {
            layoutTopBar.apply {
                dialogTitle.setText(R.string.dialog_title_sound)
                buttonDismiss.setDebouncedOnClickListener { back() }
                buttonSave.visibility = View.VISIBLE
                buttonSave.setDebouncedOnClickListener { onSaveClicked() }
                buttonDelete.visibility = View.VISIBLE
                buttonDelete.setDebouncedOnClickListener { onDeleteClicked() }
            }
            fieldName.apply {
                setLabel(R.string.generic_name)
                textField.filters = arrayOf(InputFilter.LengthFilter(context.resources.getInteger(R.integer.name_max_length)))
                setOnTextChangedListener { viewModel.setName(it.toString()) }
            }
            hideSoftInputOnFocusLoss(fieldName.textField)
            buttonChooseSound.setDebouncedOnClickListener {
                overlayManager.navigateTo(context, newSoundPickerStarterOverlay(context), hideCurrent = true)
            }
        }
        return viewBinding.root
    }

    override fun onDialogCreated(dialog: BottomSheetDialog) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) { launch { viewModel.isEditingAction.collect(::onEditingChanged) } }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { launch { viewModel.uiState.collect(::onUiStateChanged) } }
        }
    }

    override fun back() {
        if (viewModel.hasUnsavedModifications()) {
            context.showCloseWithoutSavingDialog { listener.onDismissClicked(); super.back() }
        } else {
            listener.onDismissClicked()
            super.back()
        }
    }

    private fun onUiStateChanged(state: SoundDialogUiState?) {
        state ?: return
        viewBinding.fieldName.setText(state.name)
        viewBinding.textSelectedSound.text = if (state.uri.isEmpty()) {
            context.getString(R.string.sound_not_selected)
        } else state.uri
        viewBinding.layoutTopBar.setButtonEnabledState(DialogNavigationButton.SAVE, state.canBeSaved)
    }

    private fun onSaveClicked() {
        listener.onConfirmClicked()
        super.back()
    }

    private fun onDeleteClicked() {
        listener.onDeleteClicked()
        super.back()
    }

    private fun onEditingChanged(isEditing: Boolean) {
        if (!isEditing) {
            Log.e(TAG, "Closing SoundDialog because there is no action edited")
            finish()
        }
    }
}

private const val TAG = "SoundDialog"
