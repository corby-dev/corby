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
    this.emoteTrash = emoteTrash;
    this.emoteStar = emoteStar;
    this.emoteWhiteCheckMark = emoteWhiteCheckMark;
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
      String nameAsTag) {
    this.defaultColor = defaultColor;
    this.errorColor = errorColor;
    this.successColor = successColor;
    this.starboardColor = starboardColor;
    this.botName = botName;
    this.botPfpUrl = botPfpUrl;
    this.inviteUrl = inviteUrl;
    this.id = id;
    this.nameAsTag = nameAsTag;
  }

  public Config() {}

  public static class ExitCodes {
    public static final int NORMAL_SHUTDOWN_EXIT_CODE = 0;
    public static final int CANT_CONNECT_TO_THE_DATABASE_EXIT_CODE = 11;
    public static final int BAD_TOKEN_EXIT_CODE = 21;
    // --Commented out by Inspection (03.05.2021, 22:49):public static final int BAD_CONFIG_EXIT_CODE = 22;
  }
}
