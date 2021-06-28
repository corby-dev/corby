/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.manager

import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.Corby.log
import xyz.d1snin.corby.event.EventListener

object ListenersManager {
    internal fun addAll(vararg listeners: EventListener<*>): List<EventListener<*>> {
        listeners.forEach {
            Corby.permissions.addAll(it.requiredPerms)

            log("Listener is initialized: ${it::class.simpleName}")
        }

        log("Listeners initialization completed.")
        return listeners.asList()
    }
}