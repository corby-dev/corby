package xyz.d1snin.corby.manager.config;

import xyz.d1snin.corby.Corby;

import java.io.File;
import java.io.IOException;

public class ConfigFileManager {
    public static void initConfigFile() {
        try {
            File file = new File("config.json");
            if (file.createNewFile()) {
                Corby.logger.warn("File created: " + file.getName() + ", please edit the generated file using the bot token.");
                System.exit(0);
            } else {
                Corby.logger.info("Founded config.json");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
