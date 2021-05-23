package xyz.d1snin.corby.commands.admin;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.io.IOException;

public class ReloadCommand extends Command {

  public ReloadCommand() {
    this.alias = "reload";
    this.description = "Reboots the bot";
    this.category = Category.ADMIN;
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws IOException {
    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.SUCCESS, e.getAuthor(), "Restarting...", e.getGuild(), null))
        .complete();
    Corby.restart();
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length < 2;
  }
}