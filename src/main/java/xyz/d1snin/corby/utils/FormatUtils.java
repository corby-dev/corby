package xyz.d1snin.corby.utils;

import java.util.concurrent.TimeUnit;

public class FormatUtils {
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
}
