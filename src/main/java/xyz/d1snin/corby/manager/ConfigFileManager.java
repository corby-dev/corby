package xyz.d1snin.corby.manager;

import xyz.d1snin.corby.utils.logging.Logger;
import xyz.d1snin.corby.utils.logging.LoggingTypes;

import java.io.File;
import java.io.IOException;

public class ConfigFileManager {
    public static void createConfigFile(String fileName) {
        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                Logger.log(LoggingTypes.LOADER, "File created: " + file.getName() + ", please edit the generated file using the bot token.");
                System.exit(0);
            } else {
                Logger.log(LoggingTypes.LOADER, "Founded " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
