/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
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