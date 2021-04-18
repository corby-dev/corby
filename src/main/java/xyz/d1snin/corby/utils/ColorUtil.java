package xyz.d1snin.corby.utils;

import java.awt.*;
import java.util.Random;

public class ColorUtil {

    private static Random random = new Random();

    public static Color getRandomColorRGB() {
        return new Color(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
    }

    public static Color getRandomColorHSB() {
        return Color.getHSBColor(
                random.nextInt(361),
                random.nextInt(101),
                random.nextInt(101)
        );
    }

    public static Color getDefaultColor() {
        return new Color(74, 129, 248);
    }

    public static Color getErrorColor() {
        return Color.RED;
    }
}
