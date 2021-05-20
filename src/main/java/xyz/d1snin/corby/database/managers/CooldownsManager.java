package xyz.d1snin.corby.database.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import net.dv8tion.jda.api.entities.User;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.DatabaseManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CooldownsManager {
  private static final DBCollection collection = DatabaseManager.getDb().getCollection("cooldowns");

  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor();

  public static void setCooldown(User user, Command command, int cooldown) {
    if (isDatabaseContainsCooldown(user, command)) {
      collection.update(
          new BasicDBObject().append("user", user.getId()),
          createBasicDBObject(user, command, cooldown));
    } else {
      collection.insert(createBasicDBObject(user, command, command.getCooldown()));
    }
  }

  public static int getCooldown(User u, Command command) {
    if (isDatabaseContainsCooldown(u, command)) {
      return (int)
          collection
              .find(
                  new BasicDBObject()
                      .append("user", u.getId())
                      .append("command", command.getAlias()))
              .next()
              .get("cooldown");
    }
    return 0;
  }

  public static void startUpdating() {
    executor.scheduleWithFixedDelay(
        () -> {
          DBCursor cursor = collection.find();
          while (cursor.hasNext()) {
            BasicDBObject obj = (BasicDBObject) cursor.next();
            int cooldown = (int) obj.get("cooldown");
            collection.remove(obj);
            if (cooldown != 0) {
              collection.insert(obj.append("cooldown", cooldown - 1));
            }
          }
        },
        0,
        1,
        TimeUnit.SECONDS);
  }

  private static boolean isDatabaseContainsCooldown(User user, Command command) {
    return collection.count(
            new BasicDBObject().append("user", user.getId()).append("command", command.getAlias()))
        > 0;
  }

  private static BasicDBObject createBasicDBObject(User user, Command command, int cooldown) {
    return new BasicDBObject()
        .append("user", user.getId())
        .append("command", command.getAlias())
        .append("cooldown", cooldown);
  }
}
