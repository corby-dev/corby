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
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.model.database.Prefix;
import xyz.d1snin.corby.utils.Embeds;

public class PrefixCommand extends Command {

  public PrefixCommand() {
    this.alias = "prefix";
    this.description = "Changes the bot prefix on the server.";
    this.category = Category.SETTINGS;
    this.usages = new String[] {"alias", "<New Prefix>"};

    this.permissions = new Permission[] {Permission.ADMINISTRATOR};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {

    final String currPrefix = "Current prefix is `%s`.";
    final String prefixAlready = "Bot prefix is already `%s`.";
    final String cannotBeMoreThen = "The prefix cannot be more than 5 characters.";
    final String successChanged = "The prefix was successfully changed to `%s`.";

    Prefix prefix = MongoPrefixManager.getPrefix(e.getGuild());
    String currentPrefix = prefix.getPrefix();

    if (args.length < 2) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.DEFAULT,
                  e.getAuthor(),
                  String.format(currPrefix, currentPrefix),
                  e.getGuild(),
                  null,
                  null))
          .queue();
      return;
    }

    String newPrefix = args[1];

    if (currentPrefix.equals(newPrefix)) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.ERROR,
                  e.getAuthor(),
                  String.format(prefixAlready, newPrefix),
                  e.getGuild()))
          .queue();
      return;
    }

    if (newPrefix.length() > 5) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), cannotBeMoreThen, e.getGuild()))
          .queue();
      return;
    }

    prefix.setPrefix(newPrefix);
    MongoPrefixManager.writePrefix(prefix);

    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.SUCCESS,
                e.getAuthor(),
                String.format(successChanged, newPrefix),
                e.getGuild()))
        .queue();
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length <= 2;
  }
}
