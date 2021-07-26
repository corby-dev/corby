/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.model

import net.dv8tion.jda.api.entities.Emote
import xyz.d1snin.corby.Corby
import java.awt.Color

enum class EmbedType(val color: Color, private val emote: String? = null) {
    ERROR(Corby.config.errorColor!!, Corby.config.emoteFailureId),
    SUCCESS(Corby.config.successColor!!, Corby.config.emoteSuccessId),
    DEFAULT(Corby.config.defaultColor!!);

    fun getEmote(): Emote? {
        return emote?.let {
            Corby.sharding.getEmoteById(emote)
        }
    }
}