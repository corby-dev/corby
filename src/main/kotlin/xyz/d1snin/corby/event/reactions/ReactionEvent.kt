/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

package xyz.d1snin.corby.event.reactions

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import xyz.d1snin.corby.event.EventListener

abstract class ReactionEvent(protected val emoji: String) : EventListener<GuildMessageReactionAddEvent>() {
    protected lateinit var msg: Message
    private lateinit var action: GuildMessageReactionAddEvent.() -> Unit

    protected fun perform(block: GuildMessageReactionAddEvent.() -> Unit) {
        action = block
    }

    init {
        execute {
            if (emoji == reaction.reactionEmote.name) {
                msg = retrieveMessage().complete()
                action(this)
            }
        }
    }
}