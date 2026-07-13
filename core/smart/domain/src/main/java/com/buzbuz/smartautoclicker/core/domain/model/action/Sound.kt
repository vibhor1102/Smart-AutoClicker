/*
 * Copyright (C) 2026 Kevin Buzeau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.buzbuz.smartautoclicker.core.domain.model.action

import com.buzbuz.smartautoclicker.core.base.identifier.Identifier

/** Plays a sound selected by the user through Android's ringtone picker. */
data class Sound(
    override val id: Identifier,
    override val eventId: Identifier,
    override val name: String? = null,
    override var priority: Int,
    val uri: String,
) : Action() {

    override fun hashCodeNoIds(): Int = name.hashCode() + uri.hashCode()

    override fun deepCopy(): Sound = copy(name = "" + name, uri = "" + uri)

    override fun isComplete(): Boolean = super.isComplete() && uri.isNotEmpty()
}
