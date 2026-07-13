package com.buzbuz.smartautoclicker.feature.smart.config.ui.common.model.action

import android.content.Context

import com.buzbuz.smartautoclicker.core.domain.model.action.Sound
import com.buzbuz.smartautoclicker.feature.smart.config.R

internal fun Sound.getDescription(context: Context, inError: Boolean): String =
    if (inError) context.getString(R.string.item_error_action_invalid_generic)
    else context.getString(R.string.item_sound_details)
