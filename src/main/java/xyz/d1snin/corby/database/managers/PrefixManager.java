/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.database.managers;

import net.dv8tion.jda.api.entities.Guild;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.DatabasePreparedStatements;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PrefixManager {

  public static synchronized void setPrefix(Guild guild, String prefix) throws SQLException {
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

  public static synchronized String getPrefix(Guild guild) throws SQLException {
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
}
