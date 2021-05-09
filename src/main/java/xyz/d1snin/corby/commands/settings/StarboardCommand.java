/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.database.managers.StarboardManager;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.sql.SQLException;
import java.util.Objects;

public class StarboardCommand extends Command {

  public StarboardCommand() {
    this.use = "starboard";
    this.permissions = new Permission[] {Permission.ADMINISTRATOR};
    this.botPermissions = new Permission[] {Permission.MESSAGE_ADD_REACTION};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws SQLException {

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
      if (!StarboardManager.getStarboardIsEnabled(e.getGuild())) {
        Embeds.createAndSendWithReaction(
            EmbedTemplate.ERROR,
            e.getAuthor(),
            e.getTextChannel(),
            Corby.config.emoteTrash,
            String.format(sbNotEnabled, PrefixManager.getPrefix(e.getGuild())));
      } else {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.DEFAULT,
                    e.getAuthor(),
                    String.format(
                        sbInfo,
                        StarboardManager.getStarboardStars(e.getGuild()),
                        Objects.requireNonNull(
                                StarboardManager.getStarboardChannel(e.getGuild()))
                            .getAsMention())))
            .queue();
      }
      return;
    }

    switch (args[1].toLowerCase()) {
      case "enable":
        if (StarboardManager.getStarboardChannel(e.getGuild()) == null) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbNotConfigured, PrefixManager.getPrefix(e.getGuild())));
          return;
        }

        if (StarboardManager.getStarboardIsEnabled(e.getGuild())) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              sbAlreadyEnabled);
          return;
        }

        StarboardManager.setStarboardIsEnabled(e.getGuild(), true);
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), sbEnabled))
            .queue();

        break;

      case "disable":
        if (!StarboardManager.getStarboardIsEnabled(e.getGuild())) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              sbAlreadyDisabled);
          return;
        }

        StarboardManager.setStarboardIsEnabled(e.getGuild(), false);
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
              String.format(sbIncChannel, PrefixManager.getPrefix(e.getGuild())));
          return;
        }

        if (StarboardManager.getStarboardChannel(e.getGuild()) != null
            && Objects.requireNonNull(StarboardManager.getStarboardChannel(e.getGuild()))
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

        StarboardManager.setStarboardChannel(
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
              String.format(sbStarsInc, PrefixManager.getPrefix(e.getGuild())));
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
              String.format(sbStarsInc, PrefixManager.getPrefix(e.getGuild())));
          return;
        }

        if (stars > 100 || stars < 1) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbStarsInc, PrefixManager.getPrefix(e.getGuild())));
          return;
        }

        if (!StarboardManager.getStarboardIsEnabled(e.getGuild())) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbNotEnabled, PrefixManager.getPrefix(e.getGuild())));
          return;
        }

        if (StarboardManager.getStarboardChannel(e.getGuild()) == null) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(sbNotConfigured, PrefixManager.getPrefix(e.getGuild())));
          return;
        }

        StarboardManager.setStarboardStars(e.getGuild(), stars);
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), sbStars))
            .queue();

      default:
    }
  }
}
