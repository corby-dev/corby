/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands.`fun`

import com.github.bottomSoftwareFoundation.bottom.Bottom
import com.github.bottomSoftwareFoundation.bottom.TranslationError
import xyz.d1snin.corby.commands.Command
import xyz.d1snin.corby.model.Argument
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.model.EmbedType
import xyz.d1snin.corby.util.formatWithKey

object BottomCommand : Command(
    usage = "bottom",
    description = "Encrypts your message using a bottom cipher",
    category = Category.FUN,
) {
    init {
        val userLimit = 200
        val encodedMessageLimit = 2000 // embed description limit is 2048
        val errMessage = "Your message is too long or too short." // need better solution i guess
        val longRes = "Generated result is too long."

        execute(
            Argument(
                usage = "encode",
                type = "<Message 2 - $userLimit characters>",
                isVariableLength = true
            )
        ) {
            val encodedMessage = Bottom.encode(getContent(2))

            if (content.length !in 2..userLimit) {
                sendFastEmbed(errMessage, EmbedType.ERROR)
                return@execute
            }

            if (encodedMessage.length > encodedMessageLimit) {
                sendFastEmbed(longRes, EmbedType.ERROR)
                return@execute
            }

            sendFastEmbed(formatWithKey("Result" to encodedMessage, false), EmbedType.SUCCESS)
        }

        execute(
            Argument(
                usage = "decode",
                type = "<Message 2 - $encodedMessageLimit characters>",
                isVariableLength = true
            )
        ) {
            try {
                val decodedMessage = Bottom.decode(getContent(2))

                if (content.length !in 2..encodedMessageLimit) {
                    sendFastEmbed(errMessage, EmbedType.ERROR)
                    return@execute
                }

                if (decodedMessage.length > encodedMessageLimit) {
                    sendFastEmbed(longRes, EmbedType.ERROR)
                    return@execute
                }

                sendFastEmbed(formatWithKey("Result" to decodedMessage, false), EmbedType.SUCCESS)
            } catch (e: TranslationError) {
                sendFastEmbed("You cannot decrypt this message: ${e.why}", EmbedType.ERROR)
            }
        }
    }
}