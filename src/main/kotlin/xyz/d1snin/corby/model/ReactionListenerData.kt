/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.model

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent

data class ReactionListenerData<T : GuildMessageReactionAddEvent>(
    val message: Message, val reaction: MessageReaction.ReactionEmote, val block: T.() -> Unit
)
