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
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.sql.SQLException;
import java.util.Objects;

public class HelpCommand extends Command {

  public HelpCommand() {
    this.alias = "help";
    this.description = "Gives you information about commands.";
    this.category = Category.MISC;
    this.usages = new String[] {"%shelp <Page Number>", "%shelp <Command Name>"};
  }

  private static final String incorrectPageMessage = "This page does not exist.";

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws SQLException {

    final String couldNotFindMessage = "Could not find this command: `%s`";
    final String incorrectSyntax =
        "Please use the following syntax: `%shelp <Page Number or Command Name>`";

    if (args.length < 2) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.ERROR,
                  e.getAuthor(),
                  String.format(incorrectSyntax, PrefixManager.getPrefix(e.getGuild()))))
          .queue();
      return;
    }

    if (OtherUtils.isNumeric(args[1])) {

      MessageEmbed embed =
          getEmbedByPage(Integer.parseInt(args[1]), e.getGuild(), e.getAuthor(), e);

      if (embed == null) {
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), incorrectPageMessage))
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
                  EmbedTemplate.ERROR, e.getAuthor(), String.format(couldNotFindMessage, args[1])))
          .queue();
      return;
    }

    String prefix = PrefixManager.getPrefix(e.getGuild());

    StringBuilder sb = new StringBuilder();

    sb.append("**Category:**")
        .append("\n")
        .append(command.getCategory().getName())
        .append("\n\n")
        .append("**Description**")
        .append("\n")
        .append(command.getDescription())
        .append(command.getLongDescription() == null ? "" : "\n\n" + command.getLongDescription())
        .append("\n\n")
        .append("**Usage:**")
        .append("\n");

    for (String s : command.getUsages()) {
      sb.append("`").append(String.format(s, prefix)).append("`").append("\n");
    }

    e.getTextChannel()
        .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), sb.toString()))
        .queue();
  }

  @Override
  protected boolean isValidSyntax(String[] args) {
    return args.length <= 2;
  }

  private static MessageEmbed getEmbedByPage(
      int page, Guild guild, User user, MessageReceivedEvent e) throws SQLException {

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

    if (category == Category.ADMIN && !user.getId().equals(Corby.config.ownerId)) {
      e.getTextChannel()
          .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), incorrectPageMessage))
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
          .append(" - ")
          .append(c.getDescription())
          .append("\n");
    }

    assert category != null;
    return Embeds.create(
        EmbedTemplate.DEFAULT,
        user,
        "**" + category.getName() + " Commands. Page " + page + "/" + categories + ".**\n\n" + sb);
  }
}
