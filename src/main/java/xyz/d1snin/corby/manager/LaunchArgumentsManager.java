package xyz.d1snin.corby.manager;

import xyz.d1snin.corby.model.LaunchArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LaunchArgumentsManager {

  private static final List<LaunchArgument> arguments = new ArrayList<>();

  public static void addArguments(LaunchArgument... argument) {
    arguments.addAll(Arrays.asList(argument));
  }

  public static void init(String[] args) {
    for (String s : args) {
      LaunchArgument arg = getArgumentByName(s);

      if (arg == null) {
        throw new RuntimeException(String.format("Could not resolve this argument: %s", s));
      }

      arg.setValue(true);
    }
  }

  public static boolean getValueByArgName(String name) {
    LaunchArgument argument = getArgumentByName(name);
    return argument != null && argument.getValue();
  }

  private static LaunchArgument getArgumentByName(String name) {
    for (LaunchArgument argument : arguments) {
      if (argument.getName().equals("-" + name)) {
        return argument;
      }
    }
    return null;
  }
}