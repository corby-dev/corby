package xyz.d1snin.corby.database.managers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.DatabasePreparedStatements;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildSettingsManager {

    public static synchronized void setGuildPrefix(Guild guild, String prefix) {
        try {
            if (!isDatabaseContainsPrefix(guild)) {
                DatabasePreparedStatements.psSetGuildPrefixInsert.setLong(1, guild.getIdLong());
                DatabasePreparedStatements.psSetGuildPrefixInsert.setString(2, prefix);
                DatabasePreparedStatements.psSetGuildPrefixInsert.executeUpdate();
            } else {
                DatabasePreparedStatements.psSetGuildPrefixUpdate.setString(1, prefix);
                DatabasePreparedStatements.psSetGuildPrefixUpdate.setLong(2, guild.getIdLong());
                DatabasePreparedStatements.psSetGuildPrefixUpdate.executeUpdate();
            }
        } catch (SQLException e) {
            DatabasePreparedStatements.printSQLError(e);
        }
    }

    public static synchronized String getGuildPrefix(Guild guild) {

        String result = "";

        try {
            DatabasePreparedStatements.psGetGuildPrefix.setLong(1, guild.getIdLong());
            ResultSet rs = DatabasePreparedStatements.psGetGuildPrefix.executeQuery();
            if (!rs.next()) {
                return Corby.config.bot_prefix_default;
            }
            result = rs.getString(1);
        } catch (SQLException e) {
            DatabasePreparedStatements.printSQLError(e);
        }
        return result;
    }

    private static synchronized boolean isDatabaseContainsPrefix(Guild guild) {
        try {

            DatabasePreparedStatements.psCheckGuildPrefixExists.setLong(1, guild.getIdLong());

            try (ResultSet rs = DatabasePreparedStatements.psGetGuildPrefix.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            DatabasePreparedStatements.printSQLError(e);
        }
        return false;
    }

    public static synchronized  void setGuildStarboardChannel(Guild guild, TextChannel channel) {
        try {
            if (!isDatabaseContainsStarboard(guild)) {
                DatabasePreparedStatements.psSetGuildStarboardChannelInsert.setLong(1, guild.getIdLong());
                DatabasePreparedStatements.psSetGuildStarboardChannelInsert.setLong(2, channel.getIdLong());
                DatabasePreparedStatements.psSetGuildStarboardChannelInsert.setInt(3, Corby.config.default_starboard_stars);
                DatabasePreparedStatements.psSetGuildStarboardChannelInsert.setString(4, Corby.config.default_starboard_isenabled ? "enabled" : "disabled");
                DatabasePreparedStatements.psSetGuildStarboardChannelInsert.executeUpdate();
            } else {
                DatabasePreparedStatements.psSetGuildStarboardChannelUpdate.setLong(1, channel.getIdLong());
                DatabasePreparedStatements.psSetGuildStarboardChannelUpdate.setLong(2, guild.getIdLong());
                DatabasePreparedStatements.psSetGuildStarboardChannelUpdate.executeUpdate();
            }
        } catch (SQLException e) {
            DatabasePreparedStatements.printSQLError(e);
        }
    }

    public static synchronized TextChannel getGuildStarboardChannel(Guild guild) {

        TextChannel result = null;

        try {
            DatabasePreparedStatements.psGetGuildStarboardChannel.setLong(1, guild.getIdLong());
            ResultSet rs = DatabasePreparedStatements.psGetGuildStarboardChannel.executeQuery();
            if (!rs.next()) {
                return null;
            }
            result = (TextChannel) guild.getGuildChannelById(rs.getLong(1));
        } catch (SQLException e) {
            DatabasePreparedStatements.printSQLError(e);
        }
        return result;
    }

    public static synchronized int getGuildStarboardStars(Guild guild) {
        int result = 0;

        try {
            DatabasePreparedStatements.psGetGuildStarboardStars.setLong(1, guild.getIdLong());
            ResultSet rs = DatabasePreparedStatements.psGetGuildStarboardStars.executeQuery();
            if (!rs.next()) {
                return -1;
            }
            result = rs.getInt(1);
        } catch (SQLException e) {
            DatabasePreparedStatements.printSQLError(e);
        }
        return result;
    }

    public static synchronized void setGuildStarboardStars(Guild guild, int stars) {
        try {
            DatabasePreparedStatements.psSetGuildStarboardStars.setInt(1, stars);
            DatabasePreparedStatements.psSetGuildStarboardStars.setLong(2, guild.getIdLong());
            DatabasePreparedStatements.psSetGuildStarboardStars.executeUpdate();
        } catch (SQLException e) {
            DatabasePreparedStatements.printSQLError(e);
        }
    }

    public static synchronized void setGuildStarboardIsEnabled(Guild guild, boolean value) {
         try {
             DatabasePreparedStatements.psSetGuildStarboardIsEnabled.setString(1, value ? "enabled" : "disabled");
             DatabasePreparedStatements.psSetGuildStarboardIsEnabled.setLong(2, guild.getIdLong());
             DatabasePreparedStatements.psSetGuildStarboardIsEnabled.executeUpdate();
         } catch (SQLException e) {
             DatabasePreparedStatements.printSQLError(e);
         }
    }

    public static synchronized boolean getGuildStarboardIsEnabled(Guild guild) {

        boolean result = false;

        try {
            DatabasePreparedStatements.psGetGuildStarboardIsEnabled.setLong(1, guild.getIdLong());
            ResultSet rs = DatabasePreparedStatements.psGetGuildStarboardIsEnabled.executeQuery();
            if (!rs.next()) {
                return false;
            }
            result = rs.getString(1).equals("enabled");
        } catch (SQLException e) {
            DatabasePreparedStatements.printSQLError(e);
        }
        return result;
    }

    private static synchronized boolean isDatabaseContainsStarboard(Guild guild) {
        try {

            DatabasePreparedStatements.psCheckGuildStarboardChannelExists.setLong(1, guild.getIdLong());

            try (ResultSet rs = DatabasePreparedStatements.psCheckGuildStarboardChannelExists.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            DatabasePreparedStatements.printSQLError(e);
        }
        return false;
    }
}
