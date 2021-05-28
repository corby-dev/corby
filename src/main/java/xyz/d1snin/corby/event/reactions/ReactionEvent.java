/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event.reactions;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.d1snin.corby.Corby;

public abstract class ReactionEvent extends ListenerAdapter {

  protected String emoji;

  protected abstract void execute(GuildMessageReactionAddEvent event, Message msg);

  public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
    if (emoji.equals(event.getReaction().getReactionEmote().getName())) {
      Corby.getService().execute(() -> execute(event, event.retrieveMessage().complete()));
    }
  }
}
