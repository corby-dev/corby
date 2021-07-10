/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
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