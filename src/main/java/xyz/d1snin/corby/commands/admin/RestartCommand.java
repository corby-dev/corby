package xyz.d1snin.corby.commands.admin;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;

public class RestartCommand extends Command {

  public RestartCommand() {
    this.alias = "reload";
    this.description = "Reboots the bot";
    this.category = Category.ADMIN;
    this.usages = new String[] {"%sreload", "%sreload server"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args)
      throws SQLException, LoginException, IOException, InterruptedException,
          ClassNotFoundException {
    if (args.length > 1) {
      if (args[1].equals("server")) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), "Restarting server..."))
            .complete();
        Runtime.getRuntime().exec("systemctl reboot");
      }
      return;
    }
    e.getTextChannel()
        .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), "Restarting..."))
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