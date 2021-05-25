/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import xyz.d1snin.corby.annotation.EventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventListener(event = {GuildMessageReactionAddEvent.class, GuildMessageReactionRemoveEvent.class})
public class ReactionUpdateEvent extends Listener {

  private static final Map<String, ConcurrentHashMap<String, Runnable>> reactionMap =
      new ConcurrentHashMap<>();

  public static void registerReaction(String messageId, String reactionId, Runnable execute) {
    ConcurrentHashMap<String, Runnable> concurrentHashMap = new ConcurrentHashMap<>();
    concurrentHashMap.put(reactionId, execute);
    reactionMap.put(messageId, concurrentHashMap);
  }

  @Override
  protected void perform(GenericEvent event) {
    GenericGuildMessageReactionEvent thisEvent = ((GenericGuildMessageReactionEvent) event);

    Message msg = thisEvent.retrieveMessage().complete();

    String emoteId = thisEvent.getReaction().getReactionEmote().getId();
    if (reactionMap.containsKey(msg.getId())
        && reactionMap.get(msg.getId()).containsKey(emoteId)
        && !thisEvent.getReaction().isSelf()) {
      reactionMap.get(msg.getId()).get(emoteId).run();
    }
  }
}
