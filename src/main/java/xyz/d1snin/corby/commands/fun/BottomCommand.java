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

package xyz.d1snin.corby.commands.fun;

import com.github.bottomSoftwareFoundation.bottom.Bottom;
import com.github.bottomSoftwareFoundation.bottom.TranslationError;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

public class BottomCommand extends Command {

  public BottomCommand() {
    this.alias = "bottom";
    this.description = "Encrypts your message using a bottom cipher";
    this.category = Category.FUN;
    this.usages =
        new String[] {"encode <Message 2 - 200 characters>", "decode <Message 2 - 200 characters>"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {
    try {
      final int edMsgLimit = 300;

      final String result =
          "%s\n\nPowered by [bottom-software-foundation](https://github.com/bottom-software-foundation/bottom-java).";

      final String longR = "Sorry, generated result is too long.";

      final String message =
          e.getMessage()
              .getContentRaw()
              .substring(MongoPrefixManager.getPrefix(e.getGuild()).getPrefix().length() + 14);

      switch (args[1]) {
        case "encode":
          String encodedMessage = Bottom.encode(message);

          if (encodedMessage.length() > edMsgLimit) {
            e.getTextChannel()
                .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), longR, e.getGuild()))
                .queue();
            return;
          }

          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.SUCCESS,
                      e.getAuthor(),
                      String.format(
                          result,
                          String.format(
                              OtherUtils.formatMessageKeyText("Result", "%s"), encodedMessage)),
                      e.getGuild(),
                      null,
                      null))
              .queue();

          break;

        case "decode":
          String decodedMessage = Bottom.decode(message);

          if (decodedMessage.length() > edMsgLimit) {
            e.getTextChannel()
                .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), longR, e.getGuild()))
                .queue();
            return;
          }

          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.SUCCESS,
                      e.getAuthor(),
                      String.format(
                          OtherUtils.formatMessageKeyText("Result", "%s"), decodedMessage),
                      e.getGuild()))
              .queue();

          break;

        default:
      }
    } catch (TranslationError exception) {

      final String tErr = "You cannot decrypt this message.";

      e.getTextChannel()
          .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), tErr, e.getGuild()))
          .queue();
    }
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    if (args.length < 3) {
      return false;
    }

    return getMessageContent().length() <= 200 && getMessageContent().length() >= 2;
  }
}
