/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.utils;

import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.enums.EmbedTemplate;

import java.util.Objects;

public class ExceptionUtils {
  public static void processException(Exception exception) {
    Corby.getService()
        .execute(
            () -> {
              final String message = "**An error was handled.**\n```%s: %s\n%s\n%s```";
              OtherUtils.sendPrivateMessageSafe(
                  Objects.requireNonNull(
                      Corby.getShards().getUserById(Corby.getConfig().getOwnerId())),
                  Embeds.create(
                      EmbedTemplate.DEFAULT,
                      Corby.getShards().getUserById(Corby.getConfig().getOwnerId()),
                      String.format(
                          message,
                          exception.getClass().getName(),
                          exception.getMessage(),
                          exception.getCause(),
                          getStackTrace(exception))),
                  () ->
                      Corby.getLog()
                          .warn(
                              "You have disabled messages from the bot, please enable them to receive information about errors during runtime."));
            });
  }

  private static String getStackTrace(Exception e) {
    StringBuilder sb = new StringBuilder();
    for (StackTraceElement element : e.getStackTrace()) {
      if (sb.length() > 1800) {
        break;
      }
      sb.append(element.toString()).append("\n");
    }
    return sb.toString();
  }
}
