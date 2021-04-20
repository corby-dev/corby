package xyz.d1snin.corby.commands.administration;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.Embeds;

public class ShutdownCommand extends Command {

    public ShutdownCommand() {
        this.admincommand = true;
        this.aliases = "shut, shutdown, bye, goodnight, terminate";
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {
        e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, "Terminating... Bye!")).complete();
        Corby.shutdown();
    }
}
