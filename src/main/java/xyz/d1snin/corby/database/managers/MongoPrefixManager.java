/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.database.managers;

import net.dv8tion.jda.api.entities.Guild;
import org.jongo.MongoCollection;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.DatabaseManager;
import xyz.d1snin.corby.model.Prefix;

public class MongoPrefixManager {
  private static final MongoCollection collection =
      DatabaseManager.getJongo().getCollection("guildprefix");

  public static Prefix getPrefix(Guild guild) {
    return isDatabaseContainsPrefix(guild.getId())
        ? collection.findOne(String.format("{guild: '%s'}", guild.getId())).as(Prefix.class)
        : new Prefix(guild.getId(), Corby.getConfig().getBotPrefixDefault());
  }

  public static void writePrefix(Prefix prefix) {
    if (isDatabaseContainsPrefix(prefix.getGuild())) {
      collection.update(String.format("{guild: '%s'}", prefix.getGuild())).with(prefix);
    } else {
      collection.insert(prefix);
    }
  }

  private static boolean isDatabaseContainsPrefix(String guildId) {
    return collection.count(String.format("{guild: '%s'}", guildId)) > 0;
  }
}
