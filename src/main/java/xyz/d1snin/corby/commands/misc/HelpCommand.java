/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.util.Objects;

public class HelpCommand extends Command {

  private static final String incorrectPageMessage = "This page does not exist.";

  public HelpCommand() {
    this.alias = "help";
    this.description = "Gives you information about commands.";
    this.category = Category.MISC;
    this.usages = new String[] {"alias", "<Page Number>", "<Command Name>"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {

    final String couldNotFindMessage = "Could not find this command: `%s`";

    if (args.length < 2) {
      e.getTextChannel()
          .sendMessage(Objects.requireNonNull(getEmbedByPage(1, e.getGuild(), e.getAuthor(), e)))
          .queue();
      return;
    }

    if (OtherUtils.isNumeric(args[1])) {

      MessageEmbed embed =
          getEmbedByPage(Integer.parseInt(args[1]), e.getGuild(), e.getAuthor(), e);

      if (embed == null) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    e.getAuthor(),
                    incorrectPageMessage,
                    e.getGuild(),
                    null,
                    null))
            .queue();
        return;
      }

      e.getTextChannel().sendMessage(Objects.requireNonNull(embed)).queue();
      return;
    }

    Command command = getCommandByAlias(args[1]);

    if (command == null) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.ERROR,
                  e.getAuthor(),
                  String.format(couldNotFindMessage, args[1]),
                  e.getGuild(),
                  null,
                  null))
          .queue();
      return;
    }

    String msg =
        "**Category:**"
            + "\n"
            + command.getCategory().getName()
            + "\n\n"
            + "**Description**"
            + "\n"
            + command.getDescription()
            + (command.getLongDescription() == null ? "" : "\n\n" + command.getLongDescription())
            + "\n\n"
            + "**Usage:**"
            + "\n"
            + command.getUsagesString();
    e.getTextChannel()
        .sendMessage(
            Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), msg, e.getGuild(), null, null))
        .queue();
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length <= 2;
  }

  private MessageEmbed getEmbedByPage(int page, Guild guild, User user, MessageReceivedEvent e) {

    int categories = Category.values().length;

    if (page > categories || page < 1) {
      return null;
    }

    Category category = null;
    for (Category c : Category.values()) {
      if ((c.ordinal() + 1) == page) {
        category = c;
      }
    }

    if (category == Category.ADMIN && !user.getId().equals(Corby.config.getOwnerId())) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.ERROR,
                  e.getAuthor(),
                  incorrectPageMessage,
                  e.getGuild(),
                  null,
                  null))
          .queue();
      return null;
    }

    StringBuilder sb = new StringBuilder();

    String prefix = PrefixManager.getPrefix(guild);

    for (Command c : getCommandsByCategory(category)) {
      sb.append("`")
          .append(prefix)
          .append(c.getAlias())
          .append("`")
          .append(" - *")
          .append(c.getDescription())
          .append("*\n");
    }

    assert category != null;
    return Embeds.create(
        EmbedTemplate.DEFAULT,
        user,
        "**" + category.getName() + " Commands. Page " + page + "/" + categories + ".**\n\n" + sb,
        e.getGuild(),
        null,
        null);
  }
}
