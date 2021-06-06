/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Corby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package xyz.d1snin.corby.commands;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.event.Listener;
import xyz.d1snin.corby.manager.CooldownsManager;
import xyz.d1snin.corby.model.Category;
import xyz.d1snin.corby.model.*;
import xyz.d1snin.corby.model.EmbedType;
import xyz.d1snin.corby.utils.CommandUtil;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class Command extends Listener {

  @Getter private static final List<Command> commands = new ArrayList<>();
  @Getter private final List<Statement> statements = new ArrayList<>();
  @Getter protected String usage = null;
  @Getter protected String description = null;
  @Getter protected Category category = null;
  @Getter @Setter protected int cooldown = 0;
  @Getter protected String longDescription = null;
  @Getter protected Permission[] userPerms = new Permission[0];
  @Getter protected Permission[] botPerms = new Permission[0];
  @Getter private Consumer<CommandUtil> defaultAction = null;
  @Getter private Statement currentStatement;
  private CommandUtil util;
  private List<String> args;
  private User author;
  public Command() {
    this.event = GuildMessageReceivedEvent.class;
  }

  public static List<Command> addAll(Command... commandz) {
    final String invalidCommandConfig =
        "It looks like one of the fields is not initialized in the command (%s), fields usage, description and category should be initialized. This command are ignored.";

    for (Command command : commandz) {

      if (command.getUsage() == null
          || command.getDescription() == null
          || command.getCategory() == null) {
        Corby.getLog().warn(String.format(invalidCommandConfig, command.getClass().getName()));

      } else {
        commands.add(command);
      }

      Corby.getPermissions().addAll(Arrays.asList(command.getBotPerms()));

      command.setCooldown(
          command.getCooldown() == 0
              ? Corby.getConfig().getDefaultCooldown()
              : command.getCooldown());
    }

    return Arrays.asList(commandz);
  }

  @Override
  public void perform(GenericGuildEvent event) {
    GuildMessageReceivedEvent e = (GuildMessageReceivedEvent) event;

    util = new CommandUtil(this, e);
    author = util.getAuthor();
    args = util.getArgs();

    Message msg = util.getMessage();
    TextChannel ch = util.getChannel();
    Guild guild = util.getGuild();
    Role botRole = util.getBotRole();

    if (author.isBot()
        || !msg.isFromGuild()
        || msg.isWebhookMessage()
        || !getCommands().contains(this)) {
      return;
    }

    if (util.isCommand()) {

      String invalidBotPerms =
          "I do not have or I do not have enough permissions on this server, please invite me using [this](%s) link, I am leaving right now.";

      if (!util.hasPermissions()) {
        ch.sendMessage(
                Embeds.create(
                    EmbedType.ERROR,
                    author,
                    String.format(
                        "You must have permissions %s to use this command.",
                        util.getRequiredPermissionsAsString()),
                    guild))
            .queue();
        return;
      }

      if (category == Category.ADMIN && !OtherUtils.isOwner(author)) {
        return;
      }

      if (!guild.getSelfMember().hasPermission(ch, Corby.getPermissions())) {

        if (guild.getOwner() == null) {
          guild.leave().queue();
          return;
        }

        OtherUtils.sendPrivateMessageSafe(
            guild.getOwner().getUser(),
            Embeds.create(
                EmbedType.ERROR,
                Corby.getFirstJda().getSelfUser(),
                String.format(invalidBotPerms, Corby.getConfig().getInviteUrl())),
            () -> {
              /* epic fail */
            });

        guild.leave().queue();

        return;
      }

      if (botRole == null
          || !botRole.hasPermission(Corby.getPermissions())
          || !guild.getSelfMember().hasPermission(ch, Corby.getPermissions())) {
        ch.sendMessage(
                Embeds.create(
                    EmbedType.ERROR,
                    author,
                    String.format(invalidBotPerms, Corby.getConfig().getInviteUrl()),
                    guild))
            .queue();

        guild.leave().queue();

        return;
      }

      int cooldown = CooldownsManager.getCooldown(author, this);

      if (cooldown > 0) {
        ch.sendMessage(
                Embeds.create(
                    EmbedType.ERROR,
                    author,
                    String.format(
                        "You are currently on cooldown, wait **%d seconds** to use this command again.",
                        cooldown),
                    guild))
            .queue();
        return;
      }

      if (!tryExec()) {
        util.trigger();
      }
    }
  }

  private boolean tryExec() {
    if (defaultAction == null && statements.isEmpty()) {
      Corby.getLog()
          .warn(
              "You did not set the actions on execution in the command constructor: "
                  + this.getClass().getSimpleName());
      return false;
    }

    if (args.size() < 2) {

      if (defaultAction == null) {
        return false;
      }

      defaultAction.accept(util);

      return true;

    } else {

      if (statements.isEmpty()) {
        return false;
      }

      outer:
      for (Statement s : statements) {
        if (s.getLength() != 0 && s.getLength() != args.size() - 1) {
          continue;
        }

        List<Argument> arguments = s.getArguments();

        int argCount = 0;

        for (int i = 0; i < arguments.size(); i++) {

          Argument arg = arguments.get(argCount);

          if (arg.getUsage() != null) {

            if (!arg.getUsage().equals(args.get(i + 1))) {

              if (statements.indexOf(s) == statements.size() - 1) {
                return false;

              } else {

                continue outer;
              }
            }

            if (arg.isValueRequired()) {

              if (arg.isVariableLength()) {
                arg.setValue(util.getContent(i + 2));
                break;
              }

              arg.setValue(util.getContent(i + 2));

              i++;
            }

          } else {

            if (arg.isVariableLength()) {
              arg.setValue(util.getContent(i + 1));
              break;
            }

            arg.setValue(args.get(i + 1));
          }

          argCount++;
        }

        currentStatement = s;

        s.getConsumer().accept(util);

        CooldownsManager.setCooldown(new Cooldown(author, this.cooldown, this));

        return true;
      }
    }

    return false;
  }

  protected void arg(Consumer<CommandUtil> consumer, Argument... arguments) {

    Statement statement = new Statement(Arrays.asList(arguments), consumer);

    this.statements.add(statement);

    for (Argument a : arguments) {
      if (a.isVariableLength()) {
        statement.setLength(0);
        return;
      }

      if (a.isValueRequired()) {

        statement.setLength(statement.getLength() + 2);

      } else {

        statement.setLength(statement.getLength() + 1);
      }
    }
  }

  protected void execute(Consumer<CommandUtil> consumer) {
    defaultAction = consumer;
  }

  public CommandUtil getUtil(GuildMessageReceivedEvent e) {
    return new CommandUtil(this, e);
  }
}
