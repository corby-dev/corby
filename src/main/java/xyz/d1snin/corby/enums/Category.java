package xyz.d1snin.corby.enums;

public enum Category {
  MISC("Misc"),
  FUN("Fun"),
  SETTINGS("Settings"),
  ADMIN("Admin");

  private final String name;

  Category(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
