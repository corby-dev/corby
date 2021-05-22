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
    this.usages = new String[] {"server"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws IOException {
    if (args.length > 1) {
      if (args[1].equals("server")) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.SUCCESS,
                    e.getAuthor(),
                    "Restarting server...",
                    e.getGuild(),
                    null))
            .complete();
        Runtime.getRuntime().exec("systemctl reboot");
      }
      return;
    }
    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.SUCCESS, e.getAuthor(), "Restarting...", e.getGuild(), null))
        .complete();
    Corby.restart();
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    if (!(args.length <= 2)) {
      return false;
    }
    return args.length <= 1 || args[1].equals("server");
  }
}
