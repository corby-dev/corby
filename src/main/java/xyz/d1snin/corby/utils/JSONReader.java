package xyz.d1snin.corby.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import xyz.d1snin.corby.CorbyException;
import xyz.d1snin.corby.manager.config.Config;

import java.io.*;

public class JSONReader {

    private String filename;

    public JSONReader(String filename) {
        this.filename = filename;
    }
    public String read(String object) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filename).getAbsoluteFile()))) {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
            return (String) jsonObject.get(object);
        } catch (Exception e) {
            throw new CorbyException("Error while trying to get object \"" + object + "\"", Config.ExitCodes.BAD_CONFIG_EXIT_CODE);
        }
    }
}
