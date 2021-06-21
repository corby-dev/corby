package xyz.d1snin.corby.util

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User

fun sendDmSafe(user: User, success: MessageEmbed, fail: (e: Throwable) -> Unit = { it.printStackTrace() }) {
    user.openPrivateChannel()
        .queue {
            it.sendMessage(success)
                .queue({ /* ok */ }) { throwable ->
                    fail(throwable)
                }
        }
}