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

import java.util.*;
import java.util.concurrent.*;

public abstract class Command extends ListenerAdapter {

  protected abstract void execute(MessageReceivedEvent e, String[] args);

  private static final List<Command> commands = new ArrayList<>();
  private static final List<User> cooldowns = new CopyOnWriteArrayList<>();

  protected String use = "null";
  protected Permission[] permissions = new Permission[0];
  protected Permission[] botPermissions = new Permission[0];
  protected boolean admincommand = false;

  public void onMessageReceived(MessageReceivedEvent e) {

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
            String.format(invalidPermission, getPermissionString()));
        return;
      }

      if (admincommand && !e.getAuthor().getId().equals(Corby.config.ownerId)) {
        return;
      }

      if (cooldowns.contains(e.getAuthor())) {
        Embeds.createAndSendWithReaction(
            EmbedTemplate.ERROR, e.getAuthor(), e.getTextChannel(), userOnCooldown);
        return;
      }

      try {

        Corby.getService().execute(() -> execute(e, getCommandArgs(msg)));

        cooldowns.add(e.getAuthor());
      } catch (Exception exception) {

        Embeds.createAndSendWithReaction(
            EmbedTemplate.ERROR,
            e.getAuthor(),
            e.getTextChannel(),
            "**An exception was caught while executing a command.**"
                + "\nAll necessary information has been sent to the owner!");

        Corby.getAPI()
            .openPrivateChannelById(Corby.config.ownerId)
            .complete()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.DEFAULT,
                    e.getAuthor(),
                    "**An exception was thrown while trying to execute a command.**"
                        + "\n\n**User message:**\n`"
                        + e.getMessage().getContentRaw()
                        + "`\n\n**Exception message:**\n`"
                        + exception.getClass().getName()
                        + ": "
                        + exception.getMessage()
                        + "`\n\n**Caused by:**\n`"
                        + (exception.getCause() == null ? "No reason given." : exception.getCause())
                        + "`"
                        + "`\n\n**Stacktrace:**\n`"
                        + exception.getStackTrace()[0]))
            .queue();
      }
    }
  }

  public void onLoad() {
    Corby.permissions.addAll(Arrays.asList(botPermissions));
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

  private boolean isCommand(Message message, MessageReceivedEvent event) {
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

  public static List<Command> getCommands() {
    return commands;
  }
}
