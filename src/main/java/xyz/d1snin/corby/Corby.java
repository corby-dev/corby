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
import xyz.d1snin.corby.commands.administration.RestartCommand;
import xyz.d1snin.corby.commands.administration.ShutdownCommand;
import xyz.d1snin.corby.commands.misc.PingCommand;
import xyz.d1snin.corby.commands.misc.WhoisCommand;
import xyz.d1snin.corby.commands.settings.PrefixCommand;
import xyz.d1snin.corby.database.Database;
import xyz.d1snin.corby.database.DatabasePreparedStatements;
import xyz.d1snin.corby.event.ReactionUpdateEvent;
import xyz.d1snin.corby.manager.ConfigFileManager;
import xyz.d1snin.corby.utils.JSONUtils;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Corby {

    private static JDA API;

    public static final String BOT_PREFIX_DEFAULT = "'";
    public static final String BOT_NAME = "Corby";
    public static final String CONFIG_FILE_NAME = "config.json";
    public static final String OWNER_ID = "637302865047584788";

    public static String BOT_PFP_URL;
    public static String INVITE_URL;
    public static String ID;
    public static String TOKEN;
    public static String NAME_AS_TAG;

    public static Color DEFAULT_COLOR = new Color(74, 129, 248);
    public static Color ERROR_COLOR = Color.RED;

    public static int NORMAL_SHUTDOWN_EXIT_CODE = 0;
    public static int CANT_CONNECT_TO_THE_DATABASE = 11;
    public static int BAD_TOKEN = 21;

    public static String EMOTE_TRASH = "\uD83D\uDDD1";

    public static final Logger logger = LoggerFactory.getLogger(BOT_NAME);

    private static final ExecutorService service = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        try {
            ConfigFileManager.createConfigFile(CONFIG_FILE_NAME);
            TOKEN = JSONUtils.readJSON(CONFIG_FILE_NAME, "token");
            logger.info("Starting...");
            start();
        } catch (LoginException e) {
            logger.error("Your bot token is invalid.");
            System.exit(BAD_TOKEN);
        } catch (InterruptedException e) {
            logger.error("Cant connect to discord.");
        }
        startUpdatePresence();
    }

    public static void start() throws LoginException, InterruptedException {
        logger.info("Trying to connect to the database...");
        Database.createConnection();
        logger.info("Loading prepared database statements...");
        DatabasePreparedStatements.loadAllPreparedStatements();

        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);

        jdaBuilder.enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_REACTIONS);
        jdaBuilder.enableCache(CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY);
        jdaBuilder.setEnableShutdownHook(true);
        jdaBuilder.setStatus(OnlineStatus.IDLE);

        jdaBuilder.addEventListeners(
                new ReactionUpdateEvent(),

                new PingCommand(),
                new PrefixCommand(),
                new ShutdownCommand(),
                new WhoisCommand(),
                new RestartCommand()
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

        BOT_PFP_URL = API.getSelfUser().getAvatarUrl();
        INVITE_URL = API.getInviteUrl(Permission.ADMINISTRATOR);
        ID = API.getSelfUser().getId();
        NAME_AS_TAG = API.getSelfUser().getAsTag();

        logger.info("Bot has started up!\n   " +
                "~ Name:        " + NAME_AS_TAG + "\n   " +
                "~ ID:          " + ID + "\n   " +
                "~ Invite URL:  " + INVITE_URL + "\n   " +
                "~ Ping:        " + API.getGatewayPing() + "\n");
    }

    private static void startUpdatePresence() {
        scheduler.scheduleWithFixedDelay(() -> API.getPresence().setActivity(Activity.watching("'help | Ping: " + API.getGatewayPing())), 0, 10, TimeUnit.SECONDS);
    }

    public static void shutdown() {
        logger.warn("Terminating... Bye!");
        Database.close();
        API.shutdown();
        getService().shutdown();
        scheduler.shutdown();
        System.exit(NORMAL_SHUTDOWN_EXIT_CODE);
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
