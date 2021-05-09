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
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.manager.config.Config;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.ExceptionUtils;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public abstract class Command extends ListenerAdapter {

  protected abstract void execute(MessageReceivedEvent e, String[] args)
      throws SQLException, LoginException, IOException, InterruptedException,
          ClassNotFoundException;

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

  public String getLongDescription() {
    return longDescription;
  }

  public Permission[] getPermissions() {
    return permissions;
  }

  public Permission[] getBotPermissions() {
    return botPermissions;
  }

  public void onMessageReceived(MessageReceivedEvent e) {
    try {

      final String invalidPermission = "You must have permissions %s to use this command.";
      final String invalidBotPermission =
          "It looks like I do not have or I do not have enough permissions on this server, please invite me using [this](%s) link, I am leaving right now.";

      if (!e.getChannelType().isGuild()) {
        return;
      }

      Message msg = e.getMessage();

      if (e.getAuthor().isBot()) {
        return;
      }

      if (isCommand(msg, e)) {
        if (!hasPermission(e)) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(invalidPermission, getPermissionString()));
          return;
        }

        if ((getCategory() == Category.ADMIN) && !e.getAuthor().getId().equals(Corby.config.ownerId)) {
          return;
        }

        if (!Objects.requireNonNull(e.getGuild().getBotRole()).hasPermission(Corby.permissions)) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(invalidBotPermission, Corby.config.inviteUrl));
          e.getGuild().leave().queue();
          return;
        }

        Corby.getService()
            .execute(
                () -> {
                  try {
                    execute(e, getCommandArgs(msg));
                  } catch (Exception exception) {
                    ExceptionUtils.processException(exception);
                  }
                });
      }

    } catch (SQLException exception) {
      ExceptionUtils.processException(exception);
    }
  }

  private void onLoad() throws SQLException {
    if (getAlias() == null || getDescription() == null || getCategory() == null || getUsages() == null) {
      Corby.logger.error("One of the command fields is not initialized.");
      Corby.shutdown(Config.ExitCodes.BAD_COMMAND_CONFIG);
    }
    Corby.permissions.addAll(Arrays.asList(getBotPermissions()));
  }

  private boolean hasPermission(MessageReceivedEvent event) {
    if (getPermissions().length == 0) {
      return true;
    }

    return Objects.requireNonNull(event.getMember())
        .getPermissions()
        .containsAll(Arrays.asList(getPermissions()));
  }

  private String getPermissionString() {

    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < getPermissions().length; i++) {
      sb.append(getPermissions()[i].getName()).append((i == getPermissions().length - 1) ? "" : ", ");
    }

    return sb.toString();
  }

  private String[] getCommandArgs(Message msg) {
    return msg.getContentRaw().split("\\s+");
  }

  private boolean isCommand(Message message, MessageReceivedEvent event) throws SQLException {
    return Arrays.asList(getCommandArgs(message))
            .get(0)
            .toLowerCase()
            .equals(PrefixManager.getPrefix(event.getGuild()) + getAlias())
        && getCommandArgs(message)[0].startsWith(PrefixManager.getPrefix(event.getGuild()));
  }

  public static Command add(Command command) throws SQLException {
    commands.add(command);
    command.onLoad();
    return command;
  }

  public static List<Command> getCommands() {
    return commands;
  }
}
