package xyz.d1snin.corby.commands.administration;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.Embeds;

import java.io.IOException;

public class RestartCommand extends Command {

    public RestartCommand() {
        this.admincommand = true;
        this.aliases = "reload, reboot, restart";
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {
        if (args.length > 1) {
            if (args[1].equals("server")) {
                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, "Restarting server...")).complete();
                try {
                    Runtime.getRuntime().exec("systemctl reboot");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            return;
        }
        e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, "Restarting...")).queue();
        Corby.restart();
    }
}
