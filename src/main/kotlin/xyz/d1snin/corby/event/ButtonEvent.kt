/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
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