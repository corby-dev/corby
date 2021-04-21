package xyz.d1snin.corby.database;

import xyz.d1snin.corby.Corby;

import java.sql.*;

public class DatabasePreparedStatements {

    public static PreparedStatement psSetGuildPrefixInsert;
    public static PreparedStatement psSetGuildPrefixUpdate;
    public static PreparedStatement psGetGuildPrefix;
    public static PreparedStatement psCheckGuildPrefixContains;

    public static void loadAllPreparedStatements() {
        Connection connection = Database.getConnection();
        try {
            psSetGuildPrefixInsert = connection.prepareStatement("INSERT INTO guildprefix (guildid, prefix) VALUES (?, ?);");
            psSetGuildPrefixUpdate = connection.prepareStatement("UPDATE guildprefix SET prefix = ? WHERE guildid = ?;");
            psGetGuildPrefix = connection.prepareStatement("SELECT prefix FROM guildprefix WHERE guildid = ?;");
            psCheckGuildPrefixContains = connection.prepareStatement("SELECT 1 FROM guildprefix WHERE guildid = ?;");
        } catch (SQLException e) {
            Corby.logger.error("Error while trying to load all prepared statements of the database. Stacktrace:");
            e.printStackTrace();
        }
    }

    public static void closeAllPreparedStatements() {
        try {
            psSetGuildPrefixInsert.close();
            psSetGuildPrefixUpdate.close();
            psGetGuildPrefix.close();
            psCheckGuildPrefixContains.close();
            Corby.logger.warn("Successfully closed all prepared statements.");
        } catch (SQLException e) {
            Corby.logger.error("Something went wrong while trying to close all prepared statements. Stacktrace:");
            e.printStackTrace();
        }
    }

    public static void printSQLError(SQLException e) {
        Corby.logger.error("Error while trying to execute prepared statement. Stacktrace:");
        e.printStackTrace();
    }
}
