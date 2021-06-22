package xyz.d1snin.corby.model

import net.dv8tion.jda.api.entities.User
import xyz.d1snin.corby.commands.AbstractCommand

data class Cooldown(
    val user: User,
    val command: AbstractCommand,
) {
    var cooldown = command.cooldown
}
