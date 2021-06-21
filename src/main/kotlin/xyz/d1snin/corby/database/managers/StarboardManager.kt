package xyz.d1snin.corby.database.managers

import net.dv8tion.jda.api.entities.Guild
import xyz.d1snin.corby.model.Starboard

object StarboardManager : Manager(collectionName = "starboards") {
    internal operator fun get(guild: Guild): Starboard? = collection
        .findOne("{guild: '${guild.id}'}").`as`(Starboard::class.java)


    internal operator fun plusAssign(starboard: Starboard) {
        collection.run {
            if (contains(starboard)) {
                update("{guild: '${starboard.guild}'}").with(starboard)
            } else {
                insert(starboard)
            }
        }
    }

    private fun contains(starboard: Starboard): Boolean = collection
        .count("{guild: '${starboard.guild}'}") > 0
}