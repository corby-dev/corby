/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands

import org.slf4j.event.Level
import xyz.d1snin.corby.Corby.log
import xyz.d1snin.corby.manager.CooldownsManager
import xyz.d1snin.corby.model.Cooldown
import xyz.d1snin.corby.util.runSafe

class CommandExecutor(private val absCmd: AbstractCommand, private val provider: CommandProvider) {
    fun tryToExecute(): Boolean {
        if (absCmd.defaultAction == null && absCmd.statements.isEmpty()) {
            log(
                "You did not set the actions on execution. Command can not be executed (Message Content: ${provider.content})",
                Level.WARN
            )
            return false
        }

        if (provider.args.size < 2) {
            if (absCmd.defaultAction == null) {
                return false
            }

            absCmd.defaultAction!!(provider)

            return true

        } else {
            if (absCmd.statements.isEmpty()) {
                return false
            }

            loop@ for (s in absCmd.statements) {
                if (s.length != 0 && s.length != provider.args.size - 1) {
                    continue@loop
                }

                var argCount = 0
                var i = 0

                for (_i in 0..s.arguments.size) {
                    val arg = s.arguments[argCount]

                    if (arg.usage != null) {
                        if (arg.usage != provider.args[i + 1]) {
                            if (s == absCmd.statements.last()) {
                                return false

                            } else {
                                continue@loop
                            }
                        }
                        if (arg.isValueRequired) {
                            if (arg.isVariableLength) {
                                arg.value = provider.getContent(i + 2)!!
                                break
                            }

                            arg.value = provider.getContent(i + 2)!!

                            i += 1
                        }
                    } else {
                        if (arg.isVariableLength) {
                            arg.value = provider.args[i + 1]
                        }

                        argCount += 1
                    }

                    absCmd.statement = s

                    runSafe {
                        absCmd.executeStatement(provider)
                    }

                    CooldownsManager += Cooldown(provider.author, absCmd)

                    return true
                }
            }
        }
        return false
    }
}