package xyz.d1snin.corby.commands.fun;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import com.github.bottomSoftwareFoundation.bottom.Bottom;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.Embeds;

public class BottomCommand extends Command {

    public BottomCommand() {
        this.aliases = "bottom";
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {

        final String result = "Result: %s";
        final String usage = "Please use the following syntax: `%sbottom <encode or decode> <your message, 2 - 30 characters>`";
        final String usageE = "Please use the following syntax: `%sbottom encode <your message, 2 - 30 characters>`";
        final String longR = "Sorry, generated result is too long.";

        if (args.length < 3) {
            e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(usage, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                    .queue((message1 -> message1.addReaction(Corby.config.emote_trash).queue()));
            return;
        }

        final String message = e.getMessage().getContentRaw().substring(GuildSettingsManager.getGuildPrefix(e.getGuild()).length() + 14);

        switch (args[1]) {
            case "encode":

                String encodedMessage = Bottom.encode(message);


                if (encodedMessage.length() > 200) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, longR))
                            .queue((message1 -> message1.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                if (message.length() > 50 || message.length() < 2) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(usageE, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                            .queue((message1 -> message1.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, String.format(result, encodedMessage))).queue();

                break;

            case "decode":

                String decodedMessage = Bottom.decode(message);

                if (decodedMessage.length() > 200) {
                    e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, longR))
                            .queue((message1 -> message1.addReaction(Corby.config.emote_trash).queue()));
                    return;
                }

                e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, String.format(result, decodedMessage))).queue();

                break;

            default:
                e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(usage, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                        .queue((message1 -> message1.addReaction(Corby.config.emote_trash).queue()));
        }
    }
}
