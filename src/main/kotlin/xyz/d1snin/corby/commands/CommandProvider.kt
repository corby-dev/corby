/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands

import xyz.d1snin.corby.database.managers.PrefixManager
import xyz.d1snin.corby.model.EmbedType
import xyz.d1snin.corby.model.Statement
import xyz.d1snin.corby.util.createEmbed

open class CommandProvider(val cmd: AbstractCommand) {
    val event = cmd.event
    val msg = event.message
    val author = event.author
    val content = msg.contentRaw
    val args = content.split("\\s+")
    val channel = event.channel
    val guild = event.guild
    val role = guild.botRole!!

    fun getContent(startPos: Int): String? {
        if (args.size < startPos) {
            return null
        }

        return buildString {
            for (i in startPos..args.size) {
                append(args[i]).append(" ")
            }
        }
    }

    fun sendFastMessage(content: String, type: EmbedType = EmbedType.DEFAULT) {
        event.channel.sendMessage(event.createEmbed(content, type = type, u = author)).queue()
    }

    fun trigger() {
        event.channel.sendMessage(event.createEmbed("**Incorrect Syntax:** `$content`\n\n**Usage:**\n${getUsagesAsString()}"))
            .queue()
    }

    private fun getUsagesAsString(): String {
        val defaultUsage = "`${PrefixManager[guild]}${cmd.usage}`"

        if (cmd.statements.isEmpty()) {
            return defaultUsage
        }

        return buildString {
            if (cmd.defaultAction != null) {
                append(defaultUsage).append("\n")
            }

            cmd.statements.forEach {
                append("`${PrefixManager[guild]}${cmd.usage} ")
                append(getRequiredArgumentsAsString(it))
                append("\n")
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
                        "${it.usage}${if (it.isValueRequired) " ${it.type}" else ""}"
                    }
                )
                append(" ")
            }
        }.trim() + "`"
    }
}