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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class StealCommand extends Command {

  public StealCommand() {
    this.alias = "steal";
    this.description = "Uploads emoji from a link or other emoji to your server";
    this.category = Category.MISC;
    this.usages = new String[] {"<URL> <Name>", "<Emoji> <Name>"};

    this.permissions = new Permission[] {Permission.MANAGE_EMOTES};
    this.botPermissions = new Permission[] {Permission.MANAGE_EMOTES};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws IOException {
    final String nameSizeMessage = "Name must be between 1 and 32 characters in length.";
    final String invalidUrl = "Provided URL is invalid.";
    final String success = "The emote `:%s:` has been successfully added!";
    final String failure = "Something went wrong while adding an emote, please try again.";
    final String incorrectUrl = "This format is not supported.";

    final List<Emote> emotes = e.getMessage().getEmotes();

    final String name = args[2].toLowerCase();

    if (name.length() > 32 || name.length() < 1) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), nameSizeMessage, e.getGuild()))
          .queue();
      return;
    }

    URL url;

    if (emotes.isEmpty()) {

      if (!OtherUtils.isImage(args[1])) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), incorrectUrl, e.getGuild()))
            .queue();
        return;
      }

      try {
        url = new URL(args[1]);
      } catch (MalformedURLException malformedURLException) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), invalidUrl, e.getGuild()))
            .queue();
        return;
      }
    } else {
      url = new URL(emotes.get(0).getImageUrl());
    }

    URLConnection connection = url.openConnection();
    connection.setRequestProperty("User-Agent", "");

    try (InputStream stream = connection.getInputStream()) {
      e.getGuild()
          .createEmote(name, Icon.from(stream))
          .queue(
              successfully ->
                  e.getTextChannel()
                      .sendMessage(
                          Embeds.create(
                              EmbedTemplate.SUCCESS,
                              e.getAuthor(),
                              String.format(success, name),
                              e.getGuild()))
                      .queue(),
              fail ->
                  e.getTextChannel()
                      .sendMessage(
                          Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), failure, e.getGuild()))
                      .queue());
    } catch (FileNotFoundException exception) {
      e.getTextChannel()
          .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), invalidUrl, e.getGuild()))
          .queue();
    }
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length == 3;
  }
}
