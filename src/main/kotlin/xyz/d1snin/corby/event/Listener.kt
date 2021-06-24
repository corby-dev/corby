/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.event

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.util.createEmbed
import xyz.d1snin.corby.util.runSafe
import xyz.d1snin.corby.util.sendDmSafe

abstract class Listener<E : GenericGuildEvent>(vararg _requiredPerms: Permission) : ListenerAdapter() {

    private lateinit var action: E.() -> Unit

    val requiredPerms = _requiredPerms

    protected fun execute(block: E.() -> Unit) {
        action = block
    }

    @Suppress("UNCHECKED_CAST") // this is probably not a good idea, but i dont care about it.
    override fun onGenericGuild(event: GenericGuildEvent) {
        if ((event.guild.botRole != null
                    && event.guild.botRole!!.hasPermission(requiredPerms.asList()))
            || event.guild.selfMember.hasPermission(requiredPerms.asList())
        ) {
            runSafe(ignore = ClassCastException::class) {
                action(event as E)
            }
        } else {
            sendDmSafe(
                event.guild.owner?.user,
                event.createEmbed(
                    "I can't handle some events on your server, please invite me via [this link](${Corby.config.inviteUrl})."
                )
            )
        }
    }
}