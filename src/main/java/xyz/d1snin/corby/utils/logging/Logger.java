package xyz.d1snin.corby.utils.logging;

import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.utils.TimeUtils;

public class Logger {
    public static void log(LoggingTypes loggingTypes, String msg) {
        String type = "";
        switch (loggingTypes) {
            case INFO: type = "INFO"; break;
            case ERROR: type = "ERROR"; break;
            case WARNING: type = "WARNING"; break;
            case DATABASE: type = "DATABASE"; break;
            case LOADER: type = "LOADER"; break;
        }
        System.out.println("[" + TimeUtils.getCurrentTime() + "][" + Corby.BOT_NAME + "][" + type + "] " + msg);
    }
}
