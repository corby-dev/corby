package xyz.d1snin.corby.model

import net.dv8tion.jda.api.entities.Emote
import xyz.d1snin.corby.Corby
import java.awt.Color

enum class EmbedType(val color: Color, private val emote: String?) {
    ERROR(Corby.config.errorColor!!, Corby.config.emoteFailureId),
    SUCCESS(Corby.config.successColor!!, Corby.config.emoteSuccessId),
    DEFAULT(Corby.config.defaultColor!!, null);

    fun getEmote(): Emote? {
        return emote?.let {
            Corby.shards.getEmoteById(emote)
        }
    }
}