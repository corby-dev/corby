package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.annotation.EventListener;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

@EventListener(event = MessageReceivedEvent.class)
public class MessageEvent extends Listener {
  @Override
  protected void perform(GenericEvent event) {

    MessageReceivedEvent thisEvent = ((MessageReceivedEvent) event);

    String arg = thisEvent.getMessage().getContentRaw().split("\\s+")[0];

    if (!arg.startsWith(GuildSettingsManager.getGuildPrefix(thisEvent.getGuild()))) {
      return;
    }

    final String cantFound = "Can't found this command: `%s`";

    if (!Command.getCommandUsages()
            .contains(
                arg.substring(
                    GuildSettingsManager.getGuildPrefix(thisEvent.getGuild()).length()))
        && arg.length() > 1) {
      thisEvent
          .getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.ERROR, thisEvent.getAuthor(), String.format(cantFound, arg)))
          .queue();
    }
  }
}
