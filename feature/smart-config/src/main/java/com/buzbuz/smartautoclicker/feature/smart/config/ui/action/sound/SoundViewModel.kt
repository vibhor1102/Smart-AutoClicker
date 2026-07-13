package com.buzbuz.smartautoclicker.feature.smart.config.ui.action.sound

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.buzbuz.smartautoclicker.core.domain.model.action.Sound
import com.buzbuz.smartautoclicker.feature.smart.config.domain.EditionRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SoundViewModel @Inject constructor(
    private val editionRepository: EditionRepository,
) : ViewModel() {

    private val configuredSound = editionRepository.editionState.editedActionState
        .mapNotNull { it.value }
        .filterIsInstance<Sound>()

    private val hasChanged = editionRepository.editionState.editedActionState
        .map { it.hasChanged }

    val isEditingAction: Flow<Boolean> = editionRepository.isEditingAction.distinctUntilChanged()

    val uiState: StateFlow<SoundDialogUiState?> = combine(configuredSound, hasChanged) { sound, changed ->
        SoundDialogUiState(sound.name ?: "", sound.uri, sound.isComplete(), changed)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setName(name: String) {
        editionRepository.editionState.getEditedAction<Sound>()?.let { sound ->
            editionRepository.updateEditedAction(sound.copy(name = name))
        }
    }

    fun hasUnsavedModifications(): Boolean = uiState.value?.hasUnsavedModifications == true
}

data class SoundDialogUiState(
    val name: String,
    val uri: String,
    val canBeSaved: Boolean,
    val hasUnsavedModifications: Boolean,
)
