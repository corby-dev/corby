package xyz.d1snin.corby.commands.misc

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import xyz.d1snin.corby.commands.Command
import xyz.d1snin.corby.database.managers.PrefixManager
import xyz.d1snin.corby.manager.CommandsManager
import xyz.d1snin.corby.model.Argument
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.util.createButtonSafe
import xyz.d1snin.corby.util.createEmbed
import xyz.d1snin.corby.util.isOwner
import java.util.concurrent.atomic.AtomicInteger

object HelpCommand : Command(
    usage = "help",
    description = "Gives you information about commands.",
    category = Category.MISC,
    botPerms = listOf(Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_ADD_REACTION)
) {

    private const val back = "Back"
    private const val next = "Next"

    init {
        default {
            val page = AtomicInteger(1)

            channel.sendMessage(getEmbedByPage(page.get())!!).setActionRow(
                createButtonSafe(author, back) {
                    executeButton(page, this)
                },

                createButtonSafe(author, next) {
                    executeButton(page, this)
                }
            ).queue()
        }

        execute(
            Argument(
                type = "<Command Usage>",
            )
        ) {
            val usage = getArgVal(0)
            CommandsManager.getCommandByUsage(usage)?.let {
                sendFastEmbed(
                    """
                        **Category:**
                        ${it.category.categoryName}
                        
                        **Description:**
                        ${it.description}${if (it.longDescription != null) "\n\n${it.longDescription}" else ""}
                        
                        **Usage:**
                        ${CommandsManager.getUsagesAsString(it)}
                    """.trimIndent()
                )
            } ?: trigger()
        }
    }

    private fun getEmbedByPage(page: Int): MessageEmbed? {
        val categories = if (isOwner(event.author)) {
            Category.values().size
        } else {
            Category.values().count {
                !it.adminCategory
            }
        }

        if (page > categories || page < 1) {
            return null
        }

        val category = Category.values().first {
            it.ordinal + 1 == page
        }

        val sb = buildString {
            CommandsManager.getCommandsByCategory(category).forEach {
                append("`${PrefixManager[event.guild].prefix}${it.usage}` - *${it.description}*\n")
            }
        }

        return createEmbed(
            "**${category.categoryName} Commands. Page $page/${categories}.**\n\n$sb",
            event.guild,
            event.author
        )
    }

    private fun executeButton(
        page: AtomicInteger,
        event: ButtonClickEvent,
    ) {
        val label = event.component?.label
        val editedPage = if (label == next) page.get() + 1 else page.get() - 1

        if (getEmbedByPage(editedPage) != null && event.message != null) {
            event.message?.editMessage(getEmbedByPage(page.addAndGet(if (label == next) +1 else -1))!!)?.queue()
        }

    }
}