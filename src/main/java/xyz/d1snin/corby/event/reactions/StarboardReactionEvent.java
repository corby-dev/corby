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

package xyz.d1snin.corby.event.reactions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.MongoStarboardManager;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.model.Starboard;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class StarboardReactionEvent extends ReactionEvent {

  private static final Set<MessageReaction> executed = new CopyOnWriteArraySet<>();

  public StarboardReactionEvent() {
    this.emoji = Corby.getConfig().getEmoteStar();
  }

  @Override
  protected void performReaction(MessageReactionAddEvent event, Message msg) {
    Starboard starboard = MongoStarboardManager.getStarboard(event.getGuild());

    if (starboard == null) {
      return;
    }

    if (!starboard.isStatus()) {
      return;
    }

    if (msg.getReactions().isEmpty()) {
      return;
    }

    MessageReaction reaction = msg.getReactions().get(0);

    for (MessageReaction r : msg.getReactions()) {
      if (r.getReactionEmote().getName().equals(Corby.getConfig().getEmoteStar())) {
        reaction = r;
      }
    }

    if (reaction.getCount() == starboard.getStars() && !executed.contains(reaction)) {
      try {
        Message.Attachment attachment =
            msg.getAttachments().isEmpty() ? null : msg.getAttachments().get(0);

        MessageEmbed embed = msg.getEmbeds().isEmpty() ? null : msg.getEmbeds().get(0);

        EmbedBuilder builder =
            new EmbedBuilder()
                .setAuthor(
                    msg.getAuthor().getAsTag(),
                    msg.getJumpUrl(),
                    msg.getAuthor().getEffectiveAvatarUrl())
                .setDescription(
                    String.format(
                        "[[context]](%s)\n%s%s%s",
                        msg.getJumpUrl(),
                        msg.getContentRaw().length() > 0 ? "\n" + msg.getContentRaw() : "",
                        attachment == null
                            ? ""
                            : attachment.isImage() ? "" : "\n" + attachment.getProxyUrl(),
                        embed == null
                            ? ""
                            : String.format(
                                "\n%s\n%s",
                                embed.getTitle() == null ? "" : embed.getTitle(),
                                embed.getDescription() == null ? "" : embed.getDescription())))
                .setTimestamp(Instant.now())
                .setColor(Corby.getConfig().getStarboardColor())
                .setFooter(Corby.getConfig().getBotName(), Corby.getConfig().getBotPfpUrl());

        if (attachment != null && attachment.isImage()) {
          builder.setImage(attachment.getProxyUrl());
        }
        Objects.requireNonNull(Corby.getShards().getTextChannelById(starboard.getChannel()))
            .sendMessage(builder.build())
            .queue();
      } catch (Exception e) {
        User owner =
            Objects.requireNonNull(Objects.requireNonNull(event.getGuild().getOwner()).getUser());
        OtherUtils.sendPrivateMessageSafe(
            owner,
            Embeds.create(
                EmbedTemplate.DEFAULT,
                owner,
                "Hey! You received this message because I cannot send messages to the starboard. Please make sure I have permission."),
            () -> {
              /* ignore */
            });
        return;
      }
      executed.add(reaction);
    }
  }
}
