/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.manager.config;

import xyz.d1snin.corby.Corby;

import java.io.File;
import java.io.IOException;

public class ConfigFileManager {
  public static void initConfigFile() throws IOException {
    File file = new File("config.json");
    if (file.createNewFile()) {
      Corby.log.warn("File created: " + file.getName() + ", please edit the generated file.");
      System.exit(0);
    } else {
      Corby.log.info("Found config.json");
    }
  }
}
