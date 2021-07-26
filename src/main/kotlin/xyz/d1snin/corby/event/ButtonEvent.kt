/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.event

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import xyz.d1snin.corby.model.ButtonListenerData

object ButtonEvent : EventListener<ButtonClickEvent>() {

    private val listeners = mutableListOf<ButtonListenerData>()

    init {
        execute {
            listeners.forEach {
                if (user == it.user
                    && it.buttonId == componentId
                ) {
                    it.block(this)
                    deferEdit().queue()
                }
            }
        }
    }

    internal fun registerListener(data: ButtonListenerData) {
        listeners += data
    }
}