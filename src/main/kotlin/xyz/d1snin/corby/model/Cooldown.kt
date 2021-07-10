/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
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
