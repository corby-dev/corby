/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands

import org.slf4j.event.Level
import xyz.d1snin.corby.Corby.log
import xyz.d1snin.corby.manager.CooldownsManager
import xyz.d1snin.corby.model.Cooldown
import xyz.d1snin.corby.model.Statement
import xyz.d1snin.corby.util.runSafe

class CommandExecutor(private val provider: CommandProvider) {

    private val cmd = provider.cmd

    internal fun tryToExecute(): Boolean {
        if (cmd.defaultAction == null && cmd.statements.isEmpty()) {
            log(
                "You did not set the actions on execution. Command can not be executed (Message Content: ${provider.content})",
                Level.WARN
            )
            return false
        }

        if (provider.args.size < 2) {
            if (cmd.defaultAction == null) {
                return false
            }
            submit()
            return true

        } else {

            if (cmd.statements.isEmpty()) {
                return false
            }

            outer@ for (s in cmd.statements) {
                if (s.length != 0 && s.length != provider.args.size - 1) {
                    continue@outer
                }

                var argCount = 0
                var i = 0

                inner@ for (_i in 0 until s.arguments.size) {
                    val arg = s.arguments[argCount]

                    if (arg.usage != null) {
                        if (arg.usage != provider.args[i + 1]) {
                            if (s == cmd.statements.last()) {
                                return false

                            } else {
                                continue@outer
                            }
                        }
                        if (arg.isValueRequired || arg.isVariableLength) {
                            if (arg.isVariableLength) {
                                arg.value = provider.getContent(i + 2)!!
                                break@inner
                            }

                            arg.value = provider.args[i + 2]

                            i += 1
                        }
                    } else {
                        if (arg.isVariableLength) {
                            arg.value = provider.getContent(i + 1)!!

                        } else {
                            arg.value = provider.args[i + 1]
                        }
                        argCount += 1
                    }
                    i += 1
                }
                submit(s)
                return true
            }
        }
        return false
    }

    private fun submit(statement: Statement? = null) {
        statement?.let {
            cmd.statement = it
        }

        CooldownsManager += Cooldown(provider.author, cmd)

        runSafe {
            cmd.statement.block(provider)
        }
    }
}