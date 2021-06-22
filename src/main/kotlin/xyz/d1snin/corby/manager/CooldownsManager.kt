/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.manager

import net.dv8tion.jda.api.entities.User
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.commands.AbstractCommand
import xyz.d1snin.corby.model.Cooldown
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit


object CooldownsManager {
    val cooldowns = CopyOnWriteArrayList<Cooldown>()

    operator fun plusAssign(cooldown: Cooldown) {
        if (!cooldowns.contains(cooldown)) {
            cooldowns += cooldown
        }
    }

    fun getCooldown(u: User, cmd: AbstractCommand): Int {
        cooldowns.forEach {
            it.apply {
                if (user == u && command == cmd) {
                    return cooldown
                }
            }
        }
        return 0
    }

    fun startUpdating() {
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