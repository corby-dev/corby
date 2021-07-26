/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.database.managers

import net.dv8tion.jda.api.entities.Guild
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.model.Prefix
import xyz.d1snin.corby.util.formatJson

object PrefixManager : Manager(collectionName = "guildprefix") {
    internal operator fun get(guild: Guild) = if (contains(guild))
        collection.findOne(formatJson("guild" to guild.id)).`as`(Prefix::class.java)
    else
        Prefix(guild.id, Corby.config.defaultPrefix)

    internal operator fun plusAssign(prefix: Prefix) {
        collection.run {
            if (contains(prefix.getJdaGuild()!!))
                update(formatJson("guild" to prefix.guild)).with(prefix)
            else
                insert(prefix)
        }
    }

    private fun contains(guild: Guild): Boolean = collection
        .count(formatJson("guild" to guild.id)) > 0
}