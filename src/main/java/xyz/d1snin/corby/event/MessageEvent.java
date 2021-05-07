package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.annotation.EventListener;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.sql.SQLException;

@EventListener(event = MessageReceivedEvent.class)
public class MessageEvent extends Listener {
  @Override
  protected void perform(GenericEvent event) throws SQLException {

    MessageReceivedEvent thisEvent = ((MessageReceivedEvent) event);

    if (!thisEvent.getChannelType().isGuild()) {
      return;
    }

    String arg = thisEvent.getMessage().getContentRaw().split("\\s+")[0];

    if (!arg.startsWith(PrefixManager.getPrefix(thisEvent.getGuild()))) {
      return;
    }

    final String cantFound = "Could not find command: `%s`";

    if (!Command.getCommandUsages()
            .contains(
                arg.substring(PrefixManager.getPrefix(thisEvent.getGuild()).length()))
        && arg.length() > 1) {
      Embeds.createAndSendWithReaction(
          EmbedTemplate.ERROR,
          thisEvent.getAuthor(),
          thisEvent.getTextChannel(),
          Corby.config.emoteTrash,
          String.format(cantFound, arg));
    }
  }
}
