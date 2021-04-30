package xyz.d1snin.corby.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.event.ReactionUpdateEvent;

public class Embeds {
    public static MessageEmbed create(EmbedTemplate template, User u, String description) {
        switch (template) {
            case ERROR:
                return new EmbedBuilder()
                        .setColor(Corby.config.error_color)
                        .setDescription(description)
                        .setFooter(u.getName() + " | ID: " + u.getId(), u.getEffectiveAvatarUrl()).build();
            case DEFAULT:
                return new EmbedBuilder()
                        .setColor(Corby.config.default_color)
                        .setDescription(description)
                        .setFooter(u.getName() + " | ID: " + u.getId(), u.getEffectiveAvatarUrl()).build();
            case SUCCESS:
                return new EmbedBuilder()
                        .setColor(Corby.config.success_color)
                        .setDescription(description)
                        .setFooter(u.getName() + " | ID: " + u.getId(), u.getEffectiveAvatarUrl()).build();
        }
        return create(EmbedTemplate.DEFAULT, u, description);
    }

    public static void createAndSendWithReaction(EmbedTemplate template, User u, TextChannel c, String unicode, String description) {
        c.sendMessage(create(template, u, description))
                .queue((message -> message.addReaction(unicode)
                        .queue((message2) -> ReactionUpdateEvent.addListener(message.getReactions().get(0)))));
    }
}
