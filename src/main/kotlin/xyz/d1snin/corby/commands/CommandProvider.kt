/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands

import net.dv8tion.jda.api.entities.MessageEmbed
import xyz.d1snin.corby.database.managers.PrefixManager
import xyz.d1snin.corby.model.EmbedType
import xyz.d1snin.corby.model.Statement
import xyz.d1snin.corby.util.createEmbed

open class CommandProvider(val cmd: AbstractCommand) {
    val event = cmd.event
    val msg = event.message
    val author = event.author
    val content = msg.contentRaw.lowercase()
    val args = content.split("\\s+".toRegex())
    val channel = event.channel
    val guild = event.guild
    val role = guild.botRole!!

    internal fun getContent(startPos: Int): String? {
        if (args.size < startPos) {
            return null
        }

        if (args.lastIndex == startPos) {
            return args.last()
        }

        return buildString {
            for (i in startPos until args.size) {
                append(args[i]).append(" ")
            }
        }
    }

    internal fun sendFastEmbed(content: String, type: EmbedType = EmbedType.DEFAULT) {
        event.channel.sendMessage(event.createEmbed(content, type = type, u = author)).queue()
    }

    internal fun createFastEmbed(content: String, type: EmbedType = EmbedType.DEFAULT): MessageEmbed {
        return createEmbed(content, guild, author, type = type)
    }

    internal fun sendLoadingMessageAndEdit(successEmbed: () -> MessageEmbed) {
        channel.sendMessage(createEmbed("Processing...", guild, author)).queue {
            it.editMessage(successEmbed()).queue()
        }
    }

    internal fun getArgVal(index: Int): String {
        return cmd.statement.arguments[index].value
    }

    fun trigger() {
        event.channel.sendMessage(
            event.createEmbed(
                "**Incorrect Syntax:** `$content`\n\n**Usage:**\n${getUsagesAsString()}",
                type = EmbedType.ERROR
            )
        ).queue()
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