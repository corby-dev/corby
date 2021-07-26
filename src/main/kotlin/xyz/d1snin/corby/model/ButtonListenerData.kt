/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.model

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent

data class ButtonListenerData(
    val user: User,
    val buttonId: String,
    val block: ButtonClickEvent.() -> Unit
)