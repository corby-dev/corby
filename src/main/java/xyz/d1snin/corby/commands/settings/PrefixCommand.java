/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.sql.SQLException;

public class PrefixCommand extends Command {

  public PrefixCommand() {
    this.alias = "prefix";
    this.description = "Changes the bot prefix on the server.";
    this.category = Category.SETTINGS;
    this.usages = new String[] {"%sprefix", "%sprefix <New Prefix>"};

    this.permissions = new Permission[] {Permission.ADMINISTRATOR};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws SQLException {

    final String currPrefix = "Current prefix is `%s`.";
    final String prefixAlready = "Bot prefix is already `%s`.";
    final String cannotBeMoreThen = "The prefix cannot be more than 5 characters.";
    final String successChanged = "The prefix was successfully changed to `%s`.";

    String currentPrefix = PrefixManager.getPrefix(e.getGuild());

    if (args.length < 2) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.DEFAULT, e.getAuthor(), String.format(currPrefix, currentPrefix)))
          .queue();
      return;
    }

    String newPrefix = args[1];

    if (currentPrefix.equals(newPrefix)) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.ERROR, e.getAuthor(), String.format(prefixAlready, newPrefix)))
          .queue();
      return;
    }

    if (newPrefix.length() > 5) {
      e.getTextChannel()
          .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), cannotBeMoreThen))
          .queue();
      return;
    }

    PrefixManager.setPrefix(e.getGuild(), newPrefix);

    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT, e.getAuthor(), String.format(successChanged, newPrefix)))
        .queue();
  }

  @Override
  protected boolean isValidSyntax(String[] args) {
    return args.length <= 2;
  }
}
