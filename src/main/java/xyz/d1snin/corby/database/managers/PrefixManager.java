package xyz.d1snin.corby.database.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import net.dv8tion.jda.api.entities.Guild;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.DatabaseManager;

public class PrefixManager {
  private static final DBCollection collection =
      DatabaseManager.getDb().getCollection("guildprefix");

  public static void setPrefix(Guild guild, String prefix) {
    if (isDatabaseContainsPrefix(guild)) {
      collection.update(
          new BasicDBObject().append("guild", guild.getId()), createBasicDBObject(guild, prefix));
    } else {
      collection.insert(createBasicDBObject(guild, prefix));
    }
  }

  public static String getPrefix(Guild guild) {
    if (!isDatabaseContainsPrefix(guild)) {
      return Corby.config.botPrefixDefault;
    } else {
      return (String)
          collection.find(new BasicDBObject().append("guild", guild.getId())).next().get("prefix");
    }
  }

  private static boolean isDatabaseContainsPrefix(Guild guild) {
    return (collection.count(new BasicDBObject().append("guild", guild.getId())) > 0);
  }

  private static BasicDBObject createBasicDBObject(Guild guild, String prefix) {
    return new BasicDBObject().append("guild", guild.getId()).append("prefix", prefix);
  }
}
