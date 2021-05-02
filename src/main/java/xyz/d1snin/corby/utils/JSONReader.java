package xyz.d1snin.corby.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import xyz.d1snin.corby.CorbyException;
import xyz.d1snin.corby.manager.config.Config;

import java.io.*;
import java.net.URL;

public class JSONReader {

  public String readFromFile(String filename, String object) {
    try (BufferedReader reader =
        new BufferedReader(new FileReader(new File(filename).getAbsoluteFile()))) {

      JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);

      return (String) jsonObject.get(object);
    } catch (Exception e) {
      throw new CorbyException(
          "Error while trying to get object \"" + object + "\"",
          Config.ExitCodes.BAD_CONFIG_EXIT_CODE);
    }
  }

  public String readFromURL(String object, URL url, boolean isCatFact) {

    String result = "";

    if (!isCatFact) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {

        JSONArray jsonArray = (JSONArray) new JSONParser().parse(reader);
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        result = (String) jsonObject.get(object);

      } catch (IOException | ParseException e) {
        e.printStackTrace();
      }
    } else {
      do {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {

          JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
          result = (String) jsonObject.get(object);

        } catch (IOException | ParseException e) {
          e.printStackTrace();
        }
      } while (result.length() < 15);
    }
    return result;
  }
}
