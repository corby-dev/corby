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
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.annotation.EventListener;
import xyz.d1snin.corby.database.managers.StarboardManager;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@EventListener(event = {GuildMessageReactionAddEvent.class, GuildMessageReactionRemoveEvent.class})
public class ReactionUpdateEvent extends Listener {

  private static final Set<MessageReaction> executed = new CopyOnWriteArraySet<>();

  @Override
  protected void perform(GenericEvent event) throws SQLException {
    GenericGuildMessageReactionEvent thisEvent = ((GenericGuildMessageReactionEvent) event);

    if (thisEvent.getReaction().getReactionEmote().getName().equals(Corby.config.emoteStar)) {
      if (StarboardManager.getStarboardChannel(thisEvent.getGuild()) == null) {
        return;
      }
      if (!StarboardManager.getStarboardIsEnabled(thisEvent.getGuild())) {
        return;
      }

      Message msg = thisEvent.retrieveMessage().complete();

      if (msg.getAuthor().getId().equals(Corby.config.id)) {
        return;
      }
      if (msg.getReactions().isEmpty()) {
        return;
      }

      MessageReaction reaction = msg.getReactions().get(0);

      for (MessageReaction r : msg.getReactions()) {
        if (r.getReactionEmote().getName().equals(Corby.config.emoteStar)) {
          reaction = r;
        }
      }

      if (reaction.getCount() == StarboardManager.getStarboardStars(thisEvent.getGuild())
          && !executed.contains(reaction)) {
        Objects.requireNonNull(StarboardManager.getStarboardChannel(thisEvent.getGuild()))
            .sendMessage(
                new EmbedBuilder()
                    .setAuthor(
                        msg.getAuthor().getAsTag(),
                        msg.getJumpUrl(),
                        msg.getAuthor().getAvatarUrl())
                    .setDescription(
                        "[[context]]("
                            + msg.getJumpUrl()
                            + ")"
                            + "\n\n"
                            + msg.getContentRaw()
                            + "\n"
                            + (msg.getAttachments().isEmpty()
                                ? ""
                                : msg.getAttachments().get(0).getUrl()))
                    .setTimestamp(Instant.now())
                    .setColor(Corby.config.starboardColor)
                    .setFooter(Corby.config.botName, Corby.config.botPfpUrl)
                    .build())
            .queue();
        executed.add(reaction);
      }
    }
  }
}
