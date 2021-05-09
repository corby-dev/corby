/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.manager.config;

import java.awt.*;

public class Config {

  public String token;
  public String botPrefixDefault;
  public String ownerId;
  public int defaultCooldownSeconds;
  public String botName; // ! - These variables are initialized at startup
  public String botPfpUrl; // !
  public String inviteUrl; // !
  public String id; // !
  public String nameAsTag; // !
  public Color defaultColor; // !
  public Color errorColor; // !
  public Color successColor; // !
  public Color starboardColor; // !
  public String emoteTrash;
  public String emoteStar;
  public String emoteWhiteCheckMark;
  public int defaultStarboardStars;
  public boolean defaultStarboardIsEnabled;
  public String helpPageUrl;

  public Config(
      String token,
      String botPrefixDefault,
      String ownerId,
      int defaultCooldownSeconds,
      int defaultStarboardStars,
      boolean defaultStarboardIsEnabled,
      String helpPageUrl) {

    this.token = token;
    this.botPrefixDefault = botPrefixDefault;
    this.ownerId = ownerId;
    this.defaultCooldownSeconds = defaultCooldownSeconds;
    this.defaultStarboardStars = defaultStarboardStars;
    this.defaultStarboardIsEnabled = defaultStarboardIsEnabled;
    this.helpPageUrl = helpPageUrl;
  }

  public void initOther(
      Color defaultColor,
      Color errorColor,
      Color successColor,
      Color starboardColor,
      String botName,
      String botPfpUrl,
      String inviteUrl,
      String id,
      String nameAsTag,
      String emoteTrash,
      String emoteStar,
      String emoteWhiteCheckMark) {
    this.defaultColor = defaultColor;
    this.errorColor = errorColor;
    this.successColor = successColor;
    this.starboardColor = starboardColor;
    this.botName = botName;
    this.botPfpUrl = botPfpUrl;
    this.inviteUrl = inviteUrl;
    this.id = id;
    this.nameAsTag = nameAsTag;
    this.emoteStar = emoteStar;
    this.emoteTrash = emoteTrash;
    this.emoteWhiteCheckMark = emoteWhiteCheckMark;
  }

  public Config() {}

  public static class ExitCodes {
    public static final int NORMAL_SHUTDOWN_EXIT_CODE = 0;
    public static final int BAD_COMMAND_CONFIG = 11;
  }
}
