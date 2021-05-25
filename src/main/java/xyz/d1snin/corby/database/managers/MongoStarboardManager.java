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
import xyz.d1snin.corby.model.Starboard;

public class MongoStarboardManager {

  private static final DBCollection collection =
      DatabaseManager.getDb().getCollection("starboards");

  public static void writeStarboard(Starboard starboard) {
    if (getStarboard(starboard.getGuild()) != null) {
      collection.update(
          new BasicDBObject().append("guild", starboard.getGuild().getId()), toDbObject(starboard));
    } else {
      collection.insert(
          toDbObject(
              new Starboard(
                  starboard.getGuild(),
                  starboard.getChannel(),
                  Corby.config.getDefaultStarboardStars(),
                  Corby.config.isDefaultStarboardStatus())));
    }
  }

  public static Starboard getStarboard(Guild guild) {
    DBCursor starboardCursor = collection.find(new BasicDBObject().append("guild", guild.getId()));
    if (!starboardCursor.hasNext()) {
      return null;
    }
    DBObject starboard = starboardCursor.next();
    return new Starboard(
        Corby.getApi().getGuildById(starboard.get("guild").toString()),
        Corby.getApi().getTextChannelById(starboard.get("channel").toString()),
        (int) starboard.get("stars"),
        (boolean) starboard.get("status"));
  }

  private static DBObject toDbObject(Object object) {
    Starboard starboard = (Starboard) object;

    return new BasicDBObject()
        .append("guild", starboard.getGuild().getId())
        .append("channel", starboard.getChannel().getId())
        .append("stars", starboard.getStars())
        .append("status", starboard.isStatus());
  }
}
