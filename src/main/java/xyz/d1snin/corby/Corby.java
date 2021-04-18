package xyz.d1snin.corby;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import xyz.d1snin.corby.commands.administration.ShutdownCommand;
import xyz.d1snin.corby.commands.misc.PingCommand;
import xyz.d1snin.corby.commands.moderation.PrefixCommand;
import xyz.d1snin.corby.database.Database;
import xyz.d1snin.corby.database.DatabasePreparedStatements;
import xyz.d1snin.corby.manager.ConfigFileManager;
import xyz.d1snin.corby.utils.JSONUtils;
import xyz.d1snin.corby.utils.logging.Logger;
import xyz.d1snin.corby.utils.logging.LoggingTypes;

import javax.security.auth.login.LoginException;

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

    public static void main(String[] args) {
        try {
            ConfigFileManager.createConfigFile(CONFIG_FILE_NAME);
            TOKEN = JSONUtils.readJSON(CONFIG_FILE_NAME, "token");
            Logger.log(LoggingTypes.LOADER, "Starting...");
            start();
        } catch (LoginException e) {
            Logger.log(LoggingTypes.ERROR, "Your bot token is invalid.");
            System.exit(BAD_TOKEN);
        } catch (InterruptedException e) {
            Logger.log(LoggingTypes.ERROR, "Cant connect to discord.");
        }
        startUpdatePresence();
    }

    public static void start() throws LoginException, InterruptedException {
        Logger.log(LoggingTypes.DATABASE, "Trying to connect to the database...");
        Database.createConnection();
        Logger.log(LoggingTypes.DATABASE, "Loading prepared database statements...");
        DatabasePreparedStatements.loadAllPreparedStatements();

        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);

        jdaBuilder.enableIntents(GatewayIntent.GUILD_PRESENCES);
        jdaBuilder.enableCache(CacheFlag.CLIENT_STATUS, CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY);
        jdaBuilder.setEnableShutdownHook(true);

        jdaBuilder.setStatus(OnlineStatus.IDLE);

        jdaBuilder.addEventListeners(
                new PingCommand(),
                new PrefixCommand(),
                new ShutdownCommand()
        );

        API = jdaBuilder.build();
        API.awaitReady();
        System.out.println(
                "   ██████╗ ██████╗ ██████╗ ██████╗ ██╗   ██╗  \n" +
                "  ██╔════╝██╔═══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝  \n" +
                "  ██║     ██║   ██║██████╔╝██████╔╝ ╚████╔╝   \n" +
                "  ██║     ██║   ██║██╔══██╗██╔══██╗  ╚██╔╝    \n" +
                "  ╚██████╗╚██████╔╝██║  ██║██████╔╝   ██║     \n" +
                "   ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚═════╝    ╚═╝       "
        );

        BOT_PFP_URL = API.getSelfUser().getAvatarUrl();
        INVITE_URL = API.getInviteUrl(Permission.ADMINISTRATOR);
        ID = API.getSelfUser().getId();
        NAME_AS_TAG = API.getSelfUser().getAsTag();

        Logger.log(LoggingTypes.LOADER, "Bot has started up!\n   " +
                "~ Name:        " + NAME_AS_TAG + "\n   " +
                "~ ID:          " + ID + "\n   " +
                "~ Invite URL:  " + INVITE_URL + "\n   " +
                "~ Ping:        " + API.getGatewayPing());
    }

    private static void startUpdatePresence() {
        new Thread(() -> {
            while (true) {
                API.getPresence().setActivity(Activity.watching("'help | Ping: " + API.getGatewayPing()));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    public static void shutdown() {
        Database.close();
        API.shutdownNow();
        System.exit(NORMAL_SHUTDOWN_EXIT_CODE);
    }

    public static int NORMAL_SHUTDOWN_EXIT_CODE = 0;

    public static int CANT_CONNECT_TO_THE_DATABASE = 11;

    public static int BAD_TOKEN = 21;

    public static JDA getAPI() {
        return API;
    }
}
