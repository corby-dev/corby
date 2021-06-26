/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands.`fun`

import com.google.gson.JsonIOException
import com.google.gson.JsonParser
import jdk.nashorn.api.scripting.URLReader
import xyz.d1snin.corby.commands.Command
import xyz.d1snin.corby.model.Argument
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.model.EmbedType
import xyz.d1snin.corby.util.formatWithKey
import java.net.URL

object UrbanCommand : Command(
    usage = "urban",
    description = "Defines a word from urbandictionary.com",
    category = Category.FUN,
    cooldown = 10
) {
    init {
        execute(
            Argument(
                type = "<Phrase>",
                isVariableLength = true
            )
        ) {
            sendLoadingMessageAndEdit {
                val errMessage = createFastEmbed("Could not find this phrase.", EmbedType.ERROR)

                try {
                    JsonParser.parseReader(
                        URLReader(
                            URL(
                                "https://api.urbandictionary.com/v0/define?term=${getArgVal(0)}".replace(
                                    "\\s+".toRegex(),
                                    "%20"
                                )
                            )
                        )
                    ).asJsonObject["list"].asJsonArray.let {

                        if (it.size() == 0) {
                            return@sendLoadingMessageAndEdit errMessage
                        }

                        val def = it.first()
                            .asJsonObject["definition"]
                            .asString
                            .replace("[", "")
                            .replace("]", "")

                        return@sendLoadingMessageAndEdit if (def.length > 2000) {
                            createFastEmbed(
                                "The definition is too big, you can see it [here](${it.first().asJsonObject["permalink"].asString}).",
                                EmbedType.ERROR
                            )
                        } else {
                            createFastEmbed(formatWithKey("Definition" to def, false), EmbedType.SUCCESS)
                        }
                    }
                } catch (_: JsonIOException) {
                    return@sendLoadingMessageAndEdit errMessage
                }
            }
        }
    }
}