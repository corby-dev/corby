/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands

import net.dv8tion.jda.api.entities.MessageEmbed
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.manager.CommandsManager
import xyz.d1snin.corby.model.EmbedType
import xyz.d1snin.corby.util.createEmbed
import java.util.concurrent.TimeUnit

open class CommandProvider(val cmd: AbstractCommand) {
    val event = cmd.event
    val msg = event.message
    val author = event.author
    val content = msg.contentRaw
    val args = content.split("\\s+".toRegex())
    val channel = event.channel
    var guild = event.guild
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
        event.channel.sendMessage(createFastEmbed(content, type = type)).queue()
    }

    internal fun sendEphemeralEmbed(content: String, type: EmbedType = EmbedType.DEFAULT, delay: Long) {
        event.channel.sendMessage(createFastEmbed(content, type)).queue {
            Corby.scheduler.schedule({
                it.delete().reason("Ephemeral Corby's Message.").queue()
            }, delay, TimeUnit.SECONDS)
        }
    }

    internal fun createFastEmbed(content: String, type: EmbedType = EmbedType.DEFAULT): MessageEmbed {
        return createEmbed(content, guild, author, type = type)
    }

    internal fun sendLoadingMessageAndEdit(successEmbed: () -> MessageEmbed) {
        channel.sendMessage(createEmbed("Processing...", guild, author)).queue {
            it.editMessage(successEmbed()).queue()
        }
    }

    internal fun getArgVal(index: Int = 0): String {
        return cmd.statement.arguments[index].value
    }

    fun trigger() {
        event.channel.sendMessage(
            event.createEmbed(
                "**Incorrect Syntax:** `$content`\n\n**Usage:**\n${CommandsManager.getUsagesAsString(cmd)}",
                type = EmbedType.ERROR
            )
        ).queue()
    }
}