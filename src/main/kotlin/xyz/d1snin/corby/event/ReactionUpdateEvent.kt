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

            for (data in listeners) {
                if (data.message == msg
                    && data.reaction == react
                ) {
                    data.block(this)
                }
            }
        }
    }

    internal fun registerListener(data: ReactionListenerData<GuildMessageReactionAddEvent>) {
        listeners += data
    }
}