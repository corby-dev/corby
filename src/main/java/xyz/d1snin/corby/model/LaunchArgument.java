package xyz.d1snin.corby.model;

public class LaunchArgument {

  private final String name;
  private boolean value = false;

  public LaunchArgument(String name) {
    this.name = "-" + name;
  }

  public String getName() {
    return name;
  }

  public boolean getValue() {
    return value;
  }

  public void setValue(boolean value) {
    this.value = value;
  }
}
