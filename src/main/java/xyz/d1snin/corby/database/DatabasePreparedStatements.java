/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.database;

import xyz.d1snin.corby.Corby;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabasePreparedStatements {

  private static final List<PreparedStatement> preparedStatements = new ArrayList<>();

  private static final Connection connection = Database.getConnection();

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

  public static void loadAllPreparedStatements() throws SQLException {
    psSetGuildPrefixInsert =
        addPs(
            connection.prepareStatement(
                "INSERT INTO guildprefix (guildid, prefix) VALUES (?, ?);"));
    psSetGuildPrefixUpdate =
        addPs(connection.prepareStatement("UPDATE guildprefix SET prefix = ? WHERE guildid = ?;"));
    psGetGuildPrefix =
        addPs(connection.prepareStatement("SELECT prefix FROM guildprefix WHERE guildid = ?;"));
    psCheckGuildPrefixExists =
        addPs(connection.prepareStatement("SELECT 1 FROM guildprefix WHERE guildid = ?;"));

    psSetGuildStarboardChannelInsert =
        addPs(
            connection.prepareStatement(
                "INSERT INTO starboards (guildid, channelid, stars, isenabled) VALUES (?, ?, ?, ?);"));
    psSetGuildStarboardChannelUpdate =
        addPs(
            connection.prepareStatement("UPDATE starboards SET channelid = ? WHERE guildid = ?;"));
    psGetGuildStarboardChannel =
        addPs(connection.prepareStatement("SELECT channelid FROM starboards WHERE guildid = ?;"));
    psCheckGuildStarboardChannelExists =
        addPs(connection.prepareStatement("SELECT 1 FROM starboards WHERE guildid = ?;"));
    psGetGuildStarboardStars =
        addPs(connection.prepareStatement("SELECT stars FROM starboards WHERE guildid = ?;"));
    psSetGuildStarboardStars =
        addPs(connection.prepareStatement("UPDATE starboards SET stars = ? WHERE guildid = ?;"));
    psGetGuildStarboardIsEnabled =
        addPs(connection.prepareStatement("SELECT isenabled FROM starboards WHERE guildid = ?;"));
    psSetGuildStarboardIsEnabled =
        addPs(
            connection.prepareStatement("UPDATE starboards SET isenabled = ? WHERE guildid = ?;"));
    Corby.logger.info("All prepared statements was loaded.");
  }

  public static void closeAllPreparedStatements() throws SQLException {
    for (PreparedStatement statement : preparedStatements) {
      statement.close();
    }
    Corby.logger.warn("Successfully closed all prepared statements.");
  }

  private static PreparedStatement addPs(PreparedStatement statement) {
    preparedStatements.add(statement);
    return statement;
  }
}
