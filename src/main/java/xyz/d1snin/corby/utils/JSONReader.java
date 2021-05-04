/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;

public class JSONReader {

  public String readFromURL(String object, URL url, boolean isCatFact)
      throws IOException, ParseException {

    String result;

    if (!isCatFact) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {

        JSONArray jsonArray = (JSONArray) new JSONParser().parse(reader);
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        return (String) jsonObject.get(object);
      }
    } else {
      do {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {

          JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
          result = (String) jsonObject.get(object);
        }
      } while (result.length() < 15);
    }
    return result;
  }
}
