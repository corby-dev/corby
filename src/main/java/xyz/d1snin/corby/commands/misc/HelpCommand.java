package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;

public class HelpCommand extends Command {

    public HelpCommand() {
        this.use = "help";
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {
        e.getMessage().addReaction(Corby.config.emote_white_check_mark).queue();
        e.getAuthor().openPrivateChannel().complete().sendMessage(new EmbedBuilder()
                .setAuthor(e.getGuild().getName(), Corby.config.help_page_url, e.getGuild().getIconUrl() == null ? "https://media.discordapp.net/attachments/835925114700300380/836291623885340723/iu.png" : e.getGuild().getIconUrl())
                .setDescription("**Server:** " + e.getGuild().getName() +
                        "\n**Prefix for commands on this server:** `" + GuildSettingsManager.getGuildPrefix(e.getGuild()) + "`" +
                        "\n[Commands list](" + Corby.config.help_page_url + ")" +
                        "\n[Invite me on your server!](" + Corby.config.invite_url + ")")
                .setColor(Corby.config.default_color).build()).queue();
    }
}
