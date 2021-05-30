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

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.enums.EmbedTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class OtherUtils {
  public static void sendPrivateMessageSafe(User user, MessageEmbed success, Runnable onFailure) {
    user.openPrivateChannel()
        .complete()
        .sendMessage(success)
        .queue(
            response -> {
              /* ok */
            },
            fail -> onFailure.run());
  }

  public static void sendLoadingAndEdit(
      MessageReceivedEvent e, Supplier<MessageEmbed> messageSupplier) {
    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT, e.getAuthor(), "Looking at the data...", e.getGuild()))
        .queue(message -> message.editMessage(messageSupplier.get()).queue());
  }

  public static boolean isNumeric(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static String formatMillis(long ms) {
    return String.format(
        "%02dh %02dm %02ds",
        TimeUnit.MILLISECONDS.toHours(ms),
        TimeUnit.MILLISECONDS.toMinutes(ms) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(ms) % TimeUnit.MINUTES.toSeconds(1));
  }

  public static String formatMessageKeyText(String key, String text) {
    return formatMessageKeyText(key, text, true);
  }

  public static String formatMessageKeyText(String key, String text, boolean italicText) {
    return String.format("**%s:** " + (italicText ? "*%s*" : "%s"), key, text);
  }

  public static boolean isImage(String url) {
    String lowered = url.toLowerCase();
    return (lowered.endsWith(".jpg") || lowered.endsWith(".png") || lowered.endsWith(".jpeg"));
  }

  public static boolean isOwner(User user) {
    return user.getId().equals(Corby.getConfig().getOwnerId());
  }
}
