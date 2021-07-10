/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

package xyz.d1snin.corby.commands.misc

import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.commands.Command
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.util.formatWithKey

object PingCommand : Command(
    usage = "ping",
    description = "Provides the current ping of the bot",
    category = Category.MISC
) {
    init {
        noArgs {
            sendFastEmbed(formatWithKey("Current Ping" to Corby.ping))
        }
    }
}