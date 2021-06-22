package xyz.d1snin.corby.manager

import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.commands.AbstractCommand

object CommandsManager {
    val commands = mutableListOf<AbstractCommand>()

    fun addAll(vararg commandz: AbstractCommand): List<AbstractCommand> {
        commandz.forEach {
            commands += it

            Corby.permissions.addAll(it.botPerms)

            it.cooldown = if (it.cooldown == 0) {
                Corby.config.defaultCooldown
            } else {
                it.cooldown
            }
        }

        return commandz.asList()
    }
}