/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.commands.admin.TerminateCommand;
import xyz.d1snin.corby.commands.fun.BottomCommand;
import xyz.d1snin.corby.commands.fun.CoinCommand;
import xyz.d1snin.corby.commands.fun.UrbanCommand;
import xyz.d1snin.corby.commands.misc.*;
import xyz.d1snin.corby.commands.settings.PrefixCommand;
import xyz.d1snin.corby.commands.settings.StarboardCommand;
import xyz.d1snin.corby.database.DatabaseManager;
import xyz.d1snin.corby.event.ReactionUpdateEvent;
import xyz.d1snin.corby.event.ServerJoinEvent;
import xyz.d1snin.corby.event.reactions.StarboardReactionEvent;
import xyz.d1snin.corby.manager.CooldownsManager;
import xyz.d1snin.corby.manager.LaunchArgumentsManager;
import xyz.d1snin.corby.manager.config.ConfigFileManager;
import xyz.d1snin.corby.manager.config.ConfigManager;
import xyz.d1snin.corby.model.Config;
import xyz.d1snin.corby.model.LaunchArgument;
import xyz.d1snin.corby.utils.OtherUtils;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Corby {

  public static final RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
  public static final Set<Permission> permissions = new TreeSet<>();
  public static final List<Permission> defaultPermissions =
      Arrays.asList(
          Permission.MESSAGE_HISTORY,
          Permission.MESSAGE_READ,
          Permission.MESSAGE_WRITE,
          Permission.MESSAGE_MANAGE,
          Permission.VIEW_CHANNEL,
          Permission.MANAGE_CHANNEL);
  private static final ScheduledExecutorService schedulerPresence =
      Executors.newScheduledThreadPool(10);
  private static final ForkJoinPool service = new ForkJoinPool();
  private static final List<String> presences = new ArrayList<>();
  private static final Random random = new Random();
  public static Config config;
  public static Logger log = LoggerFactory.getLogger("loader");
  private static JDA api;
  private static boolean testMode = false;

  public static void main(String[] args) {
    try {
      LaunchArgumentsManager.init(
          args,
          new LaunchArgument(
              "test",
              () -> {
                testMode = true;
                log.warn("Start using the bot token for testing...");
              }));

      ConfigFileManager.initConfigFile();

      log.info("Starting...");

      permissions.addAll(defaultPermissions);

      start();

      startUpdatePresence();
      CooldownsManager.startUpdating();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void start() throws LoginException, InterruptedException, IOException {
    config = ConfigManager.init();

    log.info("Trying to connect to the database...");
    DatabaseManager.createConnection();

    JDABuilder jdaBuilder =
        JDABuilder.createDefault(testMode ? config.getTestBotToken() : config.getToken());

    jdaBuilder.enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS);
    jdaBuilder.enableCache(
        CacheFlag.CLIENT_STATUS,
        CacheFlag.VOICE_STATE,
        CacheFlag.ACTIVITY,
        CacheFlag.ROLE_TAGS,
        CacheFlag.EMOTE);
    jdaBuilder.setEnableShutdownHook(true);
    jdaBuilder.setStatus(OnlineStatus.IDLE);

    jdaBuilder.addEventListeners(
        new ReactionUpdateEvent(),
        new ServerJoinEvent(),
        new StarboardReactionEvent(),
        Command.add(new PingCommand()),
        Command.add(new PrefixCommand()),
        Command.add(new TerminateCommand()),
        Command.add(new StarboardCommand()),
        Command.add(new HelpCommand()),
        Command.add(new BottomCommand()),
        Command.add(new CatCommand()),
        Command.add(new StealCommand()),
        Command.add(new CoinCommand()),
        Command.add(new UptimeCommand()),
        Command.add(new UrbanCommand()));

    api = jdaBuilder.build();
    api.awaitReady();
    System.out.println(
        "\n"
            + "   ██████╗ ██████╗ ██████╗ ██████╗ ██╗   ██╗  \n"
            + "  ██╔════╝██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝  \n"
            + "  ██║     ██║   ██║██████╔╝██████╔╝ ╚████╔╝   \n"
            + "  ██║     ██║   ██║██╔══██╗██╔══██╗  ╚██╔╝    \n"
            + "  ╚██████╗╚██████╔╝██║  ██║██████╔╝   ██║     \n"
            + "   ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚═════╝    ╚═╝       "
            + "\n");

    config.initOther(
        new Color(98, 79, 255),
        new Color(255, 0, 0),
        new Color(70, 255, 0),
        new Color(255, 215, 0),
        api.getSelfUser().getName(),
        api.getSelfUser().getEffectiveAvatarUrl(),
        api.getInviteUrl(permissions),
        api.getSelfUser().getId(),
        api.getSelfUser().getAsTag());

    log = LoggerFactory.getLogger(config.getBotName());

    log.info(
        String.format(
            "Bot has started up in %s!\n"
                + "    ~ PFP:         %s\n"
                + "    ~ Name:        %s\n"
                + "    ~ ID:          %s\n"
                + "    ~ Invite URL:  %s\n"
                + "    ~ Ping:        %s\n",
            getUptime(),
            config.getBotPfpUrl(),
            config.getNameAsTag(),
            config.getId(),
            config.getInviteUrl(),
            api.getGatewayPing()));
  }

  private static void startUpdatePresence() {
    schedulerPresence.scheduleWithFixedDelay(
        () ->
            api.getPresence()
                .setActivity(Activity.watching(String.format(";help | %s", getPresence()))),
        0,
        7,
        TimeUnit.SECONDS);
  }

  private static String getPresence() {
    presences.clear();
    presences.add(String.format("Ping: %d", api.getGatewayPing()));
    presences.add(String.format("%d Servers!", api.getGuilds().size()));
    return presences.get(random.nextInt(presences.size()));
  }

  public static void shutdown(int exitCode) {
    log.warn("Terminating... Bye!");
    api.shutdownNow();
    schedulerPresence.shutdown();
    service.shutdown();
    System.exit(exitCode);
  }

  public static JDA getApi() {
    return api;
  }

  public static String getUptime() {
    return OtherUtils.formatMillis(rb.getUptime());
  }

  public static ForkJoinPool getService() {
    return service;
  }
}
