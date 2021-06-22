package xyz.d1snin.corby.database.managers

import net.dv8tion.jda.api.entities.Guild
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.model.Prefix

object PrefixManager : Manager(collectionName = "guildprefix") {
    internal operator fun get(guild: Guild) = if (contains(guild))
        collection.findOne("{guild: '${guild.id}'}").`as`(Prefix::class.java)
    else
        Prefix(guild.id, Corby.config.defaultPrefix)

    internal operator fun plusAssign(prefix: Prefix) {
        collection.run {
            if (contains(prefix.getJdaGuild()!!))
                update("{guild: '${prefix.guild}'}").with(prefix)
            else
                insert(prefix)
        }
    }

    private fun contains(guild: Guild): Boolean = collection
        .count("{guild: '${guild.id}'}") > 0
}