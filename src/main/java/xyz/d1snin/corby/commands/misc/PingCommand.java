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

package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.util.List;

public class PingCommand extends Command {

  public PingCommand() {
    this.alias = "ping";
    this.description = "Provides the current ping of the bot";
    this.category = Category.MISC;
    this.usages = new String[] {"alias", "full"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {
    if (args.length > 1 && args[1].equals("full")) {
      StringBuilder stringBuilder = new StringBuilder();

      List<JDA> shards = Corby.getShards().getShards();

      if (shards.size() > 1) {
        for (int i = 0; i < shards.size(); i++) {
          stringBuilder
              .append(
                  OtherUtils.formatMessageKeyText(
                      "Shard " + (i + 1), shards.get(i).getGatewayPing() + "ms"))
              .append("\n");
        }
      }

      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.DEFAULT,
                  e.getAuthor(),
                  OtherUtils.formatMessageKeyText("Gateway ping", Corby.getPing())
                      + "\n\n"
                      + stringBuilder))
          .queue();
      return;
    }

    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT,
                e.getAuthor(),
                OtherUtils.formatMessageKeyText(
                    "Gateway ping", String.format("%sms", Corby.getPing())),
                e.getGuild()))
        .queue();
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length <= 2;
  }
}
