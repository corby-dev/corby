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

package xyz.d1snin.corby.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.model.EmbedTemplate;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;

public class Embeds {
  public static MessageEmbed create(
      EmbedTemplate template,
      User u,
      String description,
      @Nullable Guild guild,
      @Nullable String imageUrl,
      @Nullable String thumbnailUrl) {

    Color color = null;
    Emote emote = null;
    switch (template) {
      case ERROR:
        color = Corby.getConfig().getErrorColor();
        emote = Corby.getShards().getEmoteById(Corby.getConfig().getEmoteError());
        break;
      case DEFAULT:
        color = Corby.getConfig().getDefaultColor();
        break;
      case SUCCESS:
        color = Corby.getConfig().getSuccessColor();
        emote = Corby.getShards().getEmoteById(Corby.getConfig().getEmoteSuccess());
        break;
      default:
    }

    EmbedBuilder builder =
        new EmbedBuilder()
            .setColor(color)
            .setDescription(
                (emote == null
                        ? ""
                        : guild == null
                            ? emote.getAsMention()
                            : Objects.requireNonNull(guild.getBotRole())
                                    .getPermissions()
                                    .contains(Permission.MESSAGE_EXT_EMOJI)
                                ? emote.getAsMention()
                                : "")
                    + " "
                    + description)
            .setFooter(
                Corby.getConfig().getBotName()
                    + " | "
                    + Thread.currentThread().getName()
                    + " | "
                    + u.getAsTag(),
                Corby.getConfig().getBotPfpUrl());

    if (imageUrl != null) {
      builder.setImage(imageUrl);
    }

    if (thumbnailUrl != null) {
      builder.setThumbnail(thumbnailUrl);
    }

    return builder.build();
  }

  public static MessageEmbed create(EmbedTemplate template, User u, String description) {
    return create(template, u, description, null, null, null);
  }

  public static MessageEmbed create(
      EmbedTemplate template, User u, String description, Guild guild) {
    return create(template, u, description, guild, null, null);
  }

  public static MessageEmbed create(
      EmbedTemplate template, User u, String description, Guild guild, String imageUrl) {
    return create(template, u, description, guild, imageUrl, null);
  }

  public static MessageEmbed create(EmbedTemplate template, String description, CommandUtil util) {
    return create(template, util.getAuthor(), description, util.getGuild());
  }
}
