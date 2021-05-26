/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event.reactions;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.d1snin.corby.Corby;

public abstract class ReactionEvent extends ListenerAdapter {
  @Getter protected String emote;

  protected abstract void execute(GenericGuildMessageReactionEvent event, Message msg);

  @Override
  public void onGenericGuildMessageReaction(@NotNull GenericGuildMessageReactionEvent event) {
    if (event.getReaction().getReactionEmote().getName().equals(getEmote())) {
      Corby.getService().execute(() -> execute(event, event.retrieveMessage().complete()));
    }
  }
}
