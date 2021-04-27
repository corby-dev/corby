package xyz.d1snin.corby.commands.fun;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.Embeds;

public class StarboardCommand extends Command {

    public StarboardCommand() {
        this.aliases = "starboard";
        this.permissions = new Permission[]{Permission.ADMINISTRATOR};
        this.botPermissions = new Permission[] {
                Permission.MESSAGE_ADD_REACTION
        };
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {

        final String sbInfo = "Starboard is enabled on your server!\nRequired number of stars: %d\nChannel for starboard: %s";
        final String sbNotEnabled = "It seems starboard is not enabled on your server, use `%sstarboard enable` to enable starboard.";
        final String sbNotConfigured = "It seems starboard is not configured on your server, use `%sstarboard channel <#channel>` to configure starboard.";
        final String sbAlreadyEnabled = "It seems starboard is already enabled on your server.";
        final String sbEnabled = "Starboard has been successfully enabled on your server!";
        final String sbAlreadyDisabled = "It seems starboard is already disabled on your server.";
        final String sbDisabled = "Starboard has been successfully disabled on your server!";
        final String sbIncChannel = "Please use the following syntax: `%sstarboard channel <#channel>`";
        final String sbChannelAlreadyInst = "It looks like the channel for the starboard is already installed.";
        final String sbChannelInstalled = "Starboard successfully installed on the channel %s";
        final String sbStarsInc = "Please use the following syntax: `%sstarboard stars <value from 1 to 100>`";
        final String sbStars = "The number of stars for the message has been successfully updated.";

        if (args.length < 2) {
            if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e,
                        String.format(sbNotEnabled, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                        .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
            } else {
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, String.format(sbInfo, GuildSettingsManager.getGuildStarboardStars(e.getGuild()),
                        GuildSettingsManager.getGuildStarboardChannel(e.getGuild()).getAsMention()))).queue();
            }
            return;
        }

        switch (args[1].toLowerCase()) {
            case "enable":
                if (GuildSettingsManager.getGuildStarboardChannel(e.getGuild()) == null) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(sbNotConfigured, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                if (GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, sbAlreadyEnabled))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                GuildSettingsManager.setGuildStarboardIsEnabled(e.getGuild(), true);
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, sbEnabled)).queue();

                break;

            case "disable":
                if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, sbAlreadyDisabled))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                GuildSettingsManager.setGuildStarboardIsEnabled(e.getGuild(), false);
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, sbDisabled))
                        .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));

                break;

            case "channel":
                if (e.getMessage().getMentionedChannels().isEmpty()) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(sbIncChannel, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                if (GuildSettingsManager.getGuildStarboardChannel(e.getGuild()) != null &&
                        GuildSettingsManager.getGuildStarboardChannel(e.getGuild()).getIdLong() == e.getMessage().getMentionedChannels().get(0).getIdLong()) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, sbChannelAlreadyInst))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                GuildSettingsManager.setGuildStarboardChannel(e.getGuild(), e.getMessage().getMentionedChannels().get(0));
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, String.format(sbChannelInstalled, e.getMessage().getMentionedChannels().get(0).getAsMention()))).queue();

                break;

            case "stars":
                if (args.length < 3) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(sbStarsInc, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                int stars;

                try {
                    stars = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(sbStarsInc, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                if (stars > 100 || stars < 1) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(sbStarsInc, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(sbNotEnabled, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                if (GuildSettingsManager.getGuildStarboardChannel(e.getGuild()) == null) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(sbNotConfigured, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                            .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                GuildSettingsManager.setGuildStarboardStars(e.getGuild(), stars);
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, sbStars)).queue();
        }
    }
}
