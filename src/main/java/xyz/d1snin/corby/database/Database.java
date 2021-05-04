/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.database;

import xyz.d1snin.corby.Corby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

  private static Connection connection;

  public static void createConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/corby.db");
    Corby.logger.info("Successfully connected to the database.");
  }

  public static void close() throws SQLException {
    DatabasePreparedStatements.closeAllPreparedStatements();
    connection.close();
    Corby.logger.warn("Successfully disconnected from the database.");
  }

  public static Connection getConnection() {
    return connection;
  }
}
