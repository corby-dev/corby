package xyz.d1snin.corby.commands.fun;

import com.github.bottomSoftwareFoundation.bottom.TranslationError;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import com.github.bottomSoftwareFoundation.bottom.Bottom;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

public class BottomCommand extends Command {

    public BottomCommand() {
        this.aliases = "bottom";
    }

    @Override
    protected void execute(MessageReceivedEvent e, String[] args) {
        try {

            final int edMsgLimit = 300;
            final int msgLimit1 = 2;
            final int msgLimit2 = 200;

            final String result = "**Result:**\n\n%s\n\nPowered by [bottom-software-foundation](https://github.com/bottom-software-foundation/bottom-java).";
            final String usage = "Please use the following syntax: `%sbottom <encode or decode> <your message>`";
            final String usageE = "Please use the following syntax: `%sbottom encode <your message, %d  - %d characters>`";
            final String longR = "Sorry, generated result is too long.";

            if (args.length < 3) {
                Embeds.createAndSendWithReaction(EmbedTemplate.ERROR, e.getAuthor(), e.getTextChannel(), Corby.config.emote_trash,
                        String.format(usage, GuildSettingsManager.getGuildPrefix(e.getGuild())));
                return;
            }

            final String message = e.getMessage().getContentRaw().substring(GuildSettingsManager.getGuildPrefix(e.getGuild()).length() + 14);

            switch (args[1]) {
                case "encode":

                    String encodedMessage = Bottom.encode(message);


                    if (encodedMessage.length() > edMsgLimit) {
                        Embeds.createAndSendWithReaction(EmbedTemplate.ERROR, e.getAuthor(), e.getTextChannel(), Corby.config.emote_trash, longR);
                        return;
                    }

                    if (message.length() > msgLimit2 || message.length() < msgLimit1) {
                        Embeds.createAndSendWithReaction(EmbedTemplate.ERROR, e.getAuthor(), e.getTextChannel(), Corby.config.emote_trash,
                                String.format(usageE, GuildSettingsManager.getGuildPrefix(e.getGuild()), msgLimit1, msgLimit2));
                        return;
                    }

                    e.getTextChannel().sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), String.format(result, encodedMessage))).queue();

                    break;

                case "decode":

                    String decodedMessage = Bottom.decode(message);

                    if (decodedMessage.length() > edMsgLimit) {
                        Embeds.createAndSendWithReaction(EmbedTemplate.ERROR, e.getAuthor(), e.getTextChannel(), Corby.config.emote_trash, longR);
                        return;
                    }

                    e.getTextChannel().sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), String.format(result, decodedMessage))).queue();

                    break;

                default:
                    Embeds.createAndSendWithReaction(EmbedTemplate.ERROR, e.getAuthor(), e.getTextChannel(), Corby.config.emote_trash,
                            String.format(usage, GuildSettingsManager.getGuildPrefix(e.getGuild())));
            }
        } catch (TranslationError exception) {

            final String tErr = "You cannot decrypt this message.";

            Embeds.createAndSendWithReaction(EmbedTemplate.ERROR, e.getAuthor(), e.getTextChannel(), Corby.config.emote_trash, tErr);
        }
    }
}
