package xyz.d1snin.corby.commands.admin

import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.commands.Command
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.model.EmbedType

object TerminateCommand : Command(
    usage = "terminate",
    description = "Shuts down the bot",
    category = Category.ADMIN,
) {
    init {
        default {
            sendFastEmbed("Terminating... Bye!", type = EmbedType.SUCCESS)
            Corby.shutdown(Corby.GOOD_EXIT_CODE)
        }
    }
}