/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.commands

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.slf4j.event.Level
import xyz.d1snin.corby.Corby
import xyz.d1snin.corby.Corby.log
import xyz.d1snin.corby.database.managers.PrefixManager
import xyz.d1snin.corby.event.Listener
import xyz.d1snin.corby.manager.CommandsManager
import xyz.d1snin.corby.manager.CooldownsManager
import xyz.d1snin.corby.model.Argument
import xyz.d1snin.corby.model.Category
import xyz.d1snin.corby.model.EmbedType
import xyz.d1snin.corby.model.Statement
import xyz.d1snin.corby.util.createEmbed
import xyz.d1snin.corby.util.isOwner
import xyz.d1snin.corby.util.sendDmSafe

abstract class AbstractCommand(
    val usage: String,
    val description: String,
    val category: Category,
    var cooldown: Int = 0,
    val longDescription: String? = null,
    private val userPerms: List<Permission> = mutableListOf(),
    val botPerms: List<Permission> = mutableListOf()
) : Listener<GuildMessageReceivedEvent>() {

    lateinit var event: GuildMessageReceivedEvent
    lateinit var statement: Statement // could not be initialized if only default execution used

    private lateinit var provider: CommandProvider

    fun executeStatement(provider: CommandProvider) = statement.block(provider)

    val statements = mutableListOf<Statement>()

    var defaultAction: (CommandProvider.() -> Unit?)? = null
    var action: (CommandProvider.() -> Unit?)? = null

    init {
        super.execute {
            event = this
            provider = CommandProvider(this@AbstractCommand)

            val executor = CommandExecutor(provider)

            if (author.isBot
                || !provider.msg.isFromGuild
                || provider.msg.isWebhookMessage
                || !CommandsManager.commands.contains(this@AbstractCommand)
            ) {
                return@execute
            }

            if (isCommand()) {
                if (category == Category.ADMIN && !isOwner(author)) {
                    return@execute
                }

                if (!guild.selfMember.hasPermission(event.channel, botPerms)
                    || guild.botRole == null
                    || (!guild.botRole!!.hasPermission(botPerms)
                            && !guild.selfMember.hasPermission(botPerms))
                ) {
                    sendDmSafe(
                        guild.owner?.user,
                        event.createEmbed(
                            "I do not have or I do not have enough permissions on your server," +
                                    "please invite me using [this link](${Corby.config.inviteUrl})!",
                            type = EmbedType.ERROR
                        )
                    )
                    return@execute
                }

                if (!hasPermissions()) {
                    provider.sendFastEmbed(
                        "You must have permissions ${getRequiredPermissionsAsString()} to use this command.",
                        EmbedType.ERROR
                    )
                    return@execute
                }

                CooldownsManager.getCooldown(author, this@AbstractCommand).let {
                    if (it > 0) {
                        provider.sendEphemeralEmbed(
                            "You are currently on cooldown, wait **$it seconds** to use this command again!",
                            EmbedType.ERROR,
                            7
                        )
                        return@execute
                    }
                }

                if (!executor.tryToExecute()) {
                    provider.trigger() // invalid syntax message
                }
            }
        }
    }

    protected fun execute(vararg args: Argument, block: CommandProvider.() -> Unit) {
        if (args.isEmpty()) {
            log("Provided arguments is empty.", Level.WARN)
            return
        }

        val statement = Statement(args.asList(), block)

        statements += statement

        args.forEach {
            if (it.isVariableLength && !it.isValueRequired) {
                log("Argument has variable length but value is not required. ($it)", Level.WARN)
            }

            if (it.isVariableLength) {
                statement.length = 0
                return
            }

            if (it.isValueRequired) {
                statement.length += 2

            } else {
                statement.length += 1
            }
        }
    }

    protected fun default(block: CommandProvider.() -> Unit) {
        defaultAction = block
    }

    private fun isCommand(): Boolean {
        if (!CommandsManager.commands.contains(this)) {
            return false
        }

        PrefixManager[event.guild].prefix.let {
            return provider.args[0] == it + usage
                    && provider.args[0].startsWith(it)
        }
    }

    private fun hasPermissions(): Boolean {
        if (userPerms.isEmpty()) {
            return true
        }

        return event.member!!.hasPermission(userPerms)
    }

    private fun getRequiredPermissionsAsString(): String {
        return buildString {
            userPerms.forEach {
                append(it.name).append(if (userPerms.last() == it) "" else ", ")
            }
        }
    }
}