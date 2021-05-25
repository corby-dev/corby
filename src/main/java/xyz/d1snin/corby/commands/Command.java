/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.manager.CooldownsManager;
import xyz.d1snin.corby.model.Cooldown;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.ExceptionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class Command extends ListenerAdapter {

  private static final List<Command> commands = new ArrayList<>();
  @Getter protected String alias = null;
  @Getter protected String description = null;
  @Getter protected Category category = null;
  @Getter protected String[] usages = null;
  @Getter @Setter protected int cooldown = 0;
  @Getter protected String longDescription = null;
  @Getter protected Permission[] permissions = new Permission[0];
  @Getter protected Permission[] botPermissions = new Permission[0];
  private MessageReceivedEvent event = null;

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
        || command.getCategory() == null) {
      Corby.log.warn(String.format(invalidCommandConfig, command.getClass().getName()));
    } else {
      commands.add(command);
    }
    Corby.permissions.addAll(Arrays.asList(command.getBotPermissions()));

    command.setCooldown(
        command.getCooldown() == 0 ? Corby.config.getDefaultCooldown() : command.getCooldown());
    return command;
  }

  public static List<Command> getCommands() {
    return commands;
  }

  protected abstract void execute(MessageReceivedEvent e, String[] args) throws IOException;

  protected abstract boolean isValidSyntax(MessageReceivedEvent e, String[] args);

  public String getUsagesString() {
    StringBuilder sb = new StringBuilder();
    String defaultUsage =
        String.format("`%s" + getAlias() + "`", MongoPrefixManager.getPrefix(event.getGuild()));

    if (getUsages() == null || getUsages().length < 1) {
      return defaultUsage;
    }

    if (Arrays.asList(getUsages()).contains("alias")) {
      sb.append(defaultUsage).append("\n");
    }

    for (String s : getUsages()) {
      if (s.equals("alias")) {
        continue;
      }

      sb.append(
              String.format(
                  "`%s" + getAlias() + " " + s + "`",
                  MongoPrefixManager.getPrefix(event.getGuild())))
          .append("\n");
    }
    return sb.toString();
  }

  public void onMessageReceived(@NotNull MessageReceivedEvent e) {
    event = e;

    final String invalidPermission = "You must have permissions %s to use this command.";
    final String invalidBotPermission =
        "It looks like I do not have or I do not have enough permissions on this server, please invite me using [this](%s) link, I am leaving right now.";
    final String invalidSyntax = "**Incorrect Syntax:** `%s`\n\n**Usage:**\n%s";
    final String cooldown =
        "You are currently on cooldown, wait **%d seconds** to use the command again.";

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
                    String.format(invalidPermission, getPermissionString()),
                    e.getGuild()))
            .queue();
        return;
      }

      if ((getCategory() == Category.ADMIN)
          && !e.getAuthor().getId().equals(Corby.config.getOwnerId())) {
        return;
      }

      if (e.getGuild().getBotRole() == null
          || !Objects.requireNonNull(e.getGuild().getBotRole()).hasPermission(Corby.permissions)) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    e.getAuthor(),
                    String.format(invalidBotPermission, Corby.config.getInviteUrl()),
                    e.getGuild()))
            .queue();
        e.getGuild().leave().queue();
        return;
      }

      if (!isValidSyntax(e, getCommandArgs(e.getMessage()))) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    e.getAuthor(),
                    String.format(invalidSyntax, e.getMessage().getContentRaw(), getUsagesString()),
                    e.getGuild()))
            .queue();
        return;
      }

      int cooldownTime = CooldownsManager.getCooldown(e.getAuthor(), this);

      if (cooldownTime > 0) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    e.getAuthor(),
                    String.format(cooldown, cooldownTime),
                    e.getGuild()))
            .queue();
        return;
      }

      try {
        execute(e, getCommandArgs(msg));
      } catch (Exception exception) {
        ExceptionUtils.processException(exception);
      }

      CooldownsManager.setCooldown(new Cooldown(e.getAuthor(), this.getCooldown(), this));
    }
  }

  protected String getMessageContent() {
    return event.getMessage().getContentRaw();
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
            .equals(
                Corby.config.getOwnerId()); // <- Don't worry, this is only needed to test the bot.
  }

  private String getPermissionString() {

    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < getPermissions().length; i++) {
      sb.append(getPermissions()[i].getName())
          .append((i == getPermissions().length - 1) ? "" : ", ");
    }

    return sb.toString();
  }

  @SuppressWarnings("SameParameterValue")
  protected String getArgsString(int fromIndex, Message msg) {
    StringBuilder sb = new StringBuilder();
    String[] args = getCommandArgs(msg);

    for (int i = fromIndex; i < args.length; i++) {
      sb.append(args[i]).append(" ");
    }

    return sb.toString();
  }

  private String[] getCommandArgs(Message msg) {
    return msg.getContentRaw().split("\\s+");
  }

  private boolean isCommand(Message message, MessageReceivedEvent event) {
    if (!getCommands().contains(this)) {
      return false;
    }
    String prefix = MongoPrefixManager.getPrefix(event.getGuild()).getPrefix();
    String[] args = getCommandArgs(message);
    return Arrays.asList(args).get(0).toLowerCase().equals(prefix + getAlias())
        && args[0].startsWith(prefix);
  }
}
