package xyz.d1snin.corby.database.managers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.DatabasePreparedStatements;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StarboardManager {
  public static synchronized void setStarboardChannel(Guild guild, TextChannel channel)
      throws SQLException {
    if (!isDatabaseContainsStarboard(guild)) {

      DatabasePreparedStatements.psSetGuildStarboardChannelInsert.setLong(1, guild.getIdLong());
      DatabasePreparedStatements.psSetGuildStarboardChannelInsert.setLong(2, channel.getIdLong());
      DatabasePreparedStatements.psSetGuildStarboardChannelInsert.setInt(
          3, Corby.config.defaultStarboardStars);
      DatabasePreparedStatements.psSetGuildStarboardChannelInsert.setString(
          4, Corby.config.defaultStarboardIsEnabled ? "enabled" : "disabled");
      DatabasePreparedStatements.psSetGuildStarboardChannelInsert.executeUpdate();

    } else {

      DatabasePreparedStatements.psSetGuildStarboardChannelUpdate.setLong(1, channel.getIdLong());
      DatabasePreparedStatements.psSetGuildStarboardChannelUpdate.setLong(2, guild.getIdLong());
      DatabasePreparedStatements.psSetGuildStarboardChannelUpdate.executeUpdate();
    }
  }

  public static synchronized TextChannel getStarboardChannel(Guild guild) throws SQLException {
    DatabasePreparedStatements.psGetGuildStarboardChannel.setLong(1, guild.getIdLong());
    ResultSet rs = DatabasePreparedStatements.psGetGuildStarboardChannel.executeQuery();

    if (!rs.next()) {
      return null;
    }

    return (TextChannel) guild.getGuildChannelById(rs.getLong(1));
  }

  public static synchronized int getStarboardStars(Guild guild) throws SQLException {
    DatabasePreparedStatements.psGetGuildStarboardStars.setLong(1, guild.getIdLong());
    ResultSet rs = DatabasePreparedStatements.psGetGuildStarboardStars.executeQuery();

    if (!rs.next()) {
      return -1;
    }

    return rs.getInt(1);
  }

  public static synchronized void setStarboardStars(Guild guild, int stars)
      throws SQLException {
    DatabasePreparedStatements.psSetGuildStarboardStars.setInt(1, stars);
    DatabasePreparedStatements.psSetGuildStarboardStars.setLong(2, guild.getIdLong());
    DatabasePreparedStatements.psSetGuildStarboardStars.executeUpdate();
  }

  public static synchronized void setStarboardIsEnabled(Guild guild, boolean value)
      throws SQLException {
    DatabasePreparedStatements.psSetGuildStarboardIsEnabled.setString(
        1, value ? "enabled" : "disabled");
    DatabasePreparedStatements.psSetGuildStarboardIsEnabled.setLong(2, guild.getIdLong());
    DatabasePreparedStatements.psSetGuildStarboardIsEnabled.executeUpdate();
  }

  public static synchronized boolean getStarboardIsEnabled(Guild guild) throws SQLException {
    DatabasePreparedStatements.psGetGuildStarboardIsEnabled.setLong(1, guild.getIdLong());
    ResultSet rs = DatabasePreparedStatements.psGetGuildStarboardIsEnabled.executeQuery();

    if (!rs.next()) {
      return false;
    }

    return rs.getString(1).equals("enabled");
  }

  private static synchronized boolean isDatabaseContainsStarboard(Guild guild) throws SQLException {
    DatabasePreparedStatements.psCheckGuildStarboardChannelExists.setLong(1, guild.getIdLong());
    try (ResultSet rs =
        DatabasePreparedStatements.psCheckGuildStarboardChannelExists.executeQuery()) {
      return rs.next();
    }
  }
}
