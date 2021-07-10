/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

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
        return Corby.sharding.getGuildById(guild)
    }

    fun getJdaChannel(): TextChannel? {
        return Corby.sharding.getTextChannelById(channel)
    }
}
