/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.enums.EmbedTemplate;

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
}
