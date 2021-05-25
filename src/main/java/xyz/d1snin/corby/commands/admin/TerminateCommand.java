/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.admin;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.model.Config;
import xyz.d1snin.corby.utils.Embeds;

public class TerminateCommand extends Command {

  public TerminateCommand() {
    this.alias = "terminate";
    this.description = "Turns off the bot";
    this.category = Category.ADMIN;
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {
    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.SUCCESS, e.getAuthor(), "Terminating... Bye!", e.getGuild()))
        .complete();
    Corby.shutdown(Config.NORMAL_SHUTDOWN_EXIT_CODE);
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length <= 1;
  }
}
