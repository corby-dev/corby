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
import xyz.d1snin.corby.database.managers.GuildSettingsManager;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

public class HelpCommand extends Command {

  public HelpCommand() {
    this.use = "help";
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {
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
                    + GuildSettingsManager.getGuildPrefix(e.getGuild())
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
            Embeds.createAndSendWithReaction(
                EmbedTemplate.ERROR,
                e.getAuthor(),
                e.getTextChannel(),
                Corby.config.emoteTrash,
                "Unable to send you a message, make sure you accept messages from server members."),
        () -> e.getMessage().addReaction(Corby.config.emoteWhiteCheckMark).queue());
  }
}
