package xyz.d1snin.corby.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LaunchArgument {

  private final String name;
  private final Runnable onArgument;

  public void execute() {
    onArgument.run();
  }
}
