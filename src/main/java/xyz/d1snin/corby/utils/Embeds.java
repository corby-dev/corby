/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.enums.EmbedTemplate;

import java.awt.*;

public class Embeds {
  public static MessageEmbed create(EmbedTemplate template, User u, String description) {

    Color color = null;
    String emote = null;
    switch (template) {
      case ERROR:
        color = Corby.config.errorColor;
        emote = Corby.config.emoteError;
        break;
      case DEFAULT:
        color = Corby.config.defaultColor;
        break;
      case SUCCESS:
        color = Corby.config.successColor;
        emote = Corby.config.emoteSuccess;
        break;
      default:
    }

    return new EmbedBuilder()
        .setColor(color)
        .setDescription((emote == null ? "" : emote) + " " + description)
        .setFooter(
            Corby.config.botName + " | " + Thread.currentThread().getName() + " | " + u.getAsTag(), Corby.config.botPfpUrl)
        .build();
  }
}
