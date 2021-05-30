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

package xyz.d1snin.corby.commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.model.Prefix;
import xyz.d1snin.corby.utils.Embeds;

public class PrefixCommand extends Command {

  public PrefixCommand() {
    this.alias = "prefix";
    this.description = "Changes the bot prefix on the server.";
    this.category = Category.SETTINGS;
    this.usages = new String[] {"alias", "<New Prefix>"};

    this.permissions = new Permission[] {Permission.ADMINISTRATOR};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {

    final String currPrefix = "Current prefix is `%s`.";
    final String prefixAlready = "Bot prefix is already `%s`.";
    final String cannotBeMoreThen = "The prefix cannot be more than 5 characters.";
    final String successChanged = "The prefix was successfully changed to `%s`.";

    Prefix prefix = MongoPrefixManager.getPrefix(e.getGuild());
    String currentPrefix = prefix.getPrefix();

    if (args.length < 2) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.DEFAULT,
                  e.getAuthor(),
                  String.format(currPrefix, currentPrefix),
                  e.getGuild(),
                  null,
                  null))
          .queue();
      return;
    }

    String newPrefix = args[1];

    if (currentPrefix.equals(newPrefix)) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.ERROR,
                  e.getAuthor(),
                  String.format(prefixAlready, newPrefix),
                  e.getGuild()))
          .queue();
      return;
    }

    if (newPrefix.length() > 5) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), cannotBeMoreThen, e.getGuild()))
          .queue();
      return;
    }

    prefix.setPrefix(newPrefix);
    prefix.setGuild(e.getGuild().getId());

    MongoPrefixManager.writePrefix(prefix);

    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.SUCCESS,
                e.getAuthor(),
                String.format(successChanged, newPrefix),
                e.getGuild()))
        .queue();
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length <= 2;
  }
}
