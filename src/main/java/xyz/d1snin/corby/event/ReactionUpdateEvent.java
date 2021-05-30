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
