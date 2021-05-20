package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.io.IOException;

public class UptimeCommand extends Command {

  public UptimeCommand() {
    this.alias = "uptime";
    this.description = "Get the bot's uptime";
    this.category = Category.MISC;
    this.usages = new String[] {"%suptime"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws IOException {
    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT,
                e.getAuthor(),
                String.format("Uptime: **[ %s ]**", Corby.getUptime()),
                e.getGuild(),
                null))
        .queue();
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length < 2;
  }
}
