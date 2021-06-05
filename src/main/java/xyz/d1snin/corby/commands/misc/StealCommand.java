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

package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.model.Argument;
import xyz.d1snin.corby.model.Category;
import xyz.d1snin.corby.model.EmbedTemplate;
import xyz.d1snin.corby.utils.OtherUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class StealCommand extends Command {

  public StealCommand() {
    this.usage = "steal";
    this.description = "Uploads emoji from a link or other emoji to your server";
    this.category = Category.MISC;

    this.userPerms = new Permission[] {Permission.MANAGE_EMOTES};
    this.botPerms = new Permission[] {Permission.MANAGE_EMOTES};

    arg(
        u -> {
          final List<Emote> emotes = u.getMessage().getEmotes();
          final String name = u.getArgumentValue(1);

          final String invalidUrl = "Provided URL is invalid.";

          if (name.length() > 32 || name.length() < 1) {
            u.sendEmbed(EmbedTemplate.ERROR, "Name must be between 1 and 32 characters in length.");
            return;
          }

          OtherUtils.sendLoadingAndEdit(
              u.getEvent(),
              () -> {
                try {

                  URL url;

                  if (emotes.isEmpty()) {

                    if (!OtherUtils.isImage(u.getArgumentValue(0))) {
                      return u.createEmbed(EmbedTemplate.ERROR, "This format is not supported.");
                    }

                    try {
                      url = new URL(u.getArgumentValue(0));
                    } catch (MalformedURLException malformedURLException) {
                      return u.createEmbed(EmbedTemplate.ERROR, invalidUrl);
                    }

                  } else {

                    url = new URL(emotes.get(0).getImageUrl());
                  }

                  URLConnection connection = url.openConnection();
                  connection.setRequestProperty("User-Agent", "");

                  try (InputStream stream = connection.getInputStream()) {
                    u.getGuild().createEmote(name, Icon.from(stream)).queue();
                  }

                } catch (IOException exception) {
                  exception.printStackTrace();
                }

                return u.createEmbed(
                    EmbedTemplate.SUCCESS,
                    String.format("The emote `:%s:` has been successfully added!", name));
              });
        },
        new Argument(null, "<URL or Emote>", false, false),
        new Argument(null, "<Name>", false, false));
  }
}
