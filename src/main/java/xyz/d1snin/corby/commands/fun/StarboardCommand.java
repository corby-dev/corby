/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.fun;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.util.Objects;

public class StarboardCommand extends Command {

  public StarboardCommand() {
    this.use = "starboard";
    this.permissions = new Permission[] {Permission.ADMINISTRATOR};
    this.botPermissions = new Permission[] {Permission.MESSAGE_ADD_REACTION};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {

    final String sbInfo =
        "Starboard is enabled on your server!\nRequired number of stars: %d\nChannel for starboard: %s";
    final String sbNotEnabled =
        "It seems starboard is not enabled on your server, use `%sstarboard enable` to enable starboard.";
    final String sbNotConfigured =
        "It seems starboard is not configured on your server, use `%sstarboard channel <#channel>` to configure starboard.";
    final String sbAlreadyEnabled = "It seems starboard is already enabled on your server.";
    final String sbEnabled = "Starboard has been successfully enabled on your server!";
    final String sbAlreadyDisabled = "It seems starboard is already disabled on your server.";
    final String sbDisabled = "Starboard has been successfully disabled on your server!";
    final String sbIncChannel = "Please use the following syntax: `%sstarboard channel <#channel>`";
    final String sbChannelAlreadyInst =
        "It looks like the channel for the starboard is already installed.";
    final String sbChannelInstalled = "Starboard successfully installed on the channel %s";
    final String sbStarsInc =
        "Please use the following syntax: `%sstarboard stars <value from 1 to 100>`";
    final String sbStars = "The number of stars for the message has been successfully updated.";

    if (args.length < 2) {
      if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
        Embeds.createAndSendWithReaction(
            EmbedTemplate.ERROR,
            e.getAuthor(),
            e.getTextChannel(),
            Corby.config.emoteTrash,
            String.format(sbNotEnabled, GuildSettingsManager.getGuildPrefix(e.getGuild())));
      } else {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.DEFAULT,
                    e.getAuthor(),
                    String.format(
                        sbInfo,
                        GuildSettingsManager.getGuildStarboardStars(e.getGuild()),
                        Objects.requireNonNull(
                                GuildSettingsManager.getGuildStarboardChannel(e.getGuild()))
                            .getAsMention())))
            .queue();
      }
      return;
    }

    switch (args[1].toLowerCase()) {
      case "enable":
        if (GuildSettingsManager.getGuildStarboardChannel(e.getGuild()) == null) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbNotConfigured, GuildSettingsManager.getGuildPrefix(e.getGuild())));
          return;
        }

        if (GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              sbAlreadyEnabled);
          return;
        }

        GuildSettingsManager.setGuildStarboardIsEnabled(e.getGuild(), true);
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), sbEnabled))
            .queue();

        break;

      case "disable":
        if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              sbAlreadyDisabled);
          return;
        }

        GuildSettingsManager.setGuildStarboardIsEnabled(e.getGuild(), false);
        Embeds.createAndSendWithReaction(
            EmbedTemplate.ERROR,
            e.getAuthor(),
            e.getTextChannel(),
            Corby.config.emoteTrash,
            sbDisabled);

        break;

      case "channel":
        if (e.getMessage().getMentionedChannels().isEmpty()) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbIncChannel, GuildSettingsManager.getGuildPrefix(e.getGuild())));
          return;
        }

        if (GuildSettingsManager.getGuildStarboardChannel(e.getGuild()) != null
            && Objects.requireNonNull(GuildSettingsManager.getGuildStarboardChannel(e.getGuild()))
                    .getIdLong()
                == e.getMessage().getMentionedChannels().get(0).getIdLong()) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              sbChannelAlreadyInst);
          return;
        }

        GuildSettingsManager.setGuildStarboardChannel(
            e.getGuild(), e.getMessage().getMentionedChannels().get(0));
        Embeds.createAndSendWithReaction(
            EmbedTemplate.ERROR,
            e.getAuthor(),
            e.getTextChannel(),
            Corby.config.emoteTrash,
            String.format(
                sbChannelInstalled, e.getMessage().getMentionedChannels().get(0).getAsMention()));
        break;

      case "stars":
        if (args.length < 3) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbStarsInc, GuildSettingsManager.getGuildPrefix(e.getGuild())));
          return;
        }

        int stars;

        try {
          stars = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbStarsInc, GuildSettingsManager.getGuildPrefix(e.getGuild())));
          return;
        }

        if (stars > 100 || stars < 1) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbStarsInc, GuildSettingsManager.getGuildPrefix(e.getGuild())));
          return;
        }

        if (!GuildSettingsManager.getGuildStarboardIsEnabled(e.getGuild())) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbNotEnabled, GuildSettingsManager.getGuildPrefix(e.getGuild())));
          return;
        }

        if (GuildSettingsManager.getGuildStarboardChannel(e.getGuild()) == null) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbNotConfigured, GuildSettingsManager.getGuildPrefix(e.getGuild())));
          return;
        }

        GuildSettingsManager.setGuildStarboardStars(e.getGuild(), stars);
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), sbStars))
            .queue();

      default:
    }
  }
}
