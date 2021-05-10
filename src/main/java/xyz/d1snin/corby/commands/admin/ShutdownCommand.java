package xyz.d1snin.corby.commands.admin;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.manager.config.Config;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.sql.SQLException;

public class ShutdownCommand extends Command {

  public ShutdownCommand() {
    this.alias = "terminate";
    this.description = "Turns off the bot";
    this.category = Category.ADMIN;
    this.usages = new String[] {"%sterminate"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws SQLException {
    e.getTextChannel()
        .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), "Terminating... Bye!"))
        .complete();
    Corby.shutdown(Config.ExitCodes.NORMAL_SHUTDOWN_EXIT_CODE);
  }

  @Override
  protected boolean isValidSyntax(String[] args) {
    return args.length <= 1;
  }
}
