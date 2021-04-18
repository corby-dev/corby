package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.ColorUtil;

import java.util.Arrays;

public class PingCommand extends Command {

    public PingCommand() {
        this.aliases = Arrays.asList("ping", "hello?");
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {
        e.getTextChannel().sendMessage(new EmbedBuilder()
                .setColor(ColorUtil.getDefaultColor())
                .setDescription("Current ping: " + Corby.getAPI().getGatewayPing() + "ms")
                .setFooter(e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(), e.getAuthor().getEffectiveAvatarUrl())
                .build()).queue();
    }
}
