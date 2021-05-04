/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.database.managers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.DatabasePreparedStatements;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildSettingsManager {

  public static synchronized void setGuildPrefix(Guild guild, String prefix) throws SQLException {
    if (!isDatabaseContainsPrefix(guild)) {

      DatabasePreparedStatements.psSetGuildPrefixInsert.setLong(1, guild.getIdLong());
      DatabasePreparedStatements.psSetGuildPrefixInsert.setString(2, prefix);
      DatabasePreparedStatements.psSetGuildPrefixInsert.executeUpdate();

    } else {

      DatabasePreparedStatements.psSetGuildPrefixUpdate.setString(1, prefix);
      DatabasePreparedStatements.psSetGuildPrefixUpdate.setLong(2, guild.getIdLong());
      DatabasePreparedStatements.psSetGuildPrefixUpdate.executeUpdate();
    }
  }

  public static synchronized String getGuildPrefix(Guild guild) throws SQLException {
    DatabasePreparedStatements.psGetGuildPrefix.setLong(1, guild.getIdLong());
    ResultSet rs = DatabasePreparedStatements.psGetGuildPrefix.executeQuery();

    if (!rs.next()) {
      return Corby.config.botPrefixDefault;
    }

    return rs.getString(1);
  }

  private static synchronized boolean isDatabaseContainsPrefix(Guild guild) throws SQLException {
    DatabasePreparedStatements.psCheckGuildPrefixExists.setLong(1, guild.getIdLong());
    try (ResultSet rs = DatabasePreparedStatements.psGetGuildPrefix.executeQuery()) {
      return rs.next();
    }
  }

  public static synchronized void setGuildStarboardChannel(Guild guild, TextChannel channel)
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

  public static synchronized TextChannel getGuildStarboardChannel(Guild guild) throws SQLException {
    DatabasePreparedStatements.psGetGuildStarboardChannel.setLong(1, guild.getIdLong());
    ResultSet rs = DatabasePreparedStatements.psGetGuildStarboardChannel.executeQuery();

    if (!rs.next()) {
      return null;
    }

    return (TextChannel) guild.getGuildChannelById(rs.getLong(1));
  }

  public static synchronized int getGuildStarboardStars(Guild guild) throws SQLException {
    DatabasePreparedStatements.psGetGuildStarboardStars.setLong(1, guild.getIdLong());
    ResultSet rs = DatabasePreparedStatements.psGetGuildStarboardStars.executeQuery();

    if (!rs.next()) {
      return -1;
    }

    return rs.getInt(1);
  }

  public static synchronized void setGuildStarboardStars(Guild guild, int stars)
      throws SQLException {
    DatabasePreparedStatements.psSetGuildStarboardStars.setInt(1, stars);
    DatabasePreparedStatements.psSetGuildStarboardStars.setLong(2, guild.getIdLong());
    DatabasePreparedStatements.psSetGuildStarboardStars.executeUpdate();
  }

  public static synchronized void setGuildStarboardIsEnabled(Guild guild, boolean value)
      throws SQLException {
    DatabasePreparedStatements.psSetGuildStarboardIsEnabled.setString(
        1, value ? "enabled" : "disabled");
    DatabasePreparedStatements.psSetGuildStarboardIsEnabled.setLong(2, guild.getIdLong());
    DatabasePreparedStatements.psSetGuildStarboardIsEnabled.executeUpdate();
  }

  public static synchronized boolean getGuildStarboardIsEnabled(Guild guild) throws SQLException {
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
