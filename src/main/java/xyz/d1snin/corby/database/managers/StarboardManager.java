package xyz.d1snin.corby.database.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.DatabaseManager;

import java.util.Objects;

public class StarboardManager {

  private static final DBCollection collection =
      DatabaseManager.getDb().getCollection("starboards");

  public static boolean isConfigured(Guild guild) {
    return isDatabaseContainsStarboard(guild);
  }

  public static void setChannel(Guild guild, TextChannel channel) {
    if (isDatabaseContainsStarboard(guild)) {
      collection.update(
          new BasicDBObject().append("guild", guild.getId()),
          createBasicDBObject(guild, channel, getStars(guild), getStatus(guild)));
    } else {
      collection.insert(
          createBasicDBObject(
              guild,
              channel,
              Corby.config.getDefaultStarboardStars(),
              Corby.config.isDefaultStarboardStatus()));
    }
  }

  public static TextChannel getChannel(Guild guild) {
    return (TextChannel)
        Corby.getApi()
            .getGuildChannelById(
                (String)
                    collection
                        .find(new BasicDBObject().append("guild", guild.getId()))
                        .next()
                        .get("channel"));
  }

  public static void setStars(int count, Guild guild) {
    collection.update(
        new BasicDBObject().append("guild", guild.getId()),
        createBasicDBObject(
            guild, Objects.requireNonNull(getChannel(guild)), count, getStatus(guild)));
  }

  public static int getStars(Guild guild) {
    return (int)
        collection.find(new BasicDBObject().append("guild", guild.getId())).next().get("stars");
  }

  public static void setStatus(Guild guild, boolean value) {
    collection.update(
        new BasicDBObject().append("guild", guild.getId()),
        createBasicDBObject(
            guild, Objects.requireNonNull(getChannel(guild)), getStars(guild), value));
  }

  public static boolean getStatus(Guild guild) {
    return (boolean)
        collection.find(new BasicDBObject().append("guild", guild.getId())).next().get("status");
  }

  private static BasicDBObject createBasicDBObject(
      Guild guild, TextChannel channel, int stars, boolean status) {
    return new BasicDBObject()
        .append("guild", guild.getId())
        .append("channel", channel.getId())
        .append("stars", stars)
        .append("status", status);
  }

  private static boolean isDatabaseContainsStarboard(Guild guild) {
    return (collection.count(new BasicDBObject().append("guild", guild.getId())) > 0);
  }
}
