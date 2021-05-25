/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.fun;

import com.github.bottomSoftwareFoundation.bottom.Bottom;
import com.github.bottomSoftwareFoundation.bottom.TranslationError;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

public class BottomCommand extends Command {

  public BottomCommand() {
    this.alias = "bottom";
    this.description = "Encrypts your message using a bottom cipher";
    this.category = Category.FUN;
    this.usages =
        new String[] {"encode <Message 2 - 200 characters>", "decode <Message 2 - 200 characters>"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {
    try {
      final int edMsgLimit = 300;

      final String result =
          "**Result:**\n\n%s\n\nPowered by [bottom-software-foundation](https://github.com/bottom-software-foundation/bottom-java).";
      final String longR = "Sorry, generated result is too long.";

      final String message =
          e.getMessage()
              .getContentRaw()
              .substring(MongoPrefixManager.getPrefix(e.getGuild()).getPrefix().length() + 14);

      switch (args[1]) {
        case "encode":
          String encodedMessage = Bottom.encode(message);

          if (encodedMessage.length() > edMsgLimit) {
            e.getTextChannel()
                .sendMessage(
                    Embeds.create(
                        EmbedTemplate.ERROR, e.getAuthor(), longR, e.getGuild(), null, null))
                .queue();
            return;
          }

          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.SUCCESS,
                      e.getAuthor(),
                      String.format(result, encodedMessage),
                      e.getGuild(),
                      null,
                      null))
              .queue();

          break;

        case "decode":
          String decodedMessage = Bottom.decode(message);

          if (decodedMessage.length() > edMsgLimit) {
            e.getTextChannel()
                .sendMessage(
                    Embeds.create(
                        EmbedTemplate.ERROR, e.getAuthor(), longR, e.getGuild(), null, null))
                .queue();
            return;
          }

          e.getTextChannel()
              .sendMessage(
                  Embeds.create(
                      EmbedTemplate.SUCCESS,
                      e.getAuthor(),
                      String.format(result, decodedMessage),
                      e.getGuild(),
                      null,
                      null))
              .queue();

          break;

        default:
      }
    } catch (TranslationError exception) {

      final String tErr = "You cannot decrypt this message.";

      e.getTextChannel()
          .sendMessage(
              Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), tErr, e.getGuild(), null, null))
          .queue();
    }
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    if (args.length < 3) {
      return false;
    }

    return getMessageContent().length() <= 200 && getMessageContent().length() >= 2;
  }
}
