/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Corby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
