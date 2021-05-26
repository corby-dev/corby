/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.database;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import lombok.Getter;
import org.jongo.Jongo;

import java.net.UnknownHostException;

public class DatabaseManager {

  @Getter private static DB db;

  @Getter private static Jongo jongo;

  public static void createConnection() throws UnknownHostException {
    db = new MongoClient("localhost", 27017).getDB("corby");
    jongo = new Jongo(db);
  }
}
