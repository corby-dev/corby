/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Corby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import xyz.d1snin.corby.utils.FormatUtils;

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

  public static void start() throws LoginException, IOException, InterruptedException {
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
                        new UrbanCommand(),
                        // misc category
                        new HelpCommand(),
                        new PingCommand(),
                        new StealCommand(),
                        new UptimeCommand(),
                        // bot settings category
                        new PrefixCommand(),
                        new StarboardCommand())))
            .setShardsTotal(isNoShardsMode() ? 1 : getConfig().getShardsTotal())
            .build();

    for (JDA jda : shards.getShards()) {
      jda.awaitReady();
    }

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
        new Color(222, 222, 222),
        new Color(255, 75, 75),
        new Color(112, 228, 120),
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
      log.warn("Shards loading can be long\n");
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
    System.gc();
    System.exit(exitCode);
  }

  public static String getUptime() {
    return FormatUtils.formatMillis(rb.getUptime());
  }

  public static String getPing() {
    return format.format(getShards().getAverageGatewayPing());
  }
}
