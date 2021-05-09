/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.enums.EmbedTemplate;

public class Embeds {
  public static MessageEmbed create(EmbedTemplate template, User u, String description) {
    switch (template) {
      case ERROR:
        return new EmbedBuilder()
            .setColor(Corby.config.errorColor)
            .setDescription(description)
            .setFooter(u.getName() + " | ID: " + u.getId(), u.getEffectiveAvatarUrl())
            .build();
      case DEFAULT:
        return new EmbedBuilder()
            .setColor(Corby.config.defaultColor)
            .setDescription(description)
            .setFooter(u.getName() + " | ID: " + u.getId(), u.getEffectiveAvatarUrl())
            .build();
      case SUCCESS:
        return new EmbedBuilder()
            .setColor(Corby.config.successColor)
            .setDescription(description)
            .setFooter(u.getName() + " | ID: " + u.getId(), u.getEffectiveAvatarUrl())
            .build();
      default:
    }
    return create(EmbedTemplate.DEFAULT, u, description);
  }
}
