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
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.model.Argument;
import xyz.d1snin.corby.model.Category;
import xyz.d1snin.corby.model.EmbedTemplate;
import xyz.d1snin.corby.utils.FormatUtils;

import java.util.Objects;

public class BottomCommand extends Command {

  public BottomCommand() {
    this.usage = "bottom";
    this.description = "Encrypts your message using a bottom cipher";
    this.category = Category.FUN;

    final int edMsgLimit = 300;
    final String result =
        "%s\n\nPowered by [bottom-software-foundation](https://github.com/bottom-software-foundation/bottom-java).";
    final String longR = "Sorry, generated result is too long.";

    arg(
        u -> {
          String encodedMessage = Bottom.encode(Objects.requireNonNull(u.getContent(2)));

          if (encodedMessage.length() > edMsgLimit) {
            u.sendEmbed(EmbedTemplate.ERROR, longR);
            return;
          }

          u.sendEmbed(
              EmbedTemplate.SUCCESS,
              String.format(
                  result,
                  String.format(FormatUtils.formatMessageKeyText("Result", "%s"), encodedMessage)));
        },
        new Argument("encode", "<Message 2 - 200 characters>", true, true));

    arg(
        u -> {
          try {

            String decodedMessage = Bottom.decode(u.getArgumentValue(0));

            if (decodedMessage.length() > edMsgLimit) {
              u.sendEmbed(EmbedTemplate.ERROR, longR);
              return;
            }

            u.sendEmbed(
                EmbedTemplate.SUCCESS,
                String.format(
                    result,
                    String.format(
                        FormatUtils.formatMessageKeyText("Result", "%s"), decodedMessage)));
          } catch (TranslationError error) {

            u.sendEmbed(
                EmbedTemplate.ERROR,
                String.format("You cannot decrypt this message: %s", error.getWhy()));
          }
        },
        new Argument("decode", "<Message 2 - 200 characters>", true, true));
  }
}
