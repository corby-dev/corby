package xyz.d1snin.corby.commands.administration;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;

public class RestartCommand extends Command {

  public RestartCommand() {
    this.admincommand = true;
    this.use = "reload";
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
}
