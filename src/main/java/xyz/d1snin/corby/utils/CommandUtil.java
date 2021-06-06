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

package xyz.d1snin.corby.utils;

import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.model.Argument;
import xyz.d1snin.corby.model.Category;
import xyz.d1snin.corby.model.EmbedType;
import xyz.d1snin.corby.model.Statement;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@Immutable
public class CommandUtil {

  private final Command cmd;
  private final GuildMessageReceivedEvent event;

  private final Message message;
  private final String content;
  private final List<String> args;
  private final TextChannel channel;
  private final User author;
  private final Guild guild;
  private final Role botRole;
  private final StringBuilder sb = new StringBuilder();

  public CommandUtil(Command cmd, GuildMessageReceivedEvent event) {
    this.cmd = cmd;
    this.event = event;

    message = event.getMessage();
    content = message.getContentRaw();
    args = Arrays.asList(content.split("\\s+"));
    channel = event.getChannel();
    author = event.getAuthor();
    guild = event.getGuild();
    botRole = guild.getBotRole();
  }

  public final boolean isCommand() {
    if (!Command.getCommands().contains(getCmd())) {
      return false;
    }

    String prefix = MongoPrefixManager.getPrefix(guild).getPrefix();
    return args.get(0).toLowerCase().equals(prefix + getCmd().getUsage())
        && args.get(0).startsWith(prefix);
  }

  public final String getContent(int fromPosition) {
    if (args.size() < fromPosition) {
      return null;
    }

    sb.setLength(0);

    for (int i = fromPosition; i < args.size(); i++) {
      sb.append(args.get(i)).append(" ");
    }

    return sb.toString();
  }

  public final String getArgumentValue(int index) {
    return getCmd().getCurrentStatement().getArguments().get(index).getValue();
  }

  public final boolean hasPermissions() {
    if (getCmd().getUserPerms().length == 0) {
      return true;
    }

    return Objects.requireNonNull(event.getMember()).hasPermission(getCmd().getUserPerms())
        || OtherUtils.isOwner(author);
  }

  public final String getRequiredPermissionsAsString() {
    sb.setLength(0);
    Permission[] perms = getCmd().getUserPerms();

    for (int i = 0; i < perms.length; i++) {
      sb.append(perms[i].getName()).append(i == perms.length - 1 ? "" : ", ");
    }

    return sb.toString();
  }

  public final String getRequiredArgsAsString(Statement statement) {
    StringBuilder s = new StringBuilder();

    for (Argument a : statement.getArguments()) {
      s.append(
              a.getUsage() == null
                  ? a.getType()
                  : a.getUsage() + (!a.isValueRequired() ? "" : " " + a.getType()))
          .append(" ");
    }

    return s.toString().trim() + "`";
  }

  public final String getUsagesAsString() {
    sb.setLength(0);

    String defaultUsage =
        String.format("`%s" + getCmd().getUsage() + "`", MongoPrefixManager.getPrefix(guild));

    if (getCmd().getStatements().isEmpty()) {
      return defaultUsage;
    }

    if (getCmd().getDefaultAction() != null) {
      sb.append(defaultUsage).append("\n");
    }

    for (Statement s : getCmd().getStatements()) {
      sb.append(
              String.format("`%s" + getCmd().getUsage() + " ", MongoPrefixManager.getPrefix(guild)))
          .append(getRequiredArgsAsString(s))
          .append("\n");
    }

    return sb.toString();
  }

  public final void sendEmbed(EmbedType template, String content) {
    getChannel().sendMessage(Embeds.create(template, author, content, guild)).queue();
  }

  public final MessageEmbed createEmbed(EmbedType template, String content) {
    return Embeds.create(template, author, content, guild);
  }

  public final void trigger() {
    sendEmbed(
        EmbedType.ERROR,
        String.format(
            "**Incorrect Syntax:** `%s`\n\n**Usage:**\n%s", getContent(), getUsagesAsString()));
  }

  public List<Command> getCommandsByCategory(Category category) {
    List<Command> result = new ArrayList<>();

    for (Command c : Command.getCommands()) {
      if (c.getCategory() == category) {
        result.add(c);
      }
    }
    return result;
  }

  public final Command getCommandByUsage(String usage) {
    for (Command c : Command.getCommands()) {
      if (c.getUsage().equals(usage)) {
        return c;
      }
    }
    return null;
  }
}
