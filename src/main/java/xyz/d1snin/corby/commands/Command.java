/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.ExceptionUtils;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class Command extends ListenerAdapter {

  protected abstract void execute(MessageReceivedEvent e, String[] args)
      throws SQLException, LoginException, IOException, InterruptedException,
          ClassNotFoundException;

  protected abstract boolean isValidSyntax(MessageReceivedEvent e, String[] args);

  private static final List<Command> commands = new ArrayList<>();

  protected String alias = null;
  protected String description = null;
  protected Category category = null;
  protected String[] usages = null;

  protected String longDescription = null;
  protected Permission[] permissions = new Permission[0];
  protected Permission[] botPermissions = new Permission[0];

  public String getAlias() {
    return alias;
  }

  public String getDescription() {
    return description;
  }

  public Category getCategory() {
    return category;
  }

  public String[] getUsages() {
    return usages;
  }

  public String getUsagesString() {
    StringBuilder sb = new StringBuilder();
    for (String s : getUsages()) {
      sb.append(String.format(s, PrefixManager.getPrefix(event.getGuild()))).append("\n");
    }
    return sb.toString();
  }

  public String getLongDescription() {
    return longDescription;
  }

  public Permission[] getPermissions() {
    return permissions;
  }

  public Permission[] getBotPermissions() {
    return botPermissions;
  }

  private MessageReceivedEvent event = null;

  public void onMessageReceived(@NotNull MessageReceivedEvent e) {

    Corby.getService()
        .execute(
            () -> {
              try {

                event = e;

                final String invalidPermission =
                    "You must have permissions %s to use this command.";
                final String invalidBotPermission =
                    "It looks like I do not have or I do not have enough permissions on this server, please invite me using [this](%s) link, I am leaving right now.";
                final String invalidSyntax = "**Incorrect Syntax:** `%s`\n\n**Usage:**\n%s";

                if (!e.getChannelType().isGuild()) {
                  return;
                }

                Message msg = e.getMessage();

                if (e.getAuthor().isBot()) {
                  return;
                }

                if (isCommand(msg, e)) {
                  if (!hasPermission(e)) {
                    e.getTextChannel()
                        .sendMessage(
                            Embeds.create(
                                EmbedTemplate.ERROR,
                                e.getAuthor(),
                                String.format(invalidPermission, getPermissionString())))
                        .queue();
                    return;
                  }

                  if ((getCategory() == Category.ADMIN)
                      && !e.getAuthor().getId().equals(Corby.config.ownerId)) {
                    return;
                  }

                  if (!Objects.requireNonNull(e.getGuild().getBotRole())
                      .hasPermission(Corby.permissions)) {
                    e.getTextChannel()
                        .sendMessage(
                            Embeds.create(
                                EmbedTemplate.ERROR,
                                e.getAuthor(),
                                String.format(invalidBotPermission, Corby.config.inviteUrl)))
                        .queue();
                    e.getGuild().leave().queue();
                    return;
                  }

                  if (!isValidSyntax(e, getCommandArgs(e.getMessage()))) {
                    StringBuilder sb = new StringBuilder();
                    for (String s : getUsages()) {
                      sb.append("`")
                          .append(String.format(s, PrefixManager.getPrefix(e.getGuild())))
                          .append("`")
                          .append("\n");
                    }

                    e.getTextChannel()
                        .sendMessage(
                            Embeds.create(
                                EmbedTemplate.ERROR,
                                e.getAuthor(),
                                String.format(invalidSyntax, e.getMessage().getContentRaw(), sb)))
                        .queue();
                    return;
                  }

                  try {
                    execute(e, getCommandArgs(msg));
                  } catch (Exception exception) {
                    ExceptionUtils.processException(exception);
                  }
                }

              } catch (SQLException exception) {
                ExceptionUtils.processException(exception);
              }
            });
  }

  protected String getMessageContent() {
    return event.getMessage().getContentRaw();
  }

  private void onLoad() {
    Corby.permissions.addAll(Arrays.asList(getBotPermissions()));
  }

  private boolean hasPermission(MessageReceivedEvent event) {
    if (getPermissions().length == 0) {
      return true;
    }

    return Objects.requireNonNull(event.getMember())
            .getPermissions()
            .containsAll(Arrays.asList(getPermissions()))
        || event
            .getAuthor()
            .getId()
            .equals(Corby.config.ownerId); // <- Don't worry, this is only needed to test the bot.
  }

  private String getPermissionString() {

    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < getPermissions().length; i++) {
      sb.append(getPermissions()[i].getName())
          .append((i == getPermissions().length - 1) ? "" : ", ");
    }

    return sb.toString();
  }

  private String[] getCommandArgs(Message msg) {
    return msg.getContentRaw().split("\\s+");
  }

  private boolean isCommand(Message message, MessageReceivedEvent event) throws SQLException {

    if (!getCommands().contains(this)) {
      return false;
    }

    return Arrays.asList(getCommandArgs(message))
            .get(0)
            .toLowerCase()
            .equals(PrefixManager.getPrefix(event.getGuild()) + getAlias())
        && getCommandArgs(message)[0].startsWith(PrefixManager.getPrefix(event.getGuild()));
  }

  public static List<Command> getCommandsByCategory(Category category) {
    List<Command> result = new ArrayList<>();
    for (Command c : getCommands()) {
      if (c.getCategory() == category) {
        result.add(c);
      }
    }
    return result;
  }

  public static Command getCommandByAlias(String alias) {
    for (Command c : getCommands()) {
      if (c.getAlias().equals(alias)) {
        return c;
      }
    }
    return null;
  }

  public static Command add(Command command) {

    final String invalidCommandConfig =
        "It looks like one of the fields is not initialized in the command (%s), fields alias, description, category and usages should be initialized. This command are ignored.";

    if (command.getAlias() == null
        || command.getDescription() == null
        || command.getCategory() == null
        || command.getUsages() == null) {
      Corby.logger.warn(String.format(invalidCommandConfig, command.getClass().getName()));
    } else {
      commands.add(command);
    }
    command.onLoad();
    return command;
  }

  public static List<Command> getCommands() {
    return commands;
  }
}
