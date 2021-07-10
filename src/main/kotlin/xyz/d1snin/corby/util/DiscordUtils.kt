/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

package xyz.d1snin.corby.util

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonStyle
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.event.ButtonEvent
import xyz.d1snin.corby.model.ButtonListenerData

internal fun sendDmSafe(user: User?, success: MessageEmbed, fail: (e: Throwable) -> Unit = { it.printStackTrace() }) {
    user ?: return

    user.openPrivateChannel()
        .queue {
            it.sendMessage(success)
                .queue({ /* ok */ }) { throwable ->
                    fail(throwable)
                }
        }
}

internal fun isOwner(user: User): Boolean {
    return user.id == Corby.config.ownerId
}

internal fun createButtonSafe(
    user: User,
    label: String,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    block: ButtonClickEvent.() -> Unit
): Button {
    val simpleId = (1000000000..9999999999).random().toString()
    ButtonEvent.registerListener(ButtonListenerData(user, simpleId, block))
    return Button.of(style, simpleId, label)
}