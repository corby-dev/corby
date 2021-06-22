package xyz.d1snin.corby.event.reactions

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import xyz.d1snin.corby.event.Listener

abstract class ReactionEvent(protected val emoji: String) : Listener<GuildMessageReactionAddEvent>() {
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