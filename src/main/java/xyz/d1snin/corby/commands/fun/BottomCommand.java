package xyz.d1snin.corby.commands.fun;

import com.github.bottomSoftwareFoundation.bottom.TranslationError;
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
        try {

            final int edMsgLimit = 1900;
            final int msgLimit1 = 2;
            final int msgLimit2 = 800;

            final String result = "**Result:**\n%s\nPowered by [bottom-software-foundation](https://github.com/bottom-software-foundation/bottom-java).";
            final String usage = "Please use the following syntax: `%sbottom <encode or decode> <your message>`";
            final String usageE = "Please use the following syntax: `%sbottom encode <your message, %d  - %d characters>`";
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


                    if (encodedMessage.length() > edMsgLimit) {
                        e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, longR))
                                .queue((message1 -> message1.addReaction(Corby.config.emote_trash).queue()));
                        return;
                    }

                    if (message.length() > msgLimit2 || message.length() < msgLimit1) {
                        e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, String.format(usageE, GuildSettingsManager.getGuildPrefix(e.getGuild()))))
                                .queue((message1 -> message1.addReaction(Corby.config.emote_trash).queue()));
                        return;
                    }

                    e.getTextChannel().sendMessage(Embeds.createDefaultEmbed(e, String.format(result, encodedMessage))).queue();

                    break;

                case "decode":

                    String decodedMessage = Bottom.decode(message);

                    if (decodedMessage.length() > edMsgLimit) {
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
        } catch (TranslationError exception) {

            final String tErr = "You cannot decrypt this message.";

            e.getTextChannel().sendMessage(Embeds.createDefaultErrorEmbed(e, tErr))
                    .queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
        }
    }
}
