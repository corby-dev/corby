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

public class Database {

  private static MongoClient mongo;
  private static DB db;

  public static void createConnection() throws UnknownHostException {
    mongo = new MongoClient(Corby.config.mongoHostname, Corby.config.mongoPort);
    db = mongo.getDB(Corby.config.mongoDbName);
    db.authenticate(Corby.config.mongoUser, Corby.config.mongoPassword.toCharArray());
  }

  public static DB getDb() {
    return db;
  }

  public static void close() {
    mongo.close();
    Corby.logger.warn("Successfully disconnected from the database.");
  }
}
