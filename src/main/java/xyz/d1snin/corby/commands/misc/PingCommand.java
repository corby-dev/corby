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
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.model.Argument;
import xyz.d1snin.corby.model.Category;
import xyz.d1snin.corby.model.EmbedType;
import xyz.d1snin.corby.utils.FormatUtils;

import java.util.List;

public class PingCommand extends Command {

  public PingCommand() {
    this.usage = "ping";
    this.description = "Provides the current ping of the bot";
    this.category = Category.MISC;

    execute(
        u ->
            u.sendEmbed(
                EmbedType.DEFAULT,
                FormatUtils.formatMessageKeyText(
                    "Gateway ping", String.format("%sms", Corby.getPing()))));

    arg(
        u -> {
          StringBuilder stringBuilder = new StringBuilder();

          List<JDA> shards = Corby.getShards().getShards();

          if (shards.size() > 1) {
            for (int i = 0; i < shards.size(); i++) {
              stringBuilder
                  .append(
                      FormatUtils.formatMessageKeyText(
                          "Shard " + (i + 1), shards.get(i).getGatewayPing() + "ms"))
                  .append("\n");
            }
          }

          u.sendEmbed(
              EmbedType.DEFAULT,
              FormatUtils.formatMessageKeyText(
                      "Average gateway ping", String.format("%sms", Corby.getPing()))
                  + "\n\n"
                  + stringBuilder);
        },
        new Argument("full", "full", false, false));
  }
}
