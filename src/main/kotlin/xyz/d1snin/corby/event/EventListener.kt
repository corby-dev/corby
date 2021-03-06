/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.event

import kotlinx.coroutines.launch
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.util.runSafe

abstract class EventListener<E : Event>(vararg _requiredPerms: Permission) : ListenerAdapter() {

    private lateinit var action: E.() -> Unit

    val requiredPerms = _requiredPerms

    protected fun execute(block: E.() -> Unit) {
        action = block
    }

    @Suppress("UNCHECKED_CAST")
    override fun onGenericEvent(event: GenericEvent) {
        try {
            event as E  // We just use ClassCastException to determine whether this event is suitable for us or not (we can't determine the erased type)
        } catch (_: ClassCastException) {
            return
        }

        if (event is GenericGuildEvent && (
                    event.guild.botRole == null
                            || !event.guild.botRole?.hasPermission(requiredPerms.asList())!!
                            || !event.guild.selfMember.hasPermission(requiredPerms.asList()))
        ) {
            return
        }

        Corby.defaultScope.launch {
            runSafe {
                action(event)
            }
        }
    }
}