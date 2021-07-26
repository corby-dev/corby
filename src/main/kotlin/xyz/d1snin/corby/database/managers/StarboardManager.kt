/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.database.managers

import net.dv8tion.jda.api.entities.Guild
import xyz.d1snin.corby.model.Starboard
import xyz.d1snin.corby.util.formatJson

object StarboardManager : Manager(collectionName = "starboards") {
    internal operator fun get(guild: Guild): Starboard? = collection
        .findOne(formatJson("guild" to guild.id)).`as`(Starboard::class.java)


    internal operator fun plusAssign(starboard: Starboard) {
        collection.run {
            if (contains(starboard)) {
                update(formatJson("guild" to starboard.guild)).with(starboard)
            } else {
                insert(starboard)
            }
        }
    }

    private fun contains(starboard: Starboard): Boolean = collection
        .count(formatJson("guild" to starboard.guild)) > 0
}