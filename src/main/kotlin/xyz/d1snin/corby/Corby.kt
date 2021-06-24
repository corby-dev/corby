/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import xyz.d1snin.corby.commands.`fun`.BottomCommand
import xyz.d1snin.corby.commands.`fun`.CatCommand
import xyz.d1snin.corby.commands.`fun`.UrbanCommand
import xyz.d1snin.corby.commands.admin.TerminateCommand
import xyz.d1snin.corby.commands.misc.PingCommand
import xyz.d1snin.corby.database.DatabaseManager
import xyz.d1snin.corby.event.ReactionUpdateEvent
import xyz.d1snin.corby.event.ServerJoinEvent
import xyz.d1snin.corby.event.reactions.StarboardReactionEvent
import xyz.d1snin.corby.manager.CommandsManager
import xyz.d1snin.corby.manager.CooldownsManager
import xyz.d1snin.corby.manager.ListenersManager
import xyz.d1snin.corby.util.Configs
import xyz.d1snin.corby.util.LaunchFlags
import xyz.d1snin.corby.util.formatTimeMillis
import java.awt.Color
import java.io.File
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.text.DecimalFormat
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

object Corby {
    private const val CONFIG_FILE = "config.json"

    // exit codes
    const val GOOD_EXIT_CODE = 0
    const val DATABASE_ERROR = 10

    lateinit var log: Logger
    lateinit var config: Configs
    lateinit var scheduler: ScheduledExecutorService
    lateinit var shards: ShardManager
    lateinit var firstShard: JDA
    lateinit var permissions: MutableSet<Permission>
    lateinit var selfUser: User

    private lateinit var rb: RuntimeMXBean
    private lateinit var format: DecimalFormat
    private lateinit var defaultPermissions: Set<Permission>

    private var testMode = false
    private var noShardsMode = false

    val ping: String
        get() = format.format(shards.averageGatewayPing)

    val uptime: String
        get() = rb.uptime.formatTimeMillis()

    private val presence
        get() = listOf(
            "Ping: $ping",
            "${shards.guilds.size} Servers!",
            "Uptime: $uptime"
        ).run {
            shuffled().first()
        }

    @JvmStatic
    fun main(args: Array<String>) {
        log = LoggerFactory.getLogger("Loader")
        scheduler = ScheduledThreadPoolExecutor(10)
        defaultPermissions = setOf(
            Permission.MESSAGE_HISTORY,
            Permission.MESSAGE_READ,
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_MANAGE,
            Permission.VIEW_CHANNEL,
            Permission.MANAGE_CHANNEL,
            Permission.MESSAGE_EXT_EMOJI
        )

        LaunchFlags.init(
            args,
            LaunchFlags("test") {
                testMode = true
                log("Launch using the bot token for testing...", Level.WARN)
            },
            LaunchFlags("noshards") {
                noShardsMode = true
                log("Launching without sharding", Level.WARN)
            }
        )

        runCatching {
            start()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun start() {
        log("Starting...")

        DatabaseManager.init()

        permissions = mutableSetOf()
        rb = ManagementFactory.getRuntimeMXBean()
        format = DecimalFormat()

        permissions.addAll(defaultPermissions)

        config = Configs.init(File(CONFIG_FILE))

        shards = DefaultShardManagerBuilder.createDefault(
            if (testMode) config.testBotToken else config.token
        ).run {

            addEventListeners(
                ListenersManager.addAll(
                    StarboardReactionEvent,
                    ReactionUpdateEvent,
                    ServerJoinEvent
                )
            )

            addEventListeners(
                CommandsManager.addAll(
                    TerminateCommand,
                    BottomCommand,
                    CatCommand,
                    UrbanCommand,
                    PingCommand
                )
            )

            enableIntents(
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
            )

            enableCache(
                CacheFlag.CLIENT_STATUS,
                CacheFlag.VOICE_STATE,
                CacheFlag.ACTIVITY,
                CacheFlag.ROLE_TAGS,
                CacheFlag.EMOTE
            )

            setStatus(OnlineStatus.IDLE)

            setShardsTotal(if (noShardsMode) 1 else config.shards)

            build()
        }

        if (!noShardsMode) {
            log("Shards loading can be long\n", Level.WARN)
        }

        shards.shards.forEach {
            it.awaitReady()
        }

        CooldownsManager.startUpdating()
        startPresenceUpdating()

        println(
            """
                
                       ██████╗ ██████╗ ██████╗ ██████╗ ██╗   ██╗  
                      ██╔════╝██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝  
                      ██║     ██║   ██║██████╔╝██████╔╝ ╚████╔╝  
                      ██║     ██║   ██║██╔══██╗██╔══██╗  ╚██╔╝   
                      ╚██████╗╚██████╔╝██║  ██║██████╔╝   ██║     
                       ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚═════╝    ╚═╝     
                       
            """
        )

        firstShard = shards.shards.first()

        selfUser = firstShard.selfUser

        config.other(
            Color(222, 222, 222),
            Color(255, 75, 75),
            Color(112, 228, 120),
            Color(255, 215, 0),
            selfUser.name,
            selfUser.effectiveAvatarUrl,
            firstShard.getInviteUrl(permissions),
            selfUser.id,
            selfUser.asTag
        )

        log = LoggerFactory.getLogger(config.botName)

        log(
            """
                Bot has started up in $uptime!
                    ~ PFP:         ${config.botPfpUrl}
                    ~ Name:        ${config.nameAsTag}
                    ~ ID:          ${config.id}
                    ~ Invite URL:  ${config.inviteUrl}
            """.trimIndent()
        )
    }

    internal fun shutdown(exitCode: Int) {
        log.warn("Terminating... Bye!")
        shards.shutdown()
        scheduler.shutdown()
        exitProcess(exitCode)
    }

    fun log(message: String, level: Level = Level.INFO) = when (level) {
        Level.DEBUG -> log.debug(message)
        Level.ERROR -> log.error(message)
        Level.TRACE -> log.trace(message)
        Level.WARN -> log.warn(message)
        else -> log.info(message)
    }

    private fun startPresenceUpdating() {
        scheduler.scheduleWithFixedDelay(
            {
                shards.setActivity(Activity.watching(presence))
            }, 0, 10, TimeUnit.SECONDS
        )
    }
}