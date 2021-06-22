package xyz.d1snin.corby.util

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import xyz.d1snin.corby.Corby

fun sendDmSafe(user: User?, success: MessageEmbed, fail: (e: Throwable) -> Unit = { it.printStackTrace() }) {
    user ?: return

    user.openPrivateChannel()
        .queue {
            it.sendMessage(success)
                .queue({ /* ok */ }) { throwable ->
                    fail(throwable)
                }
        }
}

fun isOwner(user: User): Boolean {
    return user.id == Corby.config.ownerId
}