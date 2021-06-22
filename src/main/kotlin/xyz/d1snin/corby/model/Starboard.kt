package xyz.d1snin.corby.model

import com.fasterxml.jackson.annotation.JsonProperty
import lombok.NoArgsConstructor
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import xyz.d1snin.corby.Corby

@NoArgsConstructor
data class Starboard(
    @JsonProperty("guild") val guild: String,
    @JsonProperty("channel") val channel: String,
    @JsonProperty("stars") val stars: Int,
    @JsonProperty("status") val status: Boolean
) {
    fun getJdaGuild(): Guild? {
        return Corby.shards.getGuildById(guild)
    }

    fun getJdaChannel(): TextChannel? {
        return Corby.shards.getTextChannelById(channel)
    }
}
