package xyz.d1snin.corby.manager;

import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.model.LaunchArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaunchArgumentsManager {

  private static final List<LaunchArgument> arguments = new ArrayList<>();

  public static void init(String[] args, LaunchArgument... argument) {
    arguments.addAll(Arrays.asList(argument));

    if (args.length < 1) {
      Corby.log.info("No arguments provided.");
      return;
    }

    for (String s : args) {
      LaunchArgument arg = getArgumentByName(s);

      if (arg == null) {
        throw new RuntimeException(String.format("Could not resolve this argument: %s", s));
      }

      Corby.log.info(String.format("Argument detected: %s", arg.getName()));
      arg.execute();
    }
  }

  private static LaunchArgument getArgumentByName(String name) {
    for (LaunchArgument argument : arguments) {
      if (("-" + argument.getName()).equals(name)) {
        return argument;
      }
    }
    return null;
  }
}
