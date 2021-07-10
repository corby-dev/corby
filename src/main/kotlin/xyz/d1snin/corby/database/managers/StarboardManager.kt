/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
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