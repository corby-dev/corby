/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

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
        noArgs {
            channel.sendMessage(createFastEmbed("Terminating... Bye!", EmbedType.SUCCESS)).complete()
            Corby.shutdown(Corby.GOOD_EXIT_CODE)
        }
    }
}