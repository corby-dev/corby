package xyz.d1snin.corby.database;

import xyz.d1snin.corby.utils.logging.Logger;
import xyz.d1snin.corby.utils.logging.LoggingTypes;

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
            Logger.log(LoggingTypes.ERROR, "Error while trying to load all prepared statements of the database. Stacktrace:");
            e.printStackTrace();
        }
    }

    public static void closeAllPreparedStatements() {
        try {
            psSetGuildPrefixInsert.close();
            psSetGuildPrefixUpdate.close();
            psGetGuildPrefix.close();
            psCheckGuildPrefixContains.close();
            Logger.log(LoggingTypes.DATABASE, "Successfully closed all prepared statements.");
        } catch (SQLException e) {
            Logger.log(LoggingTypes.ERROR, "Something went wrong while trying to close all prepared statements. Stacktrace:");
            e.printStackTrace();
        }
    }

    public static void printSQLError(SQLException e) {
        Logger.log(LoggingTypes.ERROR, "Error while trying to execute prepared statement. Stacktrace:");
        e.printStackTrace();
    }
}
