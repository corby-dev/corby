package xyz.d1snin.corby.database;

import xyz.d1snin.corby.Corby;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Connection connection;

    public static void createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:corby.db");
            Corby.logger.info("Successfully connected to the database.");
        } catch (SQLException | ClassNotFoundException e) {
            Corby.logger.error("Something went wrong while trying to connect to the database. Stacktrace:");
            e.printStackTrace();
            System.exit(Corby.CANT_CONNECT_TO_THE_DATABASE);
        }
    }

    public static void close() {
        try {
            DatabasePreparedStatements.closeAllPreparedStatements();
            connection.close();
            Corby.logger.warn("Successfully disconnected from the database.");
        } catch (SQLException e) {
            Corby.logger.error("Something went wrong while trying to disconnect from the database. Stacktrace:");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
