/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.manager.config;

import com.google.gson.Gson;
import xyz.d1snin.corby.model.Config;

import java.io.FileReader;
import java.io.IOException;

public class ConfigManager {
  public static Config init() throws IOException {
    return new Gson().fromJson(new FileReader("config.json"), Config.class);
  }
}
