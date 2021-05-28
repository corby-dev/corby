/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.commands.admin.TerminateCommand;
import xyz.d1snin.corby.commands.fun.BottomCommand;
import xyz.d1snin.corby.commands.fun.CatCommand;
import xyz.d1snin.corby.commands.fun.CoinCommand;
import xyz.d1snin.corby.commands.fun.UrbanCommand;
import xyz.d1snin.corby.commands.misc.HelpCommand;
import xyz.d1snin.corby.commands.misc.PingCommand;
import xyz.d1snin.corby.commands.misc.StealCommand;
import xyz.d1snin.corby.commands.misc.UptimeCommand;
import xyz.d1snin.corby.commands.settings.PrefixCommand;
import xyz.d1snin.corby.commands.settings.StarboardCommand;
import xyz.d1snin.corby.event.ReactionUpdateEvent;
import xyz.d1snin.corby.event.ServerJoinEvent;
import xyz.d1snin.corby.event.reactions.StarboardReactionEvent;
import xyz.d1snin.corby.manager.ConfigManager;
import xyz.d1snin.corby.manager.CooldownsManager;
import xyz.d1snin.corby.manager.LaunchArgumentsManager;
import xyz.d1snin.corby.model.Config;
import xyz.d1snin.corby.model.LaunchArgument;
import xyz.d1snin.corby.utils.OtherUtils;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Corby {

  @Getter private static Logger log;
  @Getter private static RuntimeMXBean rb;
  @Getter private static ForkJoinPool service;
  @Getter private static ScheduledExecutorService scheduler;
  @Getter private static DecimalFormat format;
  @Getter private static Random random;
  @Getter private static ShardManager shards;
  @Getter private static JDA firstJda;
  @Getter private static Set<Permission> permissions;
  @Getter private static List<Permission> defaultPermissions;
  @Getter private static List<String> presences;
  @Getter private static Config config;
  @Getter private static boolean testMode;
  @Getter private static boolean noShardsMode;

  public static void main(String[] args) {
    log = LoggerFactory.getLogger("loader");
    rb = ManagementFactory.getRuntimeMXBean();
    service = new ForkJoinPool();
    scheduler = Executors.newScheduledThreadPool(10);
    format = new DecimalFormat();
    random = new Random();
    permissions = new HashSet<>();
    presences = new ArrayList<>();
    testMode = false;
    noShardsMode = false;

    format.setDecimalSeparatorAlwaysShown(false);

    defaultPermissions =
        Arrays.asList(
            Permission.MESSAGE_HISTORY,
            Permission.MESSAGE_READ,
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_MANAGE,
            Permission.VIEW_CHANNEL,
            Permission.MANAGE_CHANNEL);

    try {
      LaunchArgumentsManager.init(
          args,
          new LaunchArgument(
              "test",
              () -> {
                testMode = true;
                log.warn("Start using the bot token for testing...");
              }),
          new LaunchArgument(
              "noshards",
              () -> {
                noShardsMode = true;
                log.warn("Starting without sharding...");
              }));

      log.info("Starting...");

      permissions.addAll(defaultPermissions);

      start();

      startUpdatePresence();
      CooldownsManager.startUpdating();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void start() throws LoginException, IOException {
    config = ConfigManager.init();

    shards =
        DefaultShardManagerBuilder.createDefault(
                testMode ? config.getTestBotToken() : config.getToken())
            .enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
            .enableCache(
                CacheFlag.CLIENT_STATUS,
                CacheFlag.VOICE_STATE,
                CacheFlag.ACTIVITY,
                CacheFlag.ROLE_TAGS,
                CacheFlag.EMOTE)
            .setStatus(OnlineStatus.IDLE)
            .addEventListeners(
                new StarboardReactionEvent(), new ReactionUpdateEvent(), new ServerJoinEvent())
            .addEventListeners(
                new ArrayList<>(
                    Command.addAll(
                        // admin category
                        new TerminateCommand(),
                        // fun category
                        new BottomCommand(),
                        new CatCommand(),
                        new CoinCommand(),
                        new UrbanCommand(),
                        // help category
                        new HelpCommand(),
                        new PingCommand(),
                        new StealCommand(),
                        new UptimeCommand(),
                        // bot settings category
                        new PrefixCommand(),
                        new StarboardCommand())))
            .setShardsTotal(isNoShardsMode() ? 1 : getConfig().getShardsTotal())
            .build();

    System.out.println(
        "\n"
            + String.join(
                "\n",
                "   ██████╗ ██████╗ ██████╗ ██████╗ ██╗   ██╗  ",
                "  ██╔════╝██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝  ",
                "  ██║     ██║   ██║██████╔╝██████╔╝ ╚████╔╝   ",
                "  ██║     ██║   ██║██╔══██╗██╔══██╗  ╚██╔╝    ",
                "  ╚██████╗╚██████╔╝██║  ██║██████╔╝   ██║     ",
                "   ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚═════╝    ╚═╝     ")
            + "\n");

    firstJda = shards.getShards().get(0);

    config.initOther(
        new Color(98, 79, 255),
        new Color(255, 0, 0),
        new Color(70, 255, 0),
        new Color(255, 215, 0),
        firstJda.getSelfUser().getName(),
        firstJda.getSelfUser().getEffectiveAvatarUrl(),
        firstJda.getInviteUrl(permissions),
        firstJda.getSelfUser().getId(),
        firstJda.getSelfUser().getAsTag());

    log = LoggerFactory.getLogger(config.getBotName());

    log.info(
        String.format(
            "Bot has started up in %s!\n"
                + "    ~ PFP:         %s\n"
                + "    ~ Name:        %s\n"
                + "    ~ ID:          %s\n"
                + "    ~ Invite URL:  %s\n",
            getUptime(),
            config.getBotPfpUrl(),
            config.getNameAsTag(),
            config.getId(),
            config.getInviteUrl()));

    if (!isNoShardsMode()) {
      log.warn("Shards loading can be long");
    }
  }

  private static void startUpdatePresence() {
    scheduler.scheduleWithFixedDelay(
        () -> shards.setActivity(Activity.watching(String.format(";help | %s", getPresence()))),
        0,
        7,
        TimeUnit.SECONDS);
  }

  private static String getPresence() {
    presences.clear();
    presences.add(String.format("Ping: %s", getPing()));
    presences.add(String.format("%d Servers!", shards.getGuilds().size()));
    return presences.get(random.nextInt(presences.size()));
  }

  public static void shutdown(int exitCode) {
    log.warn("Terminating... Bye!");
    shards.shutdown();
    scheduler.shutdown();
    service.shutdown();
    System.exit(exitCode);
  }

  public static String getUptime() {
    return OtherUtils.formatMillis(rb.getUptime());
  }

  public static String getPing() {
    return format.format(getShards().getAverageGatewayPing());
  }
}
