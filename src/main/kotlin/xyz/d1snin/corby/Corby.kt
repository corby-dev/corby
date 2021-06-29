/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby

import ch.qos.logback.classic.Level
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
import xyz.d1snin.corby.commands.`fun`.BottomCommand
import xyz.d1snin.corby.commands.`fun`.UrbanCommand
import xyz.d1snin.corby.commands.admin.TerminateCommand
import xyz.d1snin.corby.commands.misc.HelpCommand
import xyz.d1snin.corby.commands.misc.PingCommand
import xyz.d1snin.corby.database.DatabaseManager
import xyz.d1snin.corby.event.ButtonEvent
import xyz.d1snin.corby.event.ServerJoinEvent
import xyz.d1snin.corby.event.reactions.StarboardReactionEvent
import xyz.d1snin.corby.manager.CommandsManager
import xyz.d1snin.corby.manager.CooldownsManager
import xyz.d1snin.corby.manager.ListenersManager
import xyz.d1snin.corby.util.Configs
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

    val defaultScope = CoroutineScope(Dispatchers.Default)

    lateinit var log: Logger
    lateinit var config: Configs
    lateinit var scheduler: ScheduledExecutorService
    lateinit var sharding: ShardManager
    lateinit var firstShard: JDA
    lateinit var permissions: MutableSet<Permission>
    lateinit var selfUser: User

    private lateinit var rb: RuntimeMXBean
    private lateinit var format: DecimalFormat
    private lateinit var defaultPermissions: Set<Permission>

    val ping: String
        get() = format.format(sharding.averageGatewayPing)

    val uptime: String
        get() = rb.uptime.formatTimeMillis()

    private val presence
        get() = listOf(
            "Ping: $ping",
            "${sharding.guilds.size} Servers!",
            "Uptime: $uptime"
        ).run {
            shuffled().first()
        }

    fun launch(args: Array<String>) {
        log = LoggerFactory.getLogger("Loader")

        log("Launching...")

        val arguments = Arguments().also {
            JCommander.newBuilder()
                .addObject(it)
                .build()
                .parse(*args)
        }

        defaultPermissions = setOf(
            Permission.MESSAGE_HISTORY,
            Permission.MESSAGE_READ,
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_MANAGE,
            Permission.VIEW_CHANNEL,
            Permission.MANAGE_CHANNEL
        )

        scheduler = ScheduledThreadPoolExecutor(10)
        permissions = mutableSetOf<Permission>().apply { addAll(defaultPermissions) }
        rb = ManagementFactory.getRuntimeMXBean()
        format = DecimalFormat()
        config = Configs.init(File(CONFIG_FILE))

        sharding = DefaultShardManagerBuilder.createDefault(
            if (arguments.isTestMode) config.testBotToken else config.token
        ).run {
            addEventListeners(
                ListenersManager.addAll(
                    StarboardReactionEvent,
                    ButtonEvent,
                    ServerJoinEvent
                )
            )

            addEventListeners(
                CommandsManager.addAll(
                    TerminateCommand,
                    BottomCommand,
                    UrbanCommand,
                    HelpCommand,
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

            setShardsTotal(if (arguments.isNoShards) 1 else config.shards)

            build()
        }

        firstShard = sharding.shards.first()
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

        DatabaseManager.init()
        CooldownsManager.startUpdating()
        startPresenceUpdating()

        log = LoggerFactory.getLogger(config.botName)

        log("Bot has started up in $uptime!")
    }

    internal fun shutdown(exitCode: Int) {
        log.warn("Terminating... Bye!")
        sharding.shutdown()
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
                sharding.setActivity(Activity.watching(presence))
            }, 0, 10, TimeUnit.SECONDS
        )
    }

    private class Arguments {
        @Parameter(
            names = ["--test", "-T"],
            description = "Launch the bot using token for testing."
        )
        var isTestMode = false

        @Parameter(
            names = ["--noshards", "-NS"],
            description = "Launch the bot without sharding."
        )
        var isNoShards = false
    }
}