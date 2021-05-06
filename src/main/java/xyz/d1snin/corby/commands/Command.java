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
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.ExceptionUtils;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public abstract class Command extends ListenerAdapter {

  protected abstract void execute(MessageReceivedEvent e, String[] args)
      throws SQLException, LoginException, IOException, InterruptedException,
          ClassNotFoundException;

  private static final List<Command> commands = new ArrayList<>();
  private static final Set<String> commandUsages = new HashSet<>();
  private static final List<User> cooldowns = new CopyOnWriteArrayList<>();

  protected String use = "null";
  protected Permission[] permissions = new Permission[0];
  protected Permission[] botPermissions = new Permission[0];
  protected boolean admincommand = false;

  public void onMessageReceived(MessageReceivedEvent e) {
    try {

      final String invalidPermission = "You must have permissions %s to use this command.";
      final String userOnCooldown =
          "You are now on cooldown, please wait a moment before using the command again.";

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

        if (admincommand && !e.getAuthor().getId().equals(Corby.config.ownerId)) {
          return;
        }

        if (!Objects.requireNonNull(e.getGuild().getBotRole()).hasPermission(Corby.permissions)) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              String.format(
                  "It looks like I do not have or I do not have enough permissions on this server, please invite me using [this](%s) link, I am leaving right now.",
                  Corby.config.inviteUrl));
          e.getGuild().leave().queue();
          return;
        }

        if (cooldowns.contains(e.getAuthor())) {
          Embeds.createAndSendWithReaction(
              EmbedTemplate.ERROR,
              e.getAuthor(),
              e.getTextChannel(),
              Corby.config.emoteTrash,
              userOnCooldown);
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

        cooldowns.add(e.getAuthor());
      }

    } catch (SQLException exception) {
      ExceptionUtils.processException(exception);
    }
  }

  public void onLoad() {
    Corby.permissions.addAll(Arrays.asList(botPermissions));
    commandUsages.add(use);
  }

  private boolean hasPermission(MessageReceivedEvent event) {

    if (permissions.length == 0) {
      return true;
    }

    EnumSet<Permission> userPermissions =
        Objects.requireNonNull(event.getMember()).getPermissions();

    return userPermissions.containsAll(Arrays.asList(this.permissions));
  }

  private String getPermissionString() {

    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < permissions.length; i++) {
      sb.append(permissions[i].getName()).append((i == permissions.length - 1) ? "" : ", ");
    }

    return sb.toString();
  }

  private boolean isCommand(Message message, MessageReceivedEvent event) throws SQLException {
    return Arrays.asList(getCommandArgs(message))
            .get(0)
            .toLowerCase()
            .equals(GuildSettingsManager.getGuildPrefix(event.getGuild()) + use)
        && getCommandArgs(message)[0].startsWith(
            GuildSettingsManager.getGuildPrefix(event.getGuild()));
  }

  protected String[] getCommandArgs(Message message) {
    return message.getContentRaw().split("\\s+");
  }

  public static Command add(Command command) {
    commands.add(command);
    command.onLoad();
    Corby.permissions.addAll(Corby.defaultPermissions);
    return command;
  }

  public static void startCooldownUpdater() {
    ScheduledExecutorService scheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor();
    scheduledExecutorService.scheduleWithFixedDelay(
        cooldowns::clear, 0, Corby.config.defaultCooldownSeconds, TimeUnit.SECONDS);
  }

  // --Commented out by Inspection START (03.05.2021, 22:49):
  //  public static List<Command> getCommands() {
  //    return commands;
  //  }
  // --Commented out by Inspection STOP (03.05.2021, 22:49)

  public static Set<String> getCommandUsages() {
    return commandUsages;
  }
}
