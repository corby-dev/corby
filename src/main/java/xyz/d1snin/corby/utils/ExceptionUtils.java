package xyz.d1snin.corby.utils;

import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.enums.EmbedTemplate;

import java.util.Objects;

public class ExceptionUtils {
  public static void processException(Exception exception) {

    final String message = "**An error was handled.**\n```%s: %s\n%s\n%s```";
    OtherUtils.sendPrivateMessageSafe(
        Objects.requireNonNull(Corby.getApi().getUserById(Corby.config.ownerId)),
        Embeds.create(
            EmbedTemplate.DEFAULT,
            Corby.getApi().getUserById(Corby.config.ownerId),
            String.format(
                message,
                exception.getClass().getName(),
                exception.getMessage(),
                exception.getCause(),
                getStackTrace(exception)),
            null,
            null),
        () ->
            Corby.logger.warn(
                "You have disabled messages from the bot, please enable them to receive information about errors during runtime."));
  }

  private static String getStackTrace(Exception e) {
    StringBuilder sb = new StringBuilder();
    for (StackTraceElement element : e.getStackTrace()) {
      sb.append(element.toString()).append("\n");
    }
    return sb.toString();
  }
}
