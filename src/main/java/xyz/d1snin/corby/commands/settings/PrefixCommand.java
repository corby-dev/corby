package xyz.d1snin.corby.commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.Embeds;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        this.aliases = "prefix";
        this.permissions = new Permission[]{
                Permission.ADMINISTRATOR
        };
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {

        String currentPrefix = GuildSettingsManager.getGuildPrefix(e.getGuild());

        if (args.length < 2) {
            e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, "Current prefix is `" + currentPrefix + "`\n" +
                    "Use " + currentPrefix + "prefix <New Prefix> to change it!")).queue();
            return;
        }

        String newPrefix = args[1];

        if (currentPrefix.equals(newPrefix)) {
            e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, "Bot prefix is already `" + newPrefix + "`")).queue();
            return;
        }

        if (newPrefix.length() > 5) {
            e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, "The prefix cannot be more than 5 characters.")).queue();
            return;
        }

        GuildSettingsManager.setGuildPrefix(e.getGuild(), newPrefix);

        e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, "The prefix was successfully changed to `" + newPrefix + "`" +
                "\nExample of usage: " + newPrefix + "ping")).queue();
    }
}
