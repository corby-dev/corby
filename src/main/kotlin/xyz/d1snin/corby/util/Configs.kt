package xyz.d1snin.corby.util

import com.google.gson.Gson
import lombok.NoArgsConstructor
import xyz.d1snin.corby.Corby.log
import java.awt.Color
import java.io.File
import java.io.FileReader

@NoArgsConstructor
data class Configs(
    val token: String,
    val testBotToken: String,
    val defaultPrefix: String,
    val shards: Int,
    val ownerId: String,
    val defaultStarboardStars: Int,
    val defaultStarboardStatus: Boolean,
    val defaultCooldown: Int,
    val emoteSuccessId: String,
    val emoteFailureId: String,
    val emoteBackId: String,
    val emoteNextId: String,
    val emoteStarUnicode: String,
    val emoteBackUnicode: String,
    val emoteNextUnicode: String,

    var defaultColor: Color? = null,
    var errorColor: Color? = null,
    var successColor: Color? = null,
    var starboardColor: Color? = null,
    var botName: String? = null,
    var botPfpUrl: String? = null,
    var inviteUrl: String? = null,
    var id: String? = null,
    var nameAsTag: String? = null
) {
    internal fun other(
        defaultColor: Color,
        errorColor: Color,
        successColor: Color,
        starboardColor: Color,
        botName: String,
        botPfpUrl: String,
        inviteUrl: String,
        id: String,
        nameAsTag: String
    ) {
        this.defaultColor = defaultColor
        this.errorColor = errorColor
        this.successColor = successColor
        this.starboardColor = starboardColor
        this.botName = botName
        this.botPfpUrl = botPfpUrl
        this.inviteUrl = inviteUrl
        this.id = id
        this.nameAsTag = nameAsTag
    }

    companion object {
        internal fun init(file: File): Configs {
            require(file.exists()) {
                "Config file doesn't exists."
            }

            return Gson().fromJson(FileReader(file), Configs::class.java).also {
                log("Config file was initialized.")
            }
        }
    }
}