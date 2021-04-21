package xyz.d1snin.corby.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.Embeds;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;

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

            try {
                execute(e, getCommandArgs(msg));
            } catch (Exception exception) {
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, "**An exception was caught while executing a command.**"
                        + "\n\n**Caused by:**\n`"
                        + (exception.getCause() == null ? "No reason given." : exception.getCause())
                        + "`\n\n**Message:**\n`"
                        + exception.getClass().getName() + ": " + exception.getMessage()
                        + "`\n\nAll necessary information has been sent to the owner! You can delete this message with the button below.")).queue(
                        (message -> message.addReaction(Corby.EMOTE_TRASH).queue())
                );
                Corby.getAPI().openPrivateChannelById(Corby.OWNER_ID).complete().sendMessage(Embeds.createDefaultEmbed(e, "**An exception was thrown while trying to execute a command.**"
                + "\n\n**User message:**\n`"
                + e.getMessage().getContentRaw()
                + "`\n\n**Exception message:**\n`"
                + exception.getClass().getName() + ": " + exception.getMessage()
                + "`\n\n**Caused by:**\n`"
                + (exception.getCause() == null ? "No reason given." : exception.getCause()) + "`")).queue();
            }

        }
    }


    private boolean hasPermission(MessageReceivedEvent event) {

        if (permissions.length == 0) return true;

        EnumSet<Permission> userPermissions = Objects.requireNonNull(event.getMember()).getPermissions();

        return userPermissions.containsAll(Arrays.asList(this.permissions));
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
