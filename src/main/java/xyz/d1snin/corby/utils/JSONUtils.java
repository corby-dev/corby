package xyz.d1snin.corby.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class JSONUtils {
    public static String readJSON(String filename, String object) {
        try {

            JSONParser parser = new JSONParser();
            File tokenFile = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(tokenFile.getAbsoluteFile()));
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            return (String) jsonObject.get(object);

        } catch (IOException | ParseException e) {
            System.out.println("Incorrect " + filename);
            System.exit(0);
        }
        return null;
    }
}
