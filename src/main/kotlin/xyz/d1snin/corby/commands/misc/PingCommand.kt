/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands.misc

import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.commands.Command
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.util.formatWithKey

object PingCommand : Command(
    usage = "ping",
    description = "Provides the current ping of the bot",
    category = Category.MISC,
) {
    init {
        default {
            sendFastEmbed(formatWithKey("Current Ping", Corby.ping))
        }
    }
}