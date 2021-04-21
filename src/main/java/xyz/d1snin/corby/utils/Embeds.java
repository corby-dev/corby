package xyz.d1snin.corby.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;

public class Embeds {
    public static MessageEmbed createDefaultEmbed(MessageReceivedEvent e, String description) {
        return new EmbedBuilder()
                .setColor(Corby.DEFAULT_COLOR)
                .setDescription(description)
                .setFooter(e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(), e.getAuthor().getEffectiveAvatarUrl()).build();
    }
    public static MessageEmbed createDefaultErrorEmbed(MessageReceivedEvent e, String description) {
        return new EmbedBuilder()
                .setColor(Corby.ERROR_COLOR)
                .setDescription(description)
                .setFooter(e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(), e.getAuthor().getEffectiveAvatarUrl()).build();
    }
}
