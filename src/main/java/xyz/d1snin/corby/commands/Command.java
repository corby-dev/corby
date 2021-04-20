package xyz.d1snin.corby.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.ColorUtil;

import java.util.Arrays;
import java.util.EnumSet;

public abstract class Command extends ListenerAdapter {

    protected abstract void execute(MessageReceivedEvent e, String[] args);

    protected String aliases = "null";
    protected Permission[] permissions = new Permission[0];
    protected boolean admincommand = false;

    public void onMessageReceived(MessageReceivedEvent e) {

        Message msg = e.getMessage();

        if (e.getAuthor().isBot()) {
            return;
        }

        if (isCommand(msg, e)) {
            if (!hasPermission(e)) {
                e.getTextChannel().sendMessage(new EmbedBuilder()
                        .setColor(ColorUtil.getErrorColor())
                        .setDescription("You must have permissions `" + getPermissionString() + "` to use this command.")
                        .setFooter(e.getAuthor().getName(), e.getAuthor().getEffectiveAvatarUrl())
                        .build()).queue();
                return;
            }

            if (admincommand && !e.getAuthor().getId().equals(Corby.OWNER_ID)) {
                return;
            }

            execute(e, getCommandArgs(msg));
        }
    }


    private boolean hasPermission(MessageReceivedEvent event) {

        if (permissions.length == 0) return true;

        EnumSet<Permission> permissions = event.getMember().getPermissions();

        if (permissions.containsAll(Arrays.asList(this.permissions))) return true;

        return false;
    }

    private String getPermissionString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < permissions.length; i++) {
            sb.append(permissions[i].getName()).append((i == permissions.length - 1) ? "" : ", ");
        }
        return sb.toString();
    }

    private boolean isCommand(Message message, MessageReceivedEvent event) {
        for (String alias : aliases.split(", ")) {
            if (Arrays.asList(getCommandArgs(message)).contains(GuildSettingsManager.getGuildPrefix(event.getGuild()) + alias))
                return true;
        }
        return false;
    }

    protected String[] getCommandArgs(Message message) {
        return message.getContentRaw().split("\\s+");
    }
}
