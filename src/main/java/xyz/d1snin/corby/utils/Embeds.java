package xyz.d1snin.corby.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import xyz.d1snin.corby.Corby;

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

  public static void createAndSendWithReaction(
      EmbedTemplate template, User u, TextChannel c, String unicode, String description) {
    c.sendMessage(create(template, u, description))
        .queue((message -> message.addReaction(Corby.config.emoteTrash).queue()));
  }
}
