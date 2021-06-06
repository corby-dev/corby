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
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.model.Argument;
import xyz.d1snin.corby.model.Category;
import xyz.d1snin.corby.model.EmbedType;
import xyz.d1snin.corby.model.Prefix;

public class PrefixCommand extends Command {

  public PrefixCommand() {
    this.usage = "prefix";
    this.description = "Changes the bot prefix on the server.";
    this.category = Category.SETTINGS;

    this.userPerms = new Permission[] {Permission.ADMINISTRATOR};

    execute(
        u ->
            u.sendEmbed(
                EmbedType.DEFAULT,
                String.format(
                    "Current prefix is `%s`.",
                    MongoPrefixManager.getPrefix(u.getGuild()).getPrefix())));

    arg(
        u -> {
          Prefix prefix = MongoPrefixManager.getPrefix(u.getGuild());
          String currentPrefix = prefix.getPrefix();

          String newPrefix = u.getArgumentValue(0);

          if (currentPrefix.equals(newPrefix)) {
            u.sendEmbed(EmbedType.ERROR, String.format("Bot prefix is already `%s`.", newPrefix));
            return;
          }

          if (newPrefix.length() > 5) {
            u.sendEmbed(EmbedType.ERROR, "The prefix cannot be more than 5 characters.");
            return;
          }

          prefix.setPrefix(newPrefix);
          prefix.setGuild(u.getGuild().getId());

          MongoPrefixManager.writePrefix(prefix);

          u.sendEmbed(
              EmbedType.SUCCESS,
              String.format("The prefix was successfully changed to `%s`.", newPrefix));
        },
        new Argument(null, "<New Prefix>", false, false));
  }
}
