package xyz.d1snin.corby.manager.config;

import xyz.d1snin.corby.utils.JSONReader;

import java.awt.*;

public class ConfigManager {

    public static Config init() {

        JSONReader reader = new JSONReader("config.json");

        return new Config(
                reader.read("token"),
                reader.read("bot_prefix_default"),
                reader.read("bot_name"),
                reader.read("owner_id"),
                Integer.parseInt(reader.read("default_cooldown_seconds")),
                new Color(
                        Integer.parseInt(reader.read("default_color").split("\\s")[0]),
                        Integer.parseInt(reader.read("default_color").split("\\s")[1]),
                        Integer.parseInt(reader.read("default_color").split("\\s")[2])
                ),
                new Color(
                        Integer.parseInt(reader.read("error_color").split("\\s")[0]),
                        Integer.parseInt(reader.read("error_color").split("\\s")[1]),
                        Integer.parseInt(reader.read("error_color").split("\\s")[2])
                ),
                new Color(
                        Integer.parseInt(reader.read("starboard_color").split("\\s")[0]),
                        Integer.parseInt(reader.read("starboard_color").split("\\s")[1]),
                        Integer.parseInt(reader.read("starboard_color").split("\\s")[2])
                ),
                reader.read("emote_trash"),
                reader.read("emote_star"),
                Integer.parseInt(reader.read("default_starboard_stars")),
                Boolean.parseBoolean(reader.read("default_starboard_isenabled"))
        );
    }
}
