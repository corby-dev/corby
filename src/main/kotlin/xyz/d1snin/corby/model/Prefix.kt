package xyz.d1snin.corby.model

import net.dv8tion.jda.api.entities.Guild
import xyz.d1snin.corby.Corby

data class Prefix(val guild: String, val prefix: String) {
    override fun toString(): String {
        return prefix
    }

    fun getGuild(): Guild? {
        return Corby.shards.getGuildById(guild)
    }
}
