/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.model

import net.dv8tion.jda.api.entities.User
import xyz.d1snin.corby.commands.AbstractCommand

data class Cooldown(
    val user: User,
    val command: AbstractCommand
) {
    var cooldown = command.cooldown
}
