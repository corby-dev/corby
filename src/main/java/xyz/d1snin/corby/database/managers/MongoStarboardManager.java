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
import xyz.d1snin.corby.model.database.Starboard;

import java.util.Objects;

public class MongoStarboardManager {

  private static final MongoCollection collection =
      DatabaseManager.getJongo().getCollection("starboards");

  public static Starboard getStarboard(Guild guild) {
    return collection.findOne(String.format("{guild: '%s'}", guild.getId())).as(Starboard.class);
  }

  public static void writeStarboard(Starboard starboard) {
    if (getStarboard(Objects.requireNonNull(Corby.getApi().getGuildById(starboard.getGuild())))
        != null) {
      collection.update(String.format("{guild: '%s'}", starboard.getGuild())).with(starboard);
    } else {
      starboard.setStatus(Corby.config.isDefaultStarboardStatus());
      starboard.setStars(Corby.config.getDefaultStarboardStars());

      collection.insert(starboard);
    }
  }
}
