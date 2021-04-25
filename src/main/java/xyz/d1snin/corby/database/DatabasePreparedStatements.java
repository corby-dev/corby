package xyz.d1snin.corby.database;

import xyz.d1snin.corby.Corby;

import java.sql.*;

public class DatabasePreparedStatements {

    public static PreparedStatement psSetGuildPrefixInsert;
    public static PreparedStatement psSetGuildPrefixUpdate;
    public static PreparedStatement psGetGuildPrefix;
    public static PreparedStatement psCheckGuildPrefixExists;

    public static PreparedStatement psSetGuildStarboardChannelInsert;
    public static PreparedStatement psSetGuildStarboardChannelUpdate;
    public static PreparedStatement psGetGuildStarboardChannel;
    public static PreparedStatement psCheckGuildStarboardChannelExists;
    public static PreparedStatement psGetGuildStarboardStars;
    public static PreparedStatement psSetGuildStarboardStars;
    public static PreparedStatement psGetGuildStarboardIsEnabled;
    public static PreparedStatement psSetGuildStarboardIsEnabled;

    public static void loadAllPreparedStatements() {
        Connection connection = Database.getConnection();
        try {
            psSetGuildPrefixInsert = connection.prepareStatement("INSERT INTO guildprefix (guildid, prefix) VALUES (?, ?);");
            psSetGuildPrefixUpdate = connection.prepareStatement("UPDATE guildprefix SET prefix = ? WHERE guildid = ?;");
            psGetGuildPrefix = connection.prepareStatement("SELECT prefix FROM guildprefix WHERE guildid = ?;");
            psCheckGuildPrefixExists = connection.prepareStatement("SELECT 1 FROM guildprefix WHERE guildid = ?;");

            psSetGuildStarboardChannelInsert = connection.prepareStatement("INSERT INTO starboards (guildid, channelid, stars, isenabled) VALUES (?, ?, ?, ?);");
            psSetGuildStarboardChannelUpdate = connection.prepareStatement("UPDATE starboards SET channelid = ? WHERE guildid = ?;");
            psGetGuildStarboardChannel = connection.prepareStatement("SELECT channelid FROM starboards WHERE guildid = ?;");
            psCheckGuildStarboardChannelExists = connection.prepareStatement("SELECT 1 FROM starboards WHERE guildid = ?;");
            psGetGuildStarboardStars = connection.prepareStatement("SELECT stars FROM starboards WHERE guildid = ?;");
            psSetGuildStarboardStars = connection.prepareStatement("UPDATE starboards SET stars = ? WHERE guildid = ?;");
            psGetGuildStarboardIsEnabled = connection.prepareStatement("SELECT isenabled FROM starboards WHERE guildid = ?;");
            psSetGuildStarboardIsEnabled = connection.prepareStatement("UPDATE starboards SET isenabled = ? WHERE guildid = ?;");
            Corby.logger.info("All prepared statements was loaded.");
        } catch (Exception e) {
            Corby.logger.error("Error while trying to load all prepared statements of the database. Stacktrace:");
            e.printStackTrace();
        }
    }

    public static void closeAllPreparedStatements() {
        try {
            psSetGuildPrefixInsert.close();
            psSetGuildPrefixUpdate.close();
            psGetGuildPrefix.close();
            Corby.logger.warn("Successfully closed all prepared statements.");
        } catch (Exception e) {
            Corby.logger.error("Something went wrong while trying to close all prepared statements. Stacktrace:");
            e.printStackTrace();
        }
    }

    public static void printSQLError(SQLException e) {
        Corby.logger.error("Error while trying to execute prepared statement. Stacktrace:");
        e.printStackTrace();
    }
}
