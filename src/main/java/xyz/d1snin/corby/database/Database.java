package xyz.d1snin.corby.database;

import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.manager.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

  private static Connection connection;

  public static void createConnection() {
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/corby.db");
      Corby.logger.info("Successfully connected to the database.");
    } catch (Exception e) {
      Corby.logger.error(
          "Something went wrong while trying to connect to the database. Stacktrace:");
      e.printStackTrace();
      System.exit(Config.ExitCodes.CANT_CONNECT_TO_THE_DATABASE_EXIT_CODE);
    }
  }

  public static void close() {
    try {
      DatabasePreparedStatements.closeAllPreparedStatements();
      connection.close();
      Corby.logger.warn("Successfully disconnected from the database.");
    } catch (Exception e) {
      Corby.logger.error(
          "Something went wrong while trying to disconnect from the database. Stacktrace:");
      e.printStackTrace();
    }
  }

  public static Connection getConnection() {
    return connection;
  }
}
