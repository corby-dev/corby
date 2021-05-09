/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.sql.SQLException;

public class HelpCommand extends Command {

  public HelpCommand() {
    this.alias = "help";
    this.description = "Gives you information about commands.";
//    this.category = Category.MISC;
    this.usages = new String[] {"%help", "%shelp <Command Name>"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws SQLException {
    OtherUtils.sendPrivateMessageSafe(
        e.getAuthor(),
        new EmbedBuilder()
            .setAuthor(
                e.getGuild().getName(),
                Corby.config.helpPageUrl,
                e.getGuild().getIconUrl() == null
                    ? "https://media.discordapp.net/attachments/835925114700300380/836291623885340723/iu.png"
                    : e.getGuild().getIconUrl())
            .setDescription(
                "**Server:** "
                    + e.getGuild().getName()
                    + "\n**Prefix for commands on this server:** `"
                    + PrefixManager.getPrefix(e.getGuild())
                    + "`"
                    + "\n[Commands list]("
                    + Corby.config.helpPageUrl
                    + ")"
                    + "\n[Invite me on your server!]("
                    + Corby.config.inviteUrl
                    + ")")
            .setColor(Corby.config.defaultColor)
            .build(),
        () ->
            e.getTextChannel()
                .sendMessage(
                    Embeds.create(
                        EmbedTemplate.ERROR,
                        e.getAuthor(),
                        "Unable to send you a message, make sure you accept messages from server members."))
                .queue(),
        () -> e.getMessage().addReaction(Corby.config.emoteWhiteCheckMark).queue());
  }
}
