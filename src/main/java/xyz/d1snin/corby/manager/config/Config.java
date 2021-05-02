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
  public Color defaultColor;
  public Color errorColor;
  public Color successColor;
  public Color starboardColor;
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
      Color defaultColor,
      Color errorColor,
      Color successColor,
      Color starboardColor,
      String emoteTrash,
      String emoteStar,
      String emoteWhiteCheckMark,
      int defaultStarboardStars,
      boolean defaultStarboardIsEnabled,
      String helpPageUrl) {

    this.token = token;
    this.botPrefixDefault = botPrefixDefault;
    this.ownerId = ownerId;
    this.defaultCooldownSeconds = defaultCooldownSeconds;
    this.defaultColor = defaultColor;
    this.errorColor = errorColor;
    this.successColor = successColor;
    this.starboardColor = starboardColor;
    this.emoteTrash = emoteTrash;
    this.emoteStar = emoteStar;
    this.emoteWhiteCheckMark = emoteWhiteCheckMark;
    this.defaultStarboardStars = defaultStarboardStars;
    this.defaultStarboardIsEnabled = defaultStarboardIsEnabled;
    this.helpPageUrl = helpPageUrl;
  }

  public void setBotPfpUrl(String botPfpUrl) {
    this.botPfpUrl = botPfpUrl;
  }

  public void setInviteUrl(String inviteUrl) {
    this.inviteUrl = inviteUrl;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setNameAsTag(String nameAsTag) {
    this.nameAsTag = nameAsTag;
  }

  public void setBotName(String botName) {
    this.botName = botName;
  }

  public static class ExitCodes {
    public static int NORMAL_SHUTDOWN_EXIT_CODE = 0;
    public static int CANT_CONNECT_TO_THE_DATABASE_EXIT_CODE = 11;
    public static int BAD_TOKEN_EXIT_CODE = 21;
    public static int BAD_CONFIG_EXIT_CODE = 22;
  }
}
