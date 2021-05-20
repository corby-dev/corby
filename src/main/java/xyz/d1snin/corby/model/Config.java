/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.model;

import java.awt.*;

public class Config {

  public String token;
  public String botPrefixDefault;
  public String ownerId;
  public String botName; // ! - These variables are initialized at startup
  public String botPfpUrl; // !
  public String inviteUrl; // !
  public String id; // !
  public String nameAsTag; // !
  public Color defaultColor; // !
  public Color errorColor; // !
  public Color successColor; // !
  public Color starboardColor; // !
  public String emoteStar;
  public int defaultStarboardStars;
  public boolean defaultStarboardStatus;
  public String mongoHostname;
  public int mongoPort;
  public String mongoDbName;
  public String mongoUser;
  public String mongoPassword;
  public String emoteSuccess;
  public String emoteError;

  public Config(
      String token,
      String botPrefixDefault,
      String ownerId,
      int defaultStarboardStars,
      boolean defaultStarboardStatus,
      String mongoHostname,
      int mongoPort,
      String mongoDbName,
      String mongoUser,
      String mongoPassword,
      String emoteSuccess,
      String emoteError) {

    this.token = token;
    this.botPrefixDefault = botPrefixDefault;
    this.ownerId = ownerId;
    this.defaultStarboardStars = defaultStarboardStars;
    this.defaultStarboardStatus = defaultStarboardStatus;
    this.mongoHostname = mongoHostname;
    this.mongoPort = mongoPort;
    this.mongoDbName = mongoDbName;
    this.mongoUser = mongoUser;
    this.mongoPassword = mongoPassword;
    this.emoteSuccess = emoteSuccess;
    this.emoteError = emoteError;
  }

  public Config() {}

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
      String emoteStar) {
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
  }

  public static class ExitCodes {
    public static final int NORMAL_SHUTDOWN_EXIT_CODE = 0;
  }
}
