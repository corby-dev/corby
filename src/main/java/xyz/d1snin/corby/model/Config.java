/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.model;

import lombok.Getter;

import java.awt.*;

@Getter()
public class Config {
  public static final int NORMAL_SHUTDOWN_EXIT_CODE = 0;
  private String token;
  private String testBotToken;
  private String botPrefixDefault;
  private String ownerId;
  private String botName;
  private String botPfpUrl;
  private String inviteUrl;
  private String id;
  private String nameAsTag;
  private Color defaultColor;
  private Color errorColor;
  private Color successColor;
  private Color starboardColor;
  private String emoteStar;
  private int defaultStarboardStars;
  private boolean defaultStarboardStatus;
  private int defaultCooldown;
  private String mongoPassword;
  private String emoteSuccess;
  private String emoteError;

  public Config(
      String token,
      String testBotToken,
      String botPrefixDefault,
      String ownerId,
      int defaultStarboardStars,
      boolean defaultStarboardStatus,
      int defaultCooldown,
      String mongoPassword,
      String emoteSuccess,
      String emoteError) {

    this.token = token;
    this.testBotToken = testBotToken;
    this.botPrefixDefault = botPrefixDefault;
    this.ownerId = ownerId;
    this.defaultStarboardStars = defaultStarboardStars;
    this.defaultStarboardStatus = defaultStarboardStatus;
    this.defaultCooldown = defaultCooldown;
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
}
