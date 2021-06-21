package xyz.d1snin.corby.model

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import xyz.d1snin.corby.Corby

data class Starboard(
    val guild: String, val channel: String, val stars: Int, val status: Boolean
) {
    fun getGuild(): Guild? {
        return Corby.shards.getGuildById(guild)
    }

    fun getChannel(): TextChannel? {
        return Corby.shards.getTextChannelById(channel)
    }
}
