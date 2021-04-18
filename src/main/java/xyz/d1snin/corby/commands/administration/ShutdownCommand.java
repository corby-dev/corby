package xyz.d1snin.corby.commands.administration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.ColorUtil;

import java.util.Arrays;

public class ShutdownCommand extends Command {

    public ShutdownCommand() {
        this.admincommand = true;
        this.aliases = Arrays.asList("shut", "shutdown", "bye", "goodnight", "term");
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {
        Thread t = new Thread(() -> {
            e.getTextChannel().sendMessage(new EmbedBuilder()
                    .setColor(ColorUtil.getDefaultColor())
                    .setDescription("Terminating... Bye!")
                    .setFooter(e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(), e.getAuthor().getEffectiveAvatarUrl())
                    .build()).queue();
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        Corby.shutdown();
    }
}
