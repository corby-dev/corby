package xyz.d1snin.corby.manager.config;

import xyz.d1snin.corby.utils.JSONReader;

import java.awt.*;

public class ConfigManager {

    public static Config init() {

        final String file = "config.json";
        JSONReader reader = new JSONReader();

        return new Config(
                reader.readFromFile(file, "token"),
                reader.readFromFile(file, "bot_prefix_default"),
                reader.readFromFile(file, "owner_id"),
                Integer.parseInt(reader.readFromFile(file, "default_cooldown_seconds")),
                new Color(
                        Integer.parseInt(reader.readFromFile(file, "default_color").split("\\s")[0]),
                        Integer.parseInt(reader.readFromFile(file, "default_color").split("\\s")[1]),
                        Integer.parseInt(reader.readFromFile(file, "default_color").split("\\s")[2])
                ),
                new Color(
                        Integer.parseInt(reader.readFromFile(file, "error_color").split("\\s")[0]),
                        Integer.parseInt(reader.readFromFile(file, "error_color").split("\\s")[1]),
                        Integer.parseInt(reader.readFromFile(file, "error_color").split("\\s")[2])
                ),
                new Color(
                        Integer.parseInt(reader.readFromFile(file, "success_color").split("\\s")[0]),
                        Integer.parseInt(reader.readFromFile(file, "success_color").split("\\s")[1]),
                        Integer.parseInt(reader.readFromFile(file, "success_color").split("\\s")[2])
                ),
                new Color(
                        Integer.parseInt(reader.readFromFile(file, "starboard_color").split("\\s")[0]),
                        Integer.parseInt(reader.readFromFile(file, "starboard_color").split("\\s")[1]),
                        Integer.parseInt(reader.readFromFile(file, "starboard_color").split("\\s")[2])
                ),
                reader.readFromFile(file, "emote_trash"),
                reader.readFromFile(file, "emote_star"),
                reader.readFromFile(file, "emote_white_check_mark"),
                Integer.parseInt(reader.readFromFile(file, "default_starboard_stars")),
                Boolean.parseBoolean(reader.readFromFile(file, "default_starboard_isenabled")),
                reader.readFromFile(file, "help_page_url")
        );
    }
}
