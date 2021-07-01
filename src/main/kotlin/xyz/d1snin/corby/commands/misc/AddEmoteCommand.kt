/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands.misc

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Icon
import xyz.d1snin.corby.commands.Command
import xyz.d1snin.corby.model.Argument.Companion.argument
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.model.EmbedType
import java.net.URL
import kotlin.properties.Delegates

object AddEmoteCommand : Command(
    usage = "addemote",
    description = "Uploads emoji from a link or other emoji to your server",
    category = Category.MISC,
    userPerms = listOf(Permission.MANAGE_EMOTES),
    botPerms = listOf(Permission.MANAGE_EMOTES)
) {
    init {
        withArgs(
            argument {
                type = "<URL or Emote from another server>"
            },

            argument {
                type = "<Emote Name>"
            }
        ) {
            val name = getArgVal(1)

            val invalidUrl = "Provided URL is invalid."

            if (name.length > 32 || name.isEmpty()) {
                sendFastEmbed("Name must be between 1 and 32 characters in length.", EmbedType.ERROR)
                return@withArgs
            }

            sendLoadingMessageAndEdit {
                var url by Delegates.notNull<URL>()

                url = if (msg.emotes.isEmpty()) {
                    try {
                        URL(getArgVal())
                    } catch (e: Exception) {
                        return@sendLoadingMessageAndEdit createFastEmbed(invalidUrl, EmbedType.ERROR)
                    }
                } else {
                    URL(msg.emotes.first().imageUrl)
                }

                return@sendLoadingMessageAndEdit url.openConnection().apply {
                    setRequestProperty("User-Agent", "")
                }.let {
                    it.getInputStream().use { s ->
                        runCatching {
                            guild.createEmote(name, Icon.from(s)).submit().join()
                        }.onFailure {
                            return@sendLoadingMessageAndEdit createFastEmbed(
                                "Something went wrong when adding it, check the validity of the link and that it leads to the image, also check that the name is correct.",
                                EmbedType.ERROR
                            )
                        }
                        createFastEmbed("Emote `:$name:` successfully added.", EmbedType.SUCCESS)
                    }
                }
            }
        }
    }
}