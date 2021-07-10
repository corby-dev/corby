/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

package xyz.d1snin.corby.manager

import net.dv8tion.jda.api.entities.User
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.commands.AbstractCommand
import xyz.d1snin.corby.model.Cooldown
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit


object CooldownsManager {
    private val cooldowns = CopyOnWriteArrayList<Cooldown>()

    internal operator fun plusAssign(cooldown: Cooldown) {
        if (!cooldowns.contains(cooldown)) {
            cooldowns += cooldown
        }
    }

    internal fun getCooldown(u: User, cmd: AbstractCommand): Int {
        cooldowns.forEach {
            it.apply {
                if (user == u && command == cmd) {
                    return cooldown
                }
            }
        }
        return 0
    }

    internal fun startUpdating() {
        Corby.scheduler.scheduleWithFixedDelay(
            {
                cooldowns.forEach {
                    if (it.cooldown == 0) {
                        cooldowns -= it
                    } else {
                        cooldowns[cooldowns.indexOf(it)].cooldown = it.cooldown - 1
                    }
                }
            },
            0,
            1,
            TimeUnit.SECONDS
        )
    }
}