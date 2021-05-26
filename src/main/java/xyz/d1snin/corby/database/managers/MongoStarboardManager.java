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
import xyz.d1snin.corby.model.database.Starboard;

public class MongoStarboardManager {

  private static final DBCollection collection =
      DatabaseManager.getDb().getCollection("starboards");

  public static void writeStarboard(Starboard starboard) {
    if (getStarboard(starboard.getGuild()) != null) {
      collection.update(
          new BasicDBObject().append("guild", starboard.getGuild().getId()),
          starboard.toDBObject());
    } else {
      collection.insert(
          new Starboard(
                  starboard.getGuild(),
                  starboard.getChannel(),
                  Corby.config.getDefaultStarboardStars(),
                  Corby.config.isDefaultStarboardStatus())
              .toDBObject());
    }
  }

  public static Starboard getStarboard(Guild guild) {
    DBCursor starboardCursor = collection.find(new BasicDBObject().append("guild", guild.getId()));
    if (!starboardCursor.hasNext()) {
      return null;
    }
    DBObject starboard = starboardCursor.next();

    return (Starboard) new Starboard().fromDBObject(starboard);
  }
}
