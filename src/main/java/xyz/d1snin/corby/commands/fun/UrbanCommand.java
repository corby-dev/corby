/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Corby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package xyz.d1snin.corby.commands.fun;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import jdk.nashorn.api.scripting.URLReader;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrbanCommand extends Command {

  public UrbanCommand() {
    this.alias = "urban";
    this.description = "Defines a word from urbandictionary.com.";
    this.category = Category.FUN;
    this.usages = new String[] {"<Phrase>"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {

    OtherUtils.sendLoadingAndEdit(
        e,
        () -> {
          JsonArray array;
          MessageEmbed errorMessage =
              Embeds.create(
                  EmbedTemplate.ERROR, e.getAuthor(), "Could not find this phrase.", e.getGuild());
          try {
            array =
                JsonParser.parseReader(
                        new URLReader(
                            new URL(
                                String.format(
                                    "https://api.urbandictionary.com/v0/define?term=%s",
                                    getArgsString(1, e.getMessage()).replaceAll("\\s+", "%20")))))
                    .getAsJsonObject()
                    .get("list")
                    .getAsJsonArray();

          } catch (MalformedURLException | JsonIOException exception) {
            return errorMessage;
          }

          assert array != null;
          if (array.size() == 0) {
            return errorMessage;
          }

          String definition =
              array
                  .get(0)
                  .getAsJsonObject()
                  .get("definition")
                  .getAsString()
                  .replace("[", "")
                  .replace("]", "");

          return (definition.length() > 2000
              ? Embeds.create(
                  EmbedTemplate.ERROR,
                  e.getAuthor(),
                  String.format(
                      "It seems the definition is too big, you can see it [here](%s).",
                      array.get(0).getAsJsonObject().get("permalink").getAsString()),
                  e.getGuild())
              : Embeds.create(
                  EmbedTemplate.SUCCESS,
                  e.getAuthor(),
                  OtherUtils.formatMessageKeyText("Definition", definition, false),
                  e.getGuild()));
        });
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length > 1;
  }
}
