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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jdk.nashorn.api.scripting.URLReader;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.model.Category;
import xyz.d1snin.corby.model.EmbedType;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class CatCommand extends Command {

  public CatCommand() {
    this.usage = "cat";
    this.description = "Gives you a picture of a cat";
    this.category = Category.FUN;

    execute(
        u ->
            OtherUtils.sendLoadingAndEdit(
                u,
                () -> {
                  try {
                    return Embeds.create(
                        EmbedType.DEFAULT, u.getAuthor(), getFact(), u.getGuild(), getPicture());
                  } catch (MalformedURLException exception) {
                    exception.printStackTrace();
                  }

                  return null;
                }));
  }

  private String getFact() throws MalformedURLException {
    while (true) {

      JsonElement root =
          JsonParser.parseReader(
              new URLReader(
                  new URL("https://cat-fact.herokuapp.com/facts/random?animal_type=cat&amount=1")));

      JsonElement verifiedElement =
          root.getAsJsonObject().get("status").getAsJsonObject().get("verified");

      if (!verifiedElement.isJsonNull() && verifiedElement.getAsBoolean()) {
        return root.getAsJsonObject().get("text").getAsString();
      }
    }
  }

  private String getPicture() throws MalformedURLException {
    return JsonParser.parseReader(
            new URLReader(new URL("https://api.thecatapi.com/v1/images/search")))
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("url")
        .getAsString();
  }
}
