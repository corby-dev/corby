/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.enums;

public enum Category {
  MISC("Misc"),
  FUN("Fun"),
  SETTINGS("Bot Settings"),
  ADMIN("Admin");

  private final String name;

  Category(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
