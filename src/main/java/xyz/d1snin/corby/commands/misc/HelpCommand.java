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

package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.event.ReactionUpdateEvent;
import xyz.d1snin.corby.model.Argument;
import xyz.d1snin.corby.model.Category;
import xyz.d1snin.corby.model.EmbedType;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HelpCommand extends Command {

  // CLEAN ME: 05.06.2021

  private Emote next;
  private Emote back;
  private boolean extEmojisAllowed;

  public HelpCommand() {
    this.usage = "help";
    this.description = "Gives you information about commands.";
    this.category = Category.MISC;

    this.botPerms =
        new Permission[] {Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI};

    execute(
        u -> {
          next = Corby.getShards().getEmoteById(Corby.getConfig().getEmoteNext());
          back = Corby.getShards().getEmoteById(Corby.getConfig().getEmoteBack());

          extEmojisAllowed =
              Objects.requireNonNull(u.getGuild().getBotRole())
                  .getPermissions()
                  .contains(Permission.MESSAGE_EXT_EMOJI);

          AtomicInteger page = new AtomicInteger(1);

          u.getChannel()
              .sendMessage(Objects.requireNonNull(getEmbedByPage(page.get(), u.getEvent())))
              .queue(
                  message -> {
                    addReactionsSafe(u.getEvent(), message, back, next);

                    ReactionUpdateEvent.registerReaction(
                        message.getId(),
                        extEmojisAllowed ? back.getId() : Corby.getConfig().getEmoteDefaultBack(),
                        getRun(page, message, back, u.getEvent()));

                    ReactionUpdateEvent.registerReaction(
                        message.getId(),
                        extEmojisAllowed ? next.getId() : Corby.getConfig().getEmoteDefaultNext(),
                        getRun(page, message, next, u.getEvent()));
                  });
        });

    arg(
        u -> {
          Command command = u.getCommandByUsage(u.getArgumentValue(0));

          if (command == null) {
            u.sendEmbed(
                EmbedType.ERROR,
                String.format("Could not find this command: `%s`", u.getArgumentValue(0)));
            return;
          }

          String msg =
              "**Category:**"
                  + "\n"
                  + command.getCategory().getName()
                  + "\n\n"
                  + "**Description**"
                  + "\n"
                  + command.getDescription()
                  + (command.getLongDescription() == null
                      ? ""
                      : "\n\n" + command.getLongDescription())
                  + "\n\n"
                  + "**Usage:**"
                  + "\n"
                  + u.getUsagesAsString();

          u.sendEmbed(EmbedType.DEFAULT, msg);
        },
        new Argument(null, "<Command Usage>", false, false));
  }

  private MessageEmbed getEmbedByPage(int page, GuildMessageReceivedEvent e) {

    long categories =
        OtherUtils.isOwner(e.getAuthor())
            ? Category.values().length
            : Arrays.stream(Category.values()).filter(it -> !it.isAdminCategory()).count();

    if (page > categories || page < 1) {
      return null;
    }

    Category category = null;

    for (Category c : Category.values()) {
      if ((c.ordinal() + 1) == page) {
        category = c;
      }
    }

    StringBuilder sb = new StringBuilder();

    String prefix = MongoPrefixManager.getPrefix(e.getGuild()).getPrefix();

    for (Command c : getUtil(e).getCommandsByCategory(category)) {
      sb.append("`")
          .append(prefix)
          .append(c.getUsage())
          .append("`")
          .append(" - *")
          .append(c.getDescription())
          .append("*\n");
    }

    assert category != null;
    return Embeds.create(
        EmbedType.DEFAULT,
        "**" + category.getName() + " Commands. Page " + page + "/" + categories + ".**\n\n" + sb,
        getUtil(e));
  }

  private Runnable getRun(
      AtomicInteger page, Message message, Emote emote, GuildMessageReceivedEvent event) {

    return () -> {
      if (getEmbedByPage(emote == next ? page.get() + 1 : page.get() - 1, event) == null) {

        removeReaction(message, emote, event);

        return;
      }

      message
          .editMessage(
              Objects.requireNonNull(getEmbedByPage(page.addAndGet(emote == next ? 1 : -1), event)))
          .queue();

      removeReaction(message, emote, event);
    };
  }

  private void addReactionsSafe(GuildMessageReceivedEvent event, Message message, Emote... emotes) {

    if (Objects.requireNonNull(event.getGuild().getBotRole())
        .getPermissions()
        .contains(Permission.MESSAGE_EXT_EMOJI)) {

      for (Emote emote : emotes) {
        message.addReaction(emote).queue();
      }

    } else {

      for (Emote emote : emotes) {
        message
            .addReaction(
                emote == next
                    ? Corby.getConfig().getEmoteDefaultNext()
                    : Corby.getConfig().getEmoteDefaultBack())
            .queue();
      }
    }
  }

  private String getDefaultReaction(Emote emote) {
    return emote == next
        ? Corby.getConfig().getEmoteDefaultNext()
        : Corby.getConfig().getEmoteDefaultBack();
  }

  private void removeReaction(Message message, Emote emote, GuildMessageReceivedEvent event) {
    if (extEmojisAllowed) {

      message.removeReaction(emote, event.getAuthor()).queue();

    } else {

      message.removeReaction(getDefaultReaction(emote), event.getAuthor()).queue();
    }
  }
}
