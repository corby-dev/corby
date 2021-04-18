package xyz.d1snin.corby.database;

import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.utils.logging.Logger;
import xyz.d1snin.corby.utils.logging.LoggingTypes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Connection connection;

    public static void createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:corby.db");
            Logger.log(LoggingTypes.DATABASE, "Successfully connected to the database.");
        } catch (SQLException | ClassNotFoundException e) {
            Logger.log(LoggingTypes.ERROR, "Something went wrong while trying to connect to the database. Stacktrace:");
            e.printStackTrace();
            System.exit(Corby.CANT_CONNECT_TO_THE_DATABASE);
        }
    }

    public static void close() {
        try {
            DatabasePreparedStatements.closeAllPreparedStatements();
            connection.close();
            Logger.log(LoggingTypes.DATABASE, "Successfully disconnected from the database.");
        } catch (SQLException e) {
            Logger.log(LoggingTypes.ERROR, "Something went wrong while trying to disconnect from the database. Stacktrace:");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
