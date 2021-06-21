package xyz.d1snin.corby.event

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.util.createEmbed
import xyz.d1snin.corby.util.runSafe
import xyz.d1snin.corby.util.sendDmSafe

open class Listener<E : GenericGuildEvent>(vararg _requiredPerms: Permission) : ListenerAdapter() {

    private lateinit var action: E.() -> Unit
    private val requiredPerms = _requiredPerms

    internal fun execute(block: E.() -> Unit) {
        action = block
    }

    @Suppress("UNCHECKED_CAST")
    override fun onGenericGuild(event: GenericGuildEvent) {
        if ((event as E)::class == event::class) {
            if (event.guild.botRole!!.hasPermission(requiredPerms.asList())) {
                runSafe {
                    action(event)
                }
            } else {
                runCatching {
                    sendDmSafe(
                        event.guild.owner?.user!!,
                        createEmbed(
                            "I can't handle some events on your server, please invite me via [this link](${Corby.config.inviteUrl}).",
                            event.guild
                        )
                    )
                }
            }
        }
    }
}