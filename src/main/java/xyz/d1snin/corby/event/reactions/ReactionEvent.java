/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event.reactions;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.event.Listener;

public abstract class ReactionEvent extends Listener {

  public ReactionEvent() {
    this.event = MessageReactionAddEvent.class;
  }

  protected String emoji;

  protected abstract void performReaction(MessageReactionAddEvent event, Message msg);

  @Override
  public void perform(GenericEvent event) {
    MessageReactionAddEvent thisEvent = (MessageReactionAddEvent) event;

    if (emoji.equals(thisEvent.getReaction().getReactionEmote().getName())) {
      Corby.getService()
          .execute(() -> performReaction(thisEvent, thisEvent.retrieveMessage().complete()));
    }
  }
}
