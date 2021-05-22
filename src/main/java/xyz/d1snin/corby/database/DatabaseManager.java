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
import xyz.d1snin.corby.Corby;

import java.net.UnknownHostException;

public class DatabaseManager {

  private static DB db;

  public static void createConnection() throws UnknownHostException {
    db =
        new MongoClient(Corby.config.getMongoHostname(), Corby.config.getMongoPort())
            .getDB(Corby.config.getMongoDbName());
    db.authenticate(Corby.config.getMongoUser(), Corby.config.getMongoPassword().toCharArray());
  }

  public static DB getDb() {
    return db;
  }
}
