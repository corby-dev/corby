/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;

@Getter
@NoArgsConstructor
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
  private String emoteDefaultBack;
  private String emoteDefaultNext;
  private int defaultStarboardStars;
  private boolean defaultStarboardStatus;
  private int defaultCooldown;
  private String emoteSuccess;
  private String emoteError;
  private String emoteBack;
  private String emoteNext;

  public Config(
      String token,
      String testBotToken,
      String botPrefixDefault,
      String ownerId,
      int defaultStarboardStars,
      boolean defaultStarboardStatus,
      int defaultCooldown,
      String emoteSuccess,
      String emoteError,
      String emoteBack,
      String emoteNext,
      String emoteStar,
      String emoteDefaultBack,
      String emoteDefaultNext) {

    this.token = token;
    this.testBotToken = testBotToken;
    this.botPrefixDefault = botPrefixDefault;
    this.ownerId = ownerId;
    this.defaultStarboardStars = defaultStarboardStars;
    this.defaultStarboardStatus = defaultStarboardStatus;
    this.defaultCooldown = defaultCooldown;
    this.emoteSuccess = emoteSuccess;
    this.emoteError = emoteError;
    this.emoteBack = emoteBack;
    this.emoteNext = emoteNext;
    this.emoteStar = emoteStar;
    this.emoteDefaultBack = emoteDefaultBack;
    this.emoteDefaultNext = emoteDefaultNext;
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
}
