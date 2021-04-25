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
import xyz.d1snin.corby.commands.administration.RestartCommand;
import xyz.d1snin.corby.commands.administration.ShutdownCommand;
import xyz.d1snin.corby.commands.misc.PingCommand;
import xyz.d1snin.corby.commands.settings.PrefixCommand;
import xyz.d1snin.corby.database.Database;
import xyz.d1snin.corby.database.DatabasePreparedStatements;
import xyz.d1snin.corby.event.ReactionUpdateEvent;
import xyz.d1snin.corby.event.ServerJoinEvent;
import xyz.d1snin.corby.manager.config.ConfigFileManager;
import xyz.d1snin.corby.manager.config.ConfigManager;
import xyz.d1snin.corby.manager.config.Config;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Corby {

    private static JDA API;

    private static final ExecutorService service = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private static final List<String> presences = new ArrayList<>();
    private static final Random random = new Random();

    public static Config config = ConfigManager.init();

    public static final Logger logger = LoggerFactory.getLogger(config.bot_name);

    public static void main(String[] args) {

        Thread.currentThread().setName("Worker");

        try {
            ConfigFileManager.initConfigFile();
            logger.info("Starting...");
            start();
        } catch (LoginException e) {
            logger.error("Your bot token is invalid.");
            System.exit(Config.ExitCodes.BAD_TOKEN_EXIT_CODE);
        } catch (InterruptedException e) {
            logger.error("Cant connect to discord.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        startUpdatePresence();

    }

    public static void start() throws LoginException, InterruptedException {
        logger.info("Trying to connect to the database...");
        Database.createConnection();
        logger.info("Loading prepared database statements...");
        DatabasePreparedStatements.loadAllPreparedStatements();

        Command.startCooldownUpdater();

        JDABuilder jdaBuilder = JDABuilder.createDefault(config.token);

        jdaBuilder.enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS);
        jdaBuilder.enableCache(CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY);
        jdaBuilder.setEnableShutdownHook(true);
        jdaBuilder.setStatus(OnlineStatus.IDLE);

        jdaBuilder.addEventListeners(
                new ReactionUpdateEvent(),
                new ServerJoinEvent(),

                Command.add(new PingCommand()),
                Command.add(new PrefixCommand()),
                Command.add(new ShutdownCommand()),
                Command.add(new RestartCommand())
        );

        API = jdaBuilder.build();
        API.awaitReady();
        System.out.println(
                 "\n" + "   ██████╗ ██████╗ ██████╗ ██████╗ ██╗   ██╗  \n" +
                        "  ██╔════╝██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝  \n" +
                        "  ██║     ██║   ██║██████╔╝██████╔╝ ╚████╔╝   \n" +
                        "  ██║     ██║   ██║██╔══██╗██╔══██╗  ╚██╔╝    \n" +
                        "  ╚██████╗╚██████╔╝██║  ██║██████╔╝   ██║     \n" +
                        "   ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚═════╝    ╚═╝       " + "\n"
        );

        config.setBotPfpUrl(API.getSelfUser().getAvatarUrl());
        config.setInviteUrl(API.getInviteUrl(Permission.ADMINISTRATOR));
        config.setId(API.getSelfUser().getId());
        config.setNameAsTag(API.getSelfUser().getAsTag());

        logger.info("Bot has started up!\n   " +
                "~ Name:        " + config.name_as_tag + "\n   " +
                "~ ID:          " + config.id + "\n   " +
                "~ Invite URL:  " + config.invite_url + "\n   " +
                "~ Ping:        " + API.getGatewayPing() + "\n");
    }

    private static void startUpdatePresence() {
        scheduler.scheduleWithFixedDelay(() -> API.getPresence().setActivity(Activity.watching("'help | " + getPresence())), 0, 7, TimeUnit.SECONDS);
    }

    private static String getPresence() {
        presences.clear();
        presences.add("Ping: " + API.getGatewayPing());
        presences.add(API.getGuilds().size() + " Servers!");
        return presences.get(random.nextInt(presences.size()));
    }

    public static void shutdown() {
        logger.warn("Terminating... Bye!");
        Database.close();
        API.shutdown();
        getService().shutdown();
        scheduler.shutdown();
        System.exit(Config.ExitCodes.NORMAL_SHUTDOWN_EXIT_CODE);
    }

    public static void restart() {
        logger.warn("Restarting...");
        Database.close();
        API.shutdown();
        try {
            start();
        } catch (LoginException | InterruptedException ignored) {
        }
    }

    public static JDA getAPI() {
        return API;
    }

    public static ExecutorService getService() {
        return service;
    }
}
