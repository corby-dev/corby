/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

package xyz.d1snin.corby.event.reactions

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageReaction
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.database.managers.StarboardManager
import xyz.d1snin.corby.util.createEmbed
import xyz.d1snin.corby.util.sendDmSafe
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList

object StarboardReactionEvent : ReactionEvent(emoji = Corby.config.emoteStarUnicode) {

    private val executed = CopyOnWriteArrayList<MessageReaction>() // we need to implement this with a database

    init {
        perform {
            val starboard = StarboardManager[guild]
            starboard ?: return@perform

            if (!starboard.status) {
                return@perform
            }

            if (msg.reactions.isEmpty()) {
                return@perform
            }

            val react = msg.reactions.first {
                it.reactionEmote.name == emoji
            }

            if (react.count == starboard.stars
                && !executed.contains(react)
            ) {
                runCatching {
                    val attachment = if (msg.attachments.isEmpty()) null else msg.attachments.first()
                    val embed = if (msg.embeds.isEmpty()) null else msg.embeds.first()

                    starboard.getJdaChannel()!!.sendMessage(
                        EmbedBuilder().run {
                            setAuthor(msg.author.asTag, msg.jumpUrl, msg.author.effectiveAvatarUrl)

                            setColor(Corby.config.starboardColor)

                            setDescription(
                                "[[context]](%s)\n%s%s%s".format(
                                    msg.jumpUrl,

                                    if (msg.contentRaw.isNotEmpty()) "\n${msg.contentRaw}" else "",

                                    attachment?.let {
                                        if (attachment.isImage) "" else "\n${attachment.proxyUrl}"
                                    } ?: "",

                                    embed?.let {
                                        "%s%s".format(
                                            embed.title?.let {
                                                "\n$it"
                                            } ?: "",
                                            embed.description?.let {
                                                "\n$it"
                                            } ?: ""
                                        )
                                    } ?: ""
                                )
                            )

                            setTimestamp(Instant.now())

                            if (attachment != null && attachment.isImage) {
                                setImage(attachment.proxyUrl)
                            }

                            build()
                        }
                    ).queue()

                    executed += react

                }.onFailure {
                    sendDmSafe(
                        guild.owner?.user, createEmbed(
                            "Hey! You received this message because I cannot send messages to the starboard." +
                                    "Please make sure I have permission."
                        )
                    )
                }
            }
        }
    }
}