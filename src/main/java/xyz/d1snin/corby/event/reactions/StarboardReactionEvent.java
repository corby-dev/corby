/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event.reactions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.MongoStarboardManager;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.model.database.Starboard;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class StarboardReactionEvent extends ReactionEvent {

  private static final Set<MessageReaction> executed = new CopyOnWriteArraySet<>();

  public StarboardReactionEvent() {
    this.emoji = Corby.config.getEmoteStar();
  }

  @Override
  protected void execute(GuildMessageReactionAddEvent event, Message msg) {
    Starboard starboard = MongoStarboardManager.getStarboard(event.getGuild());

    if (starboard == null) {
      return;
    }

    if (!starboard.isStatus()) {
      return;
    }

    if (msg.getAuthor().getId().equals(Corby.config.getId())) {
      return;
    }

    if (msg.getReactions().isEmpty()) {
      return;
    }

    MessageReaction reaction = msg.getReactions().get(0);

    for (MessageReaction r : msg.getReactions()) {
      if (r.getReactionEmote().getName().equals(Corby.config.getEmoteStar())) {
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
                .setColor(Corby.config.getStarboardColor())
                .setFooter(Corby.config.getBotName(), Corby.config.getBotPfpUrl());

        if (attachment != null && attachment.isImage()) {
          builder.setImage(attachment.getProxyUrl());
        }
        Objects.requireNonNull(Corby.getApi().getTextChannelById(starboard.getChannel()))
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
