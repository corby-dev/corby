/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.event

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import xyz.d1snin.corby.model.ReactionListenerData
import java.util.concurrent.CopyOnWriteArrayList

object ReactionUpdateEvent : Listener<GuildMessageReactionAddEvent>() {

    private val listeners: CopyOnWriteArrayList<ReactionListenerData<GuildMessageReactionAddEvent>> =
        CopyOnWriteArrayList()

    init {
        execute {
            if (reaction.isSelf) {
                return@execute
            }

            val msg = retrieveMessage().complete()
            val react = reaction.reactionEmote

            for ((message, reaction, block) in listeners) {
                if (message == msg
                    && reaction == react
                ) {
                    block(this)
                }
            }
        }
    }

    internal fun registerListener(data: ReactionListenerData<GuildMessageReactionAddEvent>) {
        listeners += data
    }
}