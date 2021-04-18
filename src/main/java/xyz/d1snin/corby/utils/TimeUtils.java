package xyz.d1snin.corby.utils;

import java.time.LocalTime;

public class TimeUtils {
    public static String getCurrentTime() {
        return LocalTime.now().toString().substring(0, 8);
    }
}
