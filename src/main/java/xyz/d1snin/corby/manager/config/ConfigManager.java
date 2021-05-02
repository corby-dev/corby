package xyz.d1snin.corby.manager.config;

import xyz.d1snin.corby.utils.JSONReader;

import java.awt.*;

public class ConfigManager {

  public static Config init() {

    final String file = "config.json";
    JSONReader reader = new JSONReader();

    return new Config(
        reader.readFromFile(file, "token"),
        reader.readFromFile(file, "botPrefixDefault"),
        reader.readFromFile(file, "ownerId"),
        Integer.parseInt(reader.readFromFile(file, "defaultCooldownSeconds")),
        new Color(
            Integer.parseInt(reader.readFromFile(file, "defaultColor").split("\\s")[0]),
            Integer.parseInt(reader.readFromFile(file, "defaultColor").split("\\s")[1]),
            Integer.parseInt(reader.readFromFile(file, "defaultColor").split("\\s")[2])),
        new Color(
            Integer.parseInt(reader.readFromFile(file, "errorColor").split("\\s")[0]),
            Integer.parseInt(reader.readFromFile(file, "errorColor").split("\\s")[1]),
            Integer.parseInt(reader.readFromFile(file, "errorColor").split("\\s")[2])),
        new Color(
            Integer.parseInt(reader.readFromFile(file, "successColor").split("\\s")[0]),
            Integer.parseInt(reader.readFromFile(file, "successColor").split("\\s")[1]),
            Integer.parseInt(reader.readFromFile(file, "successColor").split("\\s")[2])),
        new Color(
            Integer.parseInt(reader.readFromFile(file, "starboardColor").split("\\s")[0]),
            Integer.parseInt(reader.readFromFile(file, "starboardColor").split("\\s")[1]),
            Integer.parseInt(reader.readFromFile(file, "starboardColor").split("\\s")[2])),
        reader.readFromFile(file, "emoteTrash"),
        reader.readFromFile(file, "emoteStar"),
        reader.readFromFile(file, "emoteWhiteCheckMark"),
        Integer.parseInt(reader.readFromFile(file, "defaultStarboardStars")),
        Boolean.parseBoolean(reader.readFromFile(file, "defaultStarboardIsEnabled")),
        reader.readFromFile(file, "helpPageUrl"));
  }
}
