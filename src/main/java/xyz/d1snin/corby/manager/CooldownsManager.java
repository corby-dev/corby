/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.manager;

import net.dv8tion.jda.api.entities.User;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.model.Cooldown;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CooldownsManager {
  private static final List<Cooldown> cooldowns = new CopyOnWriteArrayList<>();

  private static final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor();

  public static void setCooldown(Cooldown cooldown) {
    if (cooldowns.contains(cooldown)) {
      return;
    }
    cooldowns.add(cooldown);
  }

  public static int getCooldown(User u, Command command) {
    for (Cooldown c : cooldowns) {
      if (c.getUser().equals(u) && c.getCommand().equals(command)) {
        return c.getCooldown();
      }
    }
    return 0;
  }

  public static void startUpdating() {
    executor.scheduleWithFixedDelay(
        () -> {
          for (Cooldown c : cooldowns) {
            if (c.getCooldown() == 0) {
              cooldowns.remove(c);
            } else {
              cooldowns.get(cooldowns.indexOf(c)).setCooldown(c.getCooldown() - 1);
            }
          }
        },
        0,
        1,
        TimeUnit.SECONDS);
  }
}
