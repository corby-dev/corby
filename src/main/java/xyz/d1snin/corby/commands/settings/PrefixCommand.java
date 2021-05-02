/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        this.use = "prefix";
        this.permissions = new Permission[]{
                Permission.ADMINISTRATOR
        };
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {

        final String currPrefix = "Current prefix is `%s`.";
        final String prefixAlready = "Bot prefix is already `%s`.";
        final String cannotBeMoreThen = "The prefix cannot be more than 5 characters.";
        final String successChanged = "The prefix was successfully changed to `%s`.";

        String currentPrefix = GuildSettingsManager.getGuildPrefix(e.getGuild());

        if (args.length < 2) {
            e.getTextChannel().sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), String.format(currPrefix, currentPrefix))).queue();
            return;
        }

        String newPrefix = args[1];

        if (currentPrefix.equals(newPrefix)) {
            Embeds.createAndSendWithReaction(EmbedTemplate.ERROR, e.getAuthor(), e.getTextChannel(), Corby.config.emoteTrash,
                    String.format(prefixAlready, newPrefix));
            return;
        }

        if (newPrefix.length() > 5) {
            Embeds.createAndSendWithReaction(EmbedTemplate.ERROR, e.getAuthor(), e.getTextChannel(), Corby.config.emoteTrash, cannotBeMoreThen);
            return;
        }

        GuildSettingsManager.setGuildPrefix(e.getGuild(), newPrefix);

        e.getTextChannel().sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), String.format(successChanged, newPrefix))).queue();
    }
}
