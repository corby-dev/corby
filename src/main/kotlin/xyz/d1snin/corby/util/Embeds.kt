package xyz.d1snin.corby.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.model.EmbedType

fun createEmbed(
    content: String,
    guild: Guild,
    u: User = Corby.selfUser,
    image: String? = null,
    thumbnail: String? = null,
    type: EmbedType = EmbedType.DEFAULT
): MessageEmbed = EmbedBuilder()

    .run {
        setColor(type.color)

        setDescription(
            buildString {
                if (type.getEmote() != null) {
                    if (guild.botRole?.hasPermission(Permission.MESSAGE_EXT_EMOJI)!!) {
                        append(type.getEmote()!!.asMention)
                            .append(" ")
                    }
                }
                append(content)
            }
        )

        setFooter(
            buildString {
                append(Corby.config.botName)
                append(" | ")
                append(Thread.currentThread().name)
                append(" | ")
                append(u.asTag)
            },

            Corby.config.botPfpUrl
        )

        image ?: setImage(image)
        thumbnail ?: setThumbnail(thumbnail)

        build()
    }

fun GenericGuildEvent.createEmbed(
    content: String,
    u: User = Corby.selfUser,
    image: String? = null,
    thumbnail: String? = null,
    type: EmbedType = EmbedType.DEFAULT
) = createEmbed(content, guild, u, image, thumbnail, type)
