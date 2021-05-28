/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.util.List;

public class PingCommand extends Command {

  public PingCommand() {
    this.alias = "ping";
    this.description = "Provides the current ping of the bot";
    this.category = Category.MISC;
    this.usages = new String[] {"alias", "full"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {
    if (args.length > 1 && args[1].equals("full")) {
      StringBuilder stringBuilder = new StringBuilder();

      List<JDA> shards = Corby.getShards().getShards();

      if (shards.size() > 1) {
        for (int i = 0; i < shards.size(); i++) {
          stringBuilder
              .append(
                  OtherUtils.formatMessageKeyText(
                      "Shard " + (i + 1), shards.get(i).getGatewayPing() + "ms"))
              .append("\n");
        }
      }

      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.DEFAULT,
                  e.getAuthor(),
                  OtherUtils.formatMessageKeyText("Gateway ping", Corby.getPing())
                      + "\n\n"
                      + stringBuilder))
          .queue();
      return;
    }

    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT,
                e.getAuthor(),
                OtherUtils.formatMessageKeyText(
                    "Gateway ping", String.format("%sms", Corby.getPing())),
                e.getGuild()))
        .queue();
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length <= 2;
  }
}
