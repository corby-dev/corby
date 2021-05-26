/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.database.managers.MongoStarboardManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.model.database.Starboard;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

public class StarboardCommand extends Command {

  public StarboardCommand() {
    this.alias = "starboard";
    this.description = "Starboard settings";
    this.category = Category.SETTINGS;
    this.usages =
        new String[] {
          "alias", "channel <#channel>", "stars <Stars Count 1 - 100>", "enable", "disable"
        };

    this.longDescription =
        "Starboard is a channel where messages will be sent on which the number of star reactions will be equal to the specified one, so users will be able to mark messages they like.";
    this.permissions = new Permission[] {Permission.ADMINISTRATOR};
    this.botPermissions = new Permission[] {Permission.MESSAGE_ADD_REACTION};
  }

  private static void onNotConfigured(MessageReceivedEvent e) {
    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.ERROR,
                e.getAuthor(),
                String.format(
                    "Starboard is not configured on your server, use `%sstarboard channel <#channel>` to configure starboard.",
                    MongoPrefixManager.getPrefix(e.getGuild())),
                e.getGuild()))
        .queue();
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {

    final String sbInfo =
        "Starboard is enabled on your server!\nRequired number of stars: %d\nChannel for starboard: %s";
    final String sbNotEnabled = "Starboard is not enabled on your server.";
    final String sbAlreadyEnabled = "Starboard is already enabled on your server.";
    final String sbEnabled = "Starboard has been successfully enabled on your server!";
    final String sbAlreadyDisabled = "Starboard is already disabled on your server.";
    final String sbDisabled = "Starboard has been successfully disabled on your server!";
    final String sbChannelAlreadyInst = "Starboard channel is already set to this channel.";
    final String sbChannelInstalled = "Starboard successfully installed on the channel %s";
    final String sbStars = "The number of stars for the message has been successfully updated.";
    final String sbStarsAlready =
        "The number of stars for the message is already set to this value.";

    Starboard starboard = MongoStarboardManager.getStarboard(e.getGuild());

    if (args.length < 2) {
      if (starboard == null) {
        onNotConfigured(e);
        return;
      }
      if (!starboard.isStatus()) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), sbNotEnabled, e.getGuild()))
            .queue();
        return;
      }

      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.DEFAULT,
                  e.getAuthor(),
                  String.format(
                      sbInfo, starboard.getStars(), starboard.getChannel().getAsMention()),
                  e.getGuild()))
          .queue();
      return;
    }

    switch (args[1].toLowerCase()) {
      case "enable":
        if (starboard == null) {
          onNotConfigured(e);
          return;
        }

        if (starboard.isStatus()) {
          e.getTextChannel()
              .sendMessage(
                  Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), sbAlreadyEnabled, e.getGuild()))
              .queue();
          return;
        }

        starboard.setStatus(true);
        MongoStarboardManager.writeStarboard(starboard);
        e.getTextChannel()
            .sendMessage(
                Embeds.create(EmbedTemplate.SUCCESS, e.getAuthor(), sbEnabled, e.getGuild()))
            .queue();

        break;

      case "disable":
        if (starboard == null) {
          onNotConfigured(e);
          return;
        }

        if (!starboard.isStatus()) {
          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.ERROR, e.getAuthor(), sbAlreadyDisabled, e.getGuild()))
              .queue();
          return;
        }

        starboard.setStatus(false);
        MongoStarboardManager.writeStarboard(starboard);
        e.getTextChannel()
            .sendMessage(
                Embeds.create(EmbedTemplate.SUCCESS, e.getAuthor(), sbDisabled, e.getGuild()))
            .queue();

        break;

      case "channel":
        if (starboard != null
            && e.getMessage().getMentionedChannels().contains(starboard.getChannel())) {
          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.ERROR, e.getAuthor(), sbChannelAlreadyInst, e.getGuild()))
              .queue();
          return;
        }

        TextChannel channel = e.getMessage().getMentionedChannels().get(0);

        if (starboard == null) {
          MongoStarboardManager.writeStarboard(
              new Starboard(
                  e.getGuild(),
                  channel,
                  Corby.config.getDefaultStarboardStars(),
                  Corby.config.isDefaultStarboardStatus()));
        } else {
          starboard.setChannel(channel);
          MongoStarboardManager.writeStarboard(starboard);
        }

        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.SUCCESS,
                    e.getAuthor(),
                    String.format(
                        sbChannelInstalled,
                        e.getMessage().getMentionedChannels().get(0).getAsMention()),
                    e.getGuild()))
            .queue();
        break;

      case "stars":
        int stars;

        stars = Integer.parseInt(args[2]);

        if (starboard == null) {
          onNotConfigured(e);
          return;
        }

        if (!starboard.isStatus()) {
          e.getTextChannel()
              .sendMessage(
                  Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), sbNotEnabled, e.getGuild()))
              .queue();
          return;
        }

        if (starboard.getStars() == stars) {
          e.getTextChannel()
              .sendMessage(
                  Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), sbStarsAlready, e.getGuild()))
              .queue();
          return;
        }

        starboard.setStars(stars);
        MongoStarboardManager.writeStarboard(starboard);
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.SUCCESS, e.getAuthor(), sbStars, e.getGuild()))
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
