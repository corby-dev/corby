/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

package xyz.d1snin.corby.commands.admin

import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.commands.Command
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.model.EmbedType

object TerminateCommand : Command(
    usage = "terminate",
    description = "Shuts down the bot",
    category = Category.ADMIN
) {
    init {
        noArgs {
            channel.sendMessage(createFastEmbed("Terminating... Bye!", EmbedType.SUCCESS)).complete()
            Corby.shutdown(Corby.GOOD_EXIT_CODE)
        }
    }
}