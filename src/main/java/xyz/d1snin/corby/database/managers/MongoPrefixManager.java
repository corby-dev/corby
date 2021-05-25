/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.database.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import net.dv8tion.jda.api.entities.Guild;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.DatabaseManager;
import xyz.d1snin.corby.model.Prefix;

public class MongoPrefixManager {
  private static final DBCollection collection =
      DatabaseManager.getDb().getCollection("guildprefix");

  public static void writePrefix(Prefix prefix) {
    if (isDatabaseContainsPrefix(prefix.getGuild())) {
      collection.update(
          new BasicDBObject().append("guild", prefix.getGuild().getId()), toDbObject(prefix));
    } else {
      collection.insert(toDbObject(prefix));
    }
  }

  public static Prefix getPrefix(Guild guild) {
    DBCursor prefixCursor = collection.find(new BasicDBObject().append("guild", guild.getId()));
    if (!prefixCursor.hasNext()) {
      return new Prefix(guild, Corby.config.getBotPrefixDefault());
    }
    DBObject prefix = prefixCursor.next();
    return new Prefix(
        Corby.getApi().getGuildById(prefix.get("guild").toString()),
        prefix.get("prefix").toString());
  }

  private static boolean isDatabaseContainsPrefix(Guild guild) {
    return collection.count(new BasicDBObject().append("guild", guild.getId())) > 0;
  }

  private static DBObject toDbObject(Object object) {
    Prefix prefix = (Prefix) object;
    return new BasicDBObject()
        .append("guild", prefix.getGuild())
        .append("prefix", prefix.getPrefix());
  }
}
