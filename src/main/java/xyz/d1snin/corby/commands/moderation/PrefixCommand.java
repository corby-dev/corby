package xyz.d1snin.corby.commands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.ColorUtil;

import java.util.Collections;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        this.aliases = Collections.singletonList("prefix");
        this.permissions = new Permission[] {
                Permission.ADMINISTRATOR
        };
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {

        String currentPrefix = GuildSettingsManager.getGuildPrefix(e.getGuild());

        if (args.length < 2) {
            e.getTextChannel().sendMessage(new EmbedBuilder()
                    .setColor(ColorUtil.getDefaultColor())
                    .setDescription("Current prefix is `" + currentPrefix + "`\n" +
                            "Use " + currentPrefix + "prefix <New Prefix> to change it!")
                    .setFooter(e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(), e.getAuthor().getEffectiveAvatarUrl())
                    .build()).queue();
            return;
        }

        String newPrefix = args[1];

        if (currentPrefix.equals(newPrefix)) {
            e.getTextChannel().sendMessage(new EmbedBuilder()
                    .setColor(ColorUtil.getErrorColor())
                    .setDescription("Bot prefix is already " + newPrefix)
                    .setFooter(e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(), e.getAuthor().getEffectiveAvatarUrl())
                    .build()).queue();
            return;
        }

        if (newPrefix.length() > 5) {
            e.getTextChannel().sendMessage(new EmbedBuilder()
                    .setColor(ColorUtil.getErrorColor())
                    .setDescription("The prefix cannot be more than 5 characters.")
                    .setFooter(e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(), e.getAuthor().getEffectiveAvatarUrl())
                    .build()).queue();
            return;
        }

        GuildSettingsManager.setGuildPrefix(e.getGuild(), newPrefix);

        e.getTextChannel().sendMessage(new EmbedBuilder()
                .setColor(ColorUtil.getDefaultColor())
                .setDescription("The prefix was successfully changed to `" + newPrefix + "`" +
                        "\nExample of usage: " + newPrefix + "ping")
                .setFooter(e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(), e.getAuthor().getEffectiveAvatarUrl())
                .build()).queue();
    }
}
