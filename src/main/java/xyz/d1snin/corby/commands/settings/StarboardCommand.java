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
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.database.managers.MongoStarboardManager;
import xyz.d1snin.corby.model.Argument;
import xyz.d1snin.corby.model.Category;
import xyz.d1snin.corby.model.EmbedType;
import xyz.d1snin.corby.model.Starboard;
import xyz.d1snin.corby.utils.CommandUtil;
import xyz.d1snin.corby.utils.FormatUtils;
import xyz.d1snin.corby.utils.OtherUtils;

import java.util.Objects;

public class StarboardCommand extends Command {

  public StarboardCommand() {
    this.usage = "starboard";
    this.description = "Starboard settings";
    this.category = Category.SETTINGS;

    this.longDescription =
        "Starboard is a channel where messages will be sent on which the number of star reactions will be equal to the specified one, so users will be able to mark messages they like.";
    this.userPerms = new Permission[] {Permission.ADMINISTRATOR};

    final String sbNotEnabled = "Starboard is not enabled on your server.";

    execute(
        u -> {
          Starboard sb = MongoStarboardManager.getStarboard(u.getGuild());

          if (sb == null) {
            onNotConfigured(u);
            return;
          }
          if (!sb.isStatus()) {
            u.sendEmbed(EmbedType.ERROR, sbNotEnabled);
            return;
          }

          u.sendEmbed(
              EmbedType.DEFAULT,
              String.format(
                  "Starboard is enabled on your server!\n\n%s\n%s",
                  FormatUtils.formatMessageKeyText(
                      "Required number of stars", String.valueOf(sb.getStars())),
                  FormatUtils.formatMessageKeyText(
                      "Channel for starboard",
                      Objects.requireNonNull(Corby.getShards().getTextChannelById(sb.getChannel()))
                          .getAsMention())));
        });

    arg(
        u -> {
          Starboard sb = MongoStarboardManager.getStarboard(u.getGuild());

          if (sb == null) {
            onNotConfigured(u);
            return;
          }

          if (sb.isStatus()) {
            u.sendEmbed(EmbedType.ERROR, "Starboard is already enabled on your server.");
            return;
          }

          sb.setStatus(true);

          MongoStarboardManager.writeStarboard(sb);

          u.sendEmbed(
              EmbedType.SUCCESS, "Starboard has been successfully enabled on your server!");
        },
        new Argument("enable", "enable", false, false));

    arg(
        u -> {
          Starboard sb = MongoStarboardManager.getStarboard(u.getGuild());

          if (sb == null) {
            onNotConfigured(u);
            return;
          }

          if (!sb.isStatus()) {
            u.sendEmbed(EmbedType.ERROR, "Starboard is already disabled on your server.");
            return;
          }

          sb.setStatus(false);

          MongoStarboardManager.writeStarboard(sb);

          u.sendEmbed(
              EmbedType.SUCCESS, "Starboard has been successfully disabled on your server!");
        },
        new Argument("disable", "disable", false, false));

    arg(
        u -> {
          if (u.getMessage().getMentionedChannels().isEmpty()) {
            u.trigger();
            return;
          }

          Starboard sb = MongoStarboardManager.getStarboard(u.getGuild());

          if (sb != null
              && u.getMessage()
                  .getMentionedChannels()
                  .contains(Corby.getShards().getTextChannelById(sb.getChannel()))) {
            u.sendEmbed(EmbedType.ERROR, "Starboard channel is already set to this channel.");
            return;
          }

          TextChannel channel = u.getMessage().getMentionedChannels().get(0);

          if (sb == null) {
            MongoStarboardManager.writeStarboard(
                new Starboard(
                    u.getGuild().getId(),
                    channel.getId(),
                    Corby.getConfig().getDefaultStarboardStars(),
                    Corby.getConfig().isDefaultStarboardStatus()));

          } else {

            sb.setChannel(channel.getId());

            MongoStarboardManager.writeStarboard(sb);
          }

          u.sendEmbed(
              EmbedType.SUCCESS,
              String.format(
                  "Starboard successfully installed on the channel %s", channel.getAsMention()));
        },
        new Argument("channel", "<#channel>", true, false));

    arg(
        u -> {
          if (!OtherUtils.isNumeric(u.getArgumentValue(0))) {
            u.trigger();
            return;
          }

          Starboard sb = MongoStarboardManager.getStarboard(u.getGuild());

          if (sb == null) {
            onNotConfigured(u);
            return;
          }

          int stars = Integer.parseInt(u.getArgumentValue(0));

          if (stars > 100 || stars < 1) {
            u.trigger();
            return;
          }

          if (!sb.isStatus()) {
            u.sendEmbed(EmbedType.ERROR, sbNotEnabled);
            return;
          }

          if (sb.getStars() == stars) {
            u.sendEmbed(
                EmbedType.ERROR,
                "The number of stars for the message is already set to this value.");
          }

          sb.setStars(stars);

          MongoStarboardManager.writeStarboard(sb);

          u.sendEmbed(
              EmbedType.SUCCESS,
              String.format(
                  "The number of stars for the message has been successfully updated to %d.",
                  stars));
        },
        new Argument("stars", "<Number of stars (1 - 100)>", true, false));
  }

  private static void onNotConfigured(CommandUtil u) {
    u.sendEmbed(
        EmbedType.ERROR,
        String.format(
            "Starboard is not configured on your server, use `%sstarboard channel <#channel>` to configure starboard.",
            MongoPrefixManager.getPrefix(u.getGuild())));
  }
}
