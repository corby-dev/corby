/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.manager

import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.Corby.log
import xyz.d1snin.corby.commands.AbstractCommand

object CommandsManager {
    val commands = mutableListOf<AbstractCommand>()

    internal fun addAll(vararg commandz: AbstractCommand): List<AbstractCommand> {
        commandz.forEach {
            commands += it

            Corby.permissions.addAll(it.botPerms)

            it.cooldown = if (it.cooldown == 0) {
                Corby.config.defaultCooldown
            } else {
                it.cooldown
            }

            log("Command is initialized: ${it::class.simpleName}")
        }

        log("Commands initialization completed.")
        return commandz.asList()
    }
}