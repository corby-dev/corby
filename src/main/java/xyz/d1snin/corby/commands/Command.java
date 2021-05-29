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
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.event.Listener;
import xyz.d1snin.corby.manager.CooldownsManager;
import xyz.d1snin.corby.model.Cooldown;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class Command extends Listener {

  private static final List<Command> commands = new ArrayList<>();

  @Getter protected String alias = null;
  @Getter protected String description = null;
  @Getter protected Category category = null;
  @Getter protected String[] usages = null;
  @Getter @Setter protected int cooldown = 0;
  @Getter protected String longDescription = null;
  @Getter protected Permission[] permissions = new Permission[0];
  @Getter protected Permission[] botPermissions = new Permission[0];
  private MessageReceivedEvent evt = null;

  public Command() {
    this.event = MessageReceivedEvent.class;
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

  public static List<Command> addAll(Command... commandz) {
    final String invalidCommandConfig =
        "It looks like one of the fields is not initialized in the command (%s), fields alias, description and category should be initialized. This command are ignored.";

    for (Command command : commandz) {

      if (command.getAlias() == null
          || command.getDescription() == null
          || command.getCategory() == null) {
        Corby.getLog().warn(String.format(invalidCommandConfig, command.getClass().getName()));

      } else {
        commands.add(command);
      }

      Corby.getPermissions().addAll(Arrays.asList(command.getBotPermissions()));

      command.setCooldown(
          command.getCooldown() == 0
              ? Corby.getConfig().getDefaultCooldown()
              : command.getCooldown());
    }

    return Arrays.asList(commandz);
  }

  public static List<Command> getCommands() {
    return commands;
  }

  protected abstract void execute(MessageReceivedEvent e, String[] args) throws IOException;

  protected abstract boolean isValidSyntax(MessageReceivedEvent e, String[] args);

  public String getUsagesString() {
    StringBuilder sb = new StringBuilder();
    String defaultUsage =
        String.format("`%s" + getAlias() + "`", MongoPrefixManager.getPrefix(evt.getGuild()));

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
                  "`%s" + getAlias() + " " + s + "`", MongoPrefixManager.getPrefix(evt.getGuild())))
          .append("\n");
    }
    return sb.toString();
  }

  @Override
  public void perform(GenericEvent thisEvent) throws IOException {
    evt = (MessageReceivedEvent) thisEvent;

    final String invalidPermission = "You must have permissions %s to use this command.";
    final String invalidBotPermission =
        "It looks like I do not have or I do not have enough permissions on this server, please invite me using [this](%s) link, I am leaving right now.";
    final String invalidSyntax = "**Incorrect Syntax:** `%s`\n\n**Usage:**\n%s";
    final String cooldown =
        "You are currently on cooldown, wait **%d seconds** to use this command again.";

    if (!evt.getChannelType().isGuild()) {
      return;
    }

    Message msg = evt.getMessage();

    if (evt.getAuthor().isBot()) {
      return;
    }

    if (isCommand(msg, evt)) {
      if (!hasPermission(evt)) {
        evt.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    evt.getAuthor(),
                    String.format(invalidPermission, getPermissionString()),
                    evt.getGuild()))
            .queue();
        return;
      }

      if ((getCategory() == Category.ADMIN)
          && !evt.getAuthor().getId().equals(Corby.getConfig().getOwnerId())) {
        return;
      }

      if (!evt.getGuild()
          .getSelfMember()
          .hasPermission(evt.getTextChannel(), Corby.getPermissions())) {
        if (evt.getGuild().getOwner() == null) {
          evt.getGuild().leave().queue();
          return;
        }

        OtherUtils.sendPrivateMessageSafe(
            Objects.requireNonNull(evt.getGuild().getOwner()).getUser(),
            Embeds.create(
                EmbedTemplate.ERROR,
                Corby.getFirstJda().getSelfUser(),
                String.format(invalidBotPermission, Corby.getConfig().getInviteUrl())),
            () -> {
              /* epic failure */
            });

        evt.getGuild().leave().queue();

        return;
      }

      if (evt.getGuild().getBotRole() == null
          || !Objects.requireNonNull(evt.getGuild().getBotRole())
              .hasPermission(Corby.getPermissions())
          || !evt.getGuild()
              .getSelfMember()
              .hasPermission(evt.getTextChannel(), Corby.getPermissions())) {
        evt.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    evt.getAuthor(),
                    String.format(invalidBotPermission, Corby.getConfig().getInviteUrl()),
                    evt.getGuild()))
            .queue();
        evt.getGuild().leave().queue();
        return;
      }

      if (!isValidSyntax(evt, getCommandArgs(evt.getMessage()))) {
        evt.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    evt.getAuthor(),
                    String.format(
                        invalidSyntax, evt.getMessage().getContentRaw(), getUsagesString()),
                    evt.getGuild()))
            .queue();
        return;
      }

      int cooldownTime = CooldownsManager.getCooldown(evt.getAuthor(), this);

      if (cooldownTime > 0) {
        evt.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    evt.getAuthor(),
                    String.format(cooldown, cooldownTime),
                    evt.getGuild()))
            .queue();
        return;
      }

      execute(evt, getCommandArgs(msg));

      CooldownsManager.setCooldown(new Cooldown(evt.getAuthor(), this.getCooldown(), this));
    }
  }

  protected String getMessageContent() {
    return evt.getMessage().getContentRaw();
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
                Corby.getConfig()
                    .getOwnerId()); // <- Don't worry, this is only needed to test the bot.
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
