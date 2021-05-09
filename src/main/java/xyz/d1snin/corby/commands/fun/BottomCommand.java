/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.fun;

import com.github.bottomSoftwareFoundation.bottom.TranslationError;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.github.bottomSoftwareFoundation.bottom.Bottom;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.sql.SQLException;

public class BottomCommand extends Command {

  public BottomCommand() {
    this.alias = "bottom";
    this.description = "Encrypts your message using a bottom cipher";
    this.category = Category.FUN;
    this.usages = new String[] {"%sbottom encode <Message>", "%sbottom decode <Message>"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws SQLException {
    try {
      final int edMsgLimit = 300;
      final int msgLimit1 = 2;
      final int msgLimit2 = 200;

      final String result =
          "**Result:**\n\n%s\n\nPowered by [bottom-software-foundation](https://github.com/bottom-software-foundation/bottom-java).";
      final String usage =
          "Please use the following syntax: `%sbottom <encode or decode> <your message>`";
      final String usageE =
          "Please use the following syntax: `%sbottom encode <your message, %d  - %d characters>`";
      final String longR = "Sorry, generated result is too long.";

      if (args.length < 3) {
        e.getTextChannel()
            .sendMessage(
                Embeds.create(
                    EmbedTemplate.ERROR,
                    e.getAuthor(),
                    String.format(usage, PrefixManager.getPrefix(e.getGuild()))))
            .queue();
        return;
      }

      final String message =
          e.getMessage()
              .getContentRaw()
              .substring(PrefixManager.getPrefix(e.getGuild()).length() + 14);

      switch (args[1]) {
        case "encode":
          String encodedMessage = Bottom.encode(message);

          if (encodedMessage.length() > edMsgLimit) {
            e.getTextChannel()
                .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), longR))
                .queue();
            return;
          }

          if (message.length() > msgLimit2 || message.length() < msgLimit1) {
            e.getTextChannel()
                .sendMessage(
                    Embeds.create(
                        EmbedTemplate.ERROR,
                        e.getAuthor(),
                        String.format(
                            usageE, PrefixManager.getPrefix(e.getGuild()), msgLimit1, msgLimit2)))
                .queue();
            return;
          }

          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.DEFAULT, e.getAuthor(), String.format(result, encodedMessage)))
              .queue();

          break;

        case "decode":
          String decodedMessage = Bottom.decode(message);

          if (decodedMessage.length() > edMsgLimit) {
            e.getTextChannel()
                .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), longR))
                .queue();
            return;
          }

          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.DEFAULT, e.getAuthor(), String.format(result, decodedMessage)))
              .queue();

          break;

        default:
          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.ERROR,
                      e.getAuthor(),
                      String.format(usage, PrefixManager.getPrefix(e.getGuild()))))
              .queue();
      }
    } catch (TranslationError exception) {

      final String tErr = "You cannot decrypt this message.";

      e.getTextChannel()
          .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), tErr))
          .queue();
    }
  }
}
