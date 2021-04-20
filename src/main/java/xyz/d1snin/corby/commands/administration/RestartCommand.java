package xyz.d1snin.corby.commands.administration;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.Embeds;

import java.util.Arrays;

public class RestartCommand extends Command {

    public RestartCommand() {
        this.admincommand = true;
        this.aliases = Arrays.asList("reload", "reboot", "restart");
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {
        e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, "Restarting...")).queue();
        Corby.restart();
    }
}
