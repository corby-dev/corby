package xyz.d1snin.corby.commands.fun;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.Embeds;

import java.util.Objects;

public class StarboardCommand extends Command {

    public StarboardCommand() {
        this.aliases = "starboard";
        this.permissions = new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {

        if (args.length < 2) {
            if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "It seems starboard is not enabled on your server, use `" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "starboard enable` to enable starboard."))
                        .queue((message -> message.addReaction(Corby.config.emote_trash).queue())
                        );
            } else {
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e,
                        "Starboard is enabled on your server!" +
                                "\nRequired number of stars: " + GuildSettingsManager.getGuildStarboardStars(e.getGuild()) +
                                "\nChannel for starboard: " + Objects.requireNonNull(GuildSettingsManager.getGuildStarboardChannel(e.getGuild())).getAsMention())).queue();
            }
            return;
        }

        if (args[1].equalsIgnoreCase("enable")) {
            if (GuildSettingsManager.getGuildStarboardChannel(e.getGuild()) == null) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "It seems starboard is not configured on your server, use `" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "starboard set_channel <#channel>` to configure starboard."))
                        .queue((message -> message.addReaction(Corby.config.emote_trash).queue())
                        );
            } else if (GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "It seems starboard is already enabled on your server."))
                        .queue((message -> message.addReaction(Corby.config.emote_trash).queue())
                        );
            } else {
                GuildSettingsManager.setGuildStarboardIsEnabled(e.getGuild(), true);
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e,
                        "Starboard has been successfully enabled on your server!")).queue();
            }
        }

        if (args[1].equalsIgnoreCase("disable")) {
            if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "It seems starboard is already disabled on your server."))
                        .queue((message -> message.addReaction(Corby.config.emote_trash).queue())
                        );
            } else {
                GuildSettingsManager.setGuildStarboardIsEnabled(e.getGuild(), false);
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e,
                        "Starboard has been successfully disabled on your server!")).queue();
            }
        }

        if (args[1].equalsIgnoreCase("set_channel") || args[1].equalsIgnoreCase("setchannel")) {
            if (e.getMessage().getMentionedChannels().isEmpty()) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "Please use the following syntax:" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "`starboard set_channel <#channel>`")).queue(
                        (message -> message.addReaction(Corby.config.emote_trash).queue())
                );
            } else if (!e.getGuild().getChannels().contains(e.getMessage().getMentionedChannels().get(0))) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "Please use the following syntax:" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "`starboard set_channel <#channel>`")).queue(
                        (message -> message.addReaction(Corby.config.emote_trash).queue())
                );
            } else if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "It seems starboard is not enabled on your server, use `" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "starboard enable` to enable starboard."))
                        .queue((message -> message.addReaction(Corby.config.emote_trash).queue())
                        );
            } else if (Objects.requireNonNull(GuildSettingsManager.getGuildStarboardChannel(e.getGuild())).getIdLong() == e.getMessage().getMentionedChannels().get(0).getIdLong()) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "It looks like the channel for the starboard is already installed."
                )).queue(
                        (message -> message.addReaction(Corby.config.emote_trash).queue())
                );
            } else {
                GuildSettingsManager.setGuildStarboardChannel(e.getGuild(), e.getMessage().getMentionedChannels().get(0));
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e,
                        "Starboard successfully installed on the channel " + e.getMessage().getMentionedChannels().get(0).getAsMention() + ".\n" +
                                "Now you can set the required number of stars for a message, use `" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "starboard set_stars <value from 1 to 100>`")).queue();
            }
        }

        if (args[1].equalsIgnoreCase("set_stars") || args[1].equalsIgnoreCase("setstars")) {
            if (args.length < 3) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "Please use the following syntax:" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "`starboard set_stars <value from 1 to 100>`")).queue(
                        (message -> message.addReaction(Corby.config.emote_trash).queue())
                );
                return;
            }

            int stars;

            try {
                stars = Integer.parseInt(args[2]);
            } catch (NumberFormatException formatException) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "Please use the following syntax:" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "`starboard set_stars <value from 1 to 100>`")).queue(
                        (message -> message.addReaction(Corby.config.emote_trash).queue())
                );
                return;
            }

            if (stars > 100 || stars < 1) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "Please use the following syntax:" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "`starboard set_stars <value from 1 to 100>`")).queue(
                        (message -> message.addReaction(Corby.config.emote_trash).queue())
                );
                return;
            }

            if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "It seems starboard is not enabled on your server, use `" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "starboard enable` to enable starboard."))
                        .queue((message -> message.addReaction(Corby.config.emote_trash).queue())
                        );
                return;
            }

            if (GuildSettingsManager.getGuildStarboardChannel(e.getGuild()) == null) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        "It seems starboard is not configured on your server, use `" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "starboard set_channel <#channel>` to configure starboard."))
                        .queue((message -> message.addReaction(Corby.config.emote_trash).queue())
                        );
            } else {
                GuildSettingsManager.setGuildStarboardStars(e.getGuild(), stars);
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e,
                        "The number of stars for the message has been successfully updated.")).queue();
            }
        }
    }
}
