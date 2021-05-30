/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Corby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import xyz.d1snin.corby.model.Starboard;
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

    final String sbInfo = "Starboard is enabled on your server!\n\n%s\n%s";
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
                      sbInfo,
                      OtherUtils.formatMessageKeyText(
                          "Required number of stars", String.valueOf(starboard.getStars())),
                      OtherUtils.formatMessageKeyText(
                          "Channel for starboard",
                          Objects.requireNonNull(
                                  Corby.getShards().getTextChannelById(starboard.getChannel()))
                              .getAsMention())),
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
            && e.getMessage()
                .getMentionedChannels()
                .contains(Corby.getShards().getTextChannelById(starboard.getChannel()))) {
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
                  e.getGuild().getId(),
                  channel.getId(),
                  Corby.getConfig().getDefaultStarboardStars(),
                  Corby.getConfig().isDefaultStarboardStatus()));
        } else {
          starboard.setChannel(channel.getId());
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
