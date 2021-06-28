/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.model

import com.fasterxml.jackson.annotation.JsonProperty
import lombok.NoArgsConstructor
import net.dv8tion.jda.api.entities.Guild
import xyz.d1snin.corby.Corby

@NoArgsConstructor
data class Prefix(
    @JsonProperty("guild") val guild: String,
    @JsonProperty("prefix") val prefix: String
) {
    override fun toString(): String {
        return prefix
    }

    fun getJdaGuild(): Guild? {
        return Corby.sharding.getGuildById(guild)
    }
}
