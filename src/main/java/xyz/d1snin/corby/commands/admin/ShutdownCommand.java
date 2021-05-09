package xyz.d1snin.corby.commands.admin;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.sql.SQLException;

public class ShutdownCommand extends Command {

  public ShutdownCommand() {
    this.admincommand = true;
    this.use = "terminate";
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws SQLException {
    e.getTextChannel()
        .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), "Terminating... Bye!"))
        .complete();
    Corby.shutdown();
  }
}
