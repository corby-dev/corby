/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.annotation.EventListener;
import xyz.d1snin.corby.database.managers.MongoStarboardManager;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.model.Starboard;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@EventListener(event = {GuildMessageReactionAddEvent.class, GuildMessageReactionRemoveEvent.class})
public class ReactionUpdateEvent extends Listener {

  private static final Set<MessageReaction> executed = new CopyOnWriteArraySet<>();

  @Override
  protected void perform(GenericEvent event) {
    GenericGuildMessageReactionEvent thisEvent = ((GenericGuildMessageReactionEvent) event);

    if (thisEvent.getReaction().getReactionEmote().getName().equals(Corby.config.getEmoteStar())) {
      Starboard starboard = MongoStarboardManager.getStarboard(thisEvent.getGuild());

      if (starboard == null) {
        return;
      }
      if (!starboard.isStatus()) {
        return;
      }

      Message msg = thisEvent.retrieveMessage().complete();

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

          EmbedBuilder builder =
              new EmbedBuilder()
                  .setAuthor(
                      msg.getAuthor().getAsTag(),
                      msg.getJumpUrl(),
                      msg.getAuthor().getEffectiveAvatarUrl())
                  .setDescription(
                      String.format("[[context]](%s)\n\n%s", msg.getJumpUrl(), msg.getContentRaw())
                          + (attachment == null
                              ? ""
                              : attachment.isImage() ? "" : attachment.getProxyUrl()))
                  .setTimestamp(Instant.now())
                  .setColor(Corby.config.getStarboardColor())
                  .setFooter(Corby.config.getBotName(), Corby.config.getBotPfpUrl());

          if (attachment != null && attachment.isImage()) {
            builder.setImage(attachment.getProxyUrl());
          }
          starboard.getChannel().sendMessage(builder.build()).queue();
        } catch (Exception e) {
          User owner =
              Objects.requireNonNull(
                  Objects.requireNonNull(thisEvent.getGuild().getOwner()).getUser());
          OtherUtils.sendPrivateMessageSafe(
              owner,
              Embeds.create(
                  EmbedTemplate.DEFAULT,
                  owner,
                  "Hey! You received this message because I cannot send messages to the starboard. Please make sure I have permission.",
                  null,
                  null,
                  null),
              () -> {
                /* ignore */
              });
          return;
        }
        executed.add(reaction);
      }
    }
  }
}
