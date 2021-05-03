package xyz.d1snin.corby.utils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

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

  public static void sendPrivateMessageSafe(
      User user, MessageEmbed success, Runnable onFailure, Runnable onSuccess) {
    user.openPrivateChannel()
        .complete()
        .sendMessage(success)
        .queue(
            response -> onSuccess.run(),
            fail -> onFailure.run());
  }
}
