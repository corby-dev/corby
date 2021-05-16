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
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.database.managers.StarboardManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.util.Objects;

public class StarboardCommand extends Command {

  public StarboardCommand() {
    this.alias = "starboard";
    this.description = "Starboard settings";
    this.category = Category.SETTINGS;
    this.usages =
        new String[] {
          "%sstarboard",
          "%sstarboard channel <#channel>",
          "%sstarboard stars <Stars Count 1 - 100>",
          "%sstarboard enable",
          "%sstarboard disable"
        };

    this.longDescription =
        "Starboard is a channel where messages will be sent on which the number of star reactions will be equal to the specified one, so users will be able to mark messages they like.";
    this.permissions = new Permission[] {Permission.ADMINISTRATOR};
    this.botPermissions = new Permission[] {Permission.MESSAGE_ADD_REACTION};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {

    final String sbInfo =
        "Starboard is enabled on your server!\nRequired number of stars: %d\nChannel for starboard: %s";
    final String sbNotEnabled = "It seems starboard is not enabled on your server.";
    final String sbNotConfigured =
        "It seems starboard is not configured on your server, use `%sstarboard channel <#channel>` to configure starboard.";
    final String sbAlreadyEnabled = "It seems starboard is already enabled on your server.";
    final String sbEnabled = "Starboard has been successfully enabled on your server!";
    final String sbAlreadyDisabled = "It seems starboard is already disabled on your server.";
    final String sbDisabled = "Starboard has been successfully disabled on your server!";
    final String sbChannelAlreadyInst =
        "It looks like the starboard channel is already set to this channel.";
    final String sbChannelInstalled = "Starboard successfully installed on the channel %s";
    final String sbStars = "The number of stars for the message has been successfully updated.";

    if (args.length < 2) {
      if (!StarboardManager.isConfigured(e.getGuild())) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    e.getAuthor(),
                    String.format(sbNotConfigured, PrefixManager.getPrefix(e.getGuild()))))
            .queue();
        return;
      }
      if (!StarboardManager.getStatus(e.getGuild())) {
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), sbNotEnabled))
            .queue();
        return;
      }

      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.DEFAULT,
                  e.getAuthor(),
                  String.format(
                      sbInfo,
                      StarboardManager.getStars(e.getGuild()),
                      Objects.requireNonNull(StarboardManager.getChannel(e.getGuild()))
                          .getAsMention())))
          .queue();
      return;
    }

    switch (args[1].toLowerCase()) {
      case "enable":
        if (!StarboardManager.isConfigured(e.getGuild())) {
          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.ERROR,
                      e.getAuthor(),
                      String.format(sbNotConfigured, PrefixManager.getPrefix(e.getGuild()))))
              .queue();
          return;
        }

        if (StarboardManager.getStatus(e.getGuild())) {
          e.getTextChannel()
              .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), sbAlreadyEnabled))
              .queue();
          return;
        }

        StarboardManager.setStatus(e.getGuild(), true);
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), sbEnabled))
            .queue();

        break;

      case "disable":
        if (!StarboardManager.isConfigured(e.getGuild())) {
          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.ERROR,
                      e.getAuthor(),
                      String.format(sbNotConfigured, PrefixManager.getPrefix(e.getGuild()))))
              .queue();
          return;
        }

        if (!StarboardManager.getStatus(e.getGuild())) {
          e.getTextChannel()
              .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), sbAlreadyDisabled))
              .queue();
          return;
        }

        StarboardManager.setStatus(e.getGuild(), false);
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), sbDisabled))
            .queue();

        break;

      case "channel":
        if (StarboardManager.isConfigured(e.getGuild())
            && e.getMessage()
                .getMentionedChannels()
                .contains(StarboardManager.getChannel(e.getGuild()))) {
          e.getTextChannel()
              .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), sbChannelAlreadyInst))
              .queue();
          return;
        }

        StarboardManager.setChannel(e.getGuild(), e.getMessage().getMentionedChannels().get(0));
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.DEFAULT,
                    e.getAuthor(),
                    String.format(
                        sbChannelInstalled,
                        e.getMessage().getMentionedChannels().get(0).getAsMention())))
            .queue();
        break;

      case "stars":
        int stars;

        stars = Integer.parseInt(args[2]);

        if (!StarboardManager.getStatus(e.getGuild())) {
          e.getTextChannel()
              .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), sbNotEnabled))
              .queue();
          return;
        }

        if (!StarboardManager.isConfigured(e.getGuild())) {
          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.ERROR,
                      e.getAuthor(),
                      String.format(sbNotConfigured, PrefixManager.getPrefix(e.getGuild()))))
              .queue();
          return;
        }

        StarboardManager.setStars(stars, e.getGuild());
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), sbStars))
            .queue();

      default:
    }
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    if (args.length > 3) {
      return false;
    }
    if (args.length > 1) {
      if (!args[1].equals("channel")
          && !args[1].equals("stars")
          && !args[1].equals("enable")
          && !args[1].equals("disable")) {
        return false;
      }
      if (args[1].equals("channel") && e.getMessage().getMentionedChannels().isEmpty()) {
        return false;
      }
      if (args[1].equals("stars") && args.length < 3) {
        return false;
      }
      if (args[1].equals("stars") && !OtherUtils.isNumeric(args[2])) {
        return false;
      }
      if (args[1].equals("stars")) {
        int stars = Integer.parseInt(args[2]);
        return stars <= 100 && stars >= 1;
      }
    }
    return true;
  }
}
