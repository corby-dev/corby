package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

public class PingCommand extends Command {

    public PingCommand() {
        this.aliases = "ping";
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {
        e.getTextChannel().sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), String.format("Current ping: %dms", Corby.getAPI().getGatewayPing()))).queue();
    }
}
