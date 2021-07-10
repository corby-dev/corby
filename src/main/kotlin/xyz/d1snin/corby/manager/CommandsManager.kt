/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

package xyz.d1snin.corby.manager

import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.Corby.log
import xyz.d1snin.corby.commands.AbstractCommand
import xyz.d1snin.corby.database.managers.PrefixManager
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.model.Statement

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

    fun getCommandsByCategory(category: Category): List<AbstractCommand> {
        return commands.filter { it.category == category }
    }

    fun getCommandByUsage(usage: String): AbstractCommand? {
        return commands.firstOrNull { it.usage == usage }
    }

    fun getUsagesAsString(cmd: AbstractCommand): String {
        val defaultUsage = "`${PrefixManager[cmd.event.guild]}${cmd.usage}`"

        if (cmd.statements.isEmpty()) {
            return defaultUsage
        }

        return buildString {
            if (cmd.defaultAction != null) {
                append(defaultUsage).append("\n")
            }

            cmd.statements.forEach {
                append("`${PrefixManager[cmd.event.guild]}${cmd.usage} ${getRequiredArgumentsAsString(it)}\n")
            }
        }
    }

    private fun getRequiredArgumentsAsString(statement: Statement): String {
        return buildString {
            statement.arguments.forEach {
                append(
                    if (it.usage == null) {
                        it.type
                    } else {
                        "${it.usage} ${it.type}"
                    }
                )
                append(" ")
            }
        }.trim() + "`"
    }
}