package xyz.d1snin.corby.database.managers;

import net.dv8tion.jda.api.entities.Guild;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.DatabasePreparedStatements;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildSettingsManager implements Manager {
    public static void setGuildPrefix(Guild guild, String prefix) {
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

    public static String getGuildPrefix(Guild guild) {

        String result = "";

        try {
            DatabasePreparedStatements.psGetGuildPrefix.setLong(1, guild.getIdLong());
            ResultSet rs = DatabasePreparedStatements.psGetGuildPrefix.executeQuery();
            if (!rs.next()) {
                return Corby.BOT_PREFIX_DEFAULT;
            }
            result = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean isDatabaseContainsPrefix(Guild guild) {
        try {

            DatabasePreparedStatements.psCheckGuildPrefixContains.setLong(1, guild.getIdLong());

            try (ResultSet rs = DatabasePreparedStatements.psCheckGuildPrefixContains.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void startDatabaseCleaner() {

    }
}
