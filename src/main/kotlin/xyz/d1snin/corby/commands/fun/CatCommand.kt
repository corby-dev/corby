/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands.`fun`

import com.google.gson.JsonParser
import jdk.nashorn.api.scripting.URLReader
import xyz.d1snin.corby.commands.Command
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.model.EmbedType
import xyz.d1snin.corby.util.createEmbed
import java.net.URL

object CatCommand : Command(
    usage = "cat",
    description = "Gives you a picture of a cat",
    category = Category.FUN,
    cooldown = 10
) {
    init {
        default {
            sendLoadingMessageAndEdit {
                createEmbed(getFact(), guild, author, getPicture(), type = EmbedType.SUCCESS)
            }
        }
    }

    private fun getFact(): String {
        while (true) {
            JsonParser.parseReader(
                URLReader(
                    URL("https://cat-fact.herokuapp.com/facts/random?animal_type=cat&amount=1")
                )
            ).run {
                asJsonObject["status"].asJsonObject["verified"].let {
                    if (!it.isJsonNull && it.asBoolean) {
                        return asJsonObject["text"].asString
                    }
                }
            }
        }
    }

    private fun getPicture(): String {
        return JsonParser.parseReader(URLReader(URL("https://api.thecatapi.com/v1/images/search")))
            .asJsonArray.first()
            .asJsonObject["url"]
            .asString
    }
}