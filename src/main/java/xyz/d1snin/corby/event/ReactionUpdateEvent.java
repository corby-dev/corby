/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReactionUpdateEvent extends Listener {

  private static final CopyOnWriteArrayList<Map<String, ConcurrentHashMap<String, Runnable>>>
      reactions = new CopyOnWriteArrayList<>();

  public ReactionUpdateEvent() {
    this.event = MessageReactionAddEvent.class;
  }

  public static void registerReaction(String messageId, String reactionId, Runnable execute) {
    ConcurrentHashMap<String, Runnable> concurrentHashMap = new ConcurrentHashMap<>();
    concurrentHashMap.put(reactionId, execute);

    ConcurrentHashMap<String, ConcurrentHashMap<String, Runnable>> eventExecution =
        new ConcurrentHashMap<>();
    eventExecution.put(messageId, concurrentHashMap);

    reactions.add(eventExecution);
  }

  @Override
  public void perform(GenericEvent event) {
    MessageReactionAddEvent thisEvent = (MessageReactionAddEvent) event;
    if (thisEvent.getReaction().isSelf()) {
      return;
    }
    Message msg = thisEvent.retrieveMessage().complete();

    MessageReaction.ReactionEmote thisReaction = thisEvent.getReaction().getReactionEmote();
    String emoteId =
        thisReaction.isEmoji() ? thisReaction.getName() : thisReaction.getEmote().getId();

    for (Map<String, ConcurrentHashMap<String, Runnable>> element : reactions) {
      if (element.containsKey(msg.getId())
          && element.get(msg.getId()).containsKey(emoteId)
          && !thisEvent.getReaction().isSelf()) {
        element.get(msg.getId()).get(emoteId).run();
      }
    }
  }
}
