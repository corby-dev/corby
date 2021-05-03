/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.annotation.EventListener;

import java.util.List;
import java.util.Objects;

@EventListener(event = GuildJoinEvent.class)
public class ServerJoinEvent extends Listener {

  @Override
  protected void perform(GenericEvent event) {

    GuildJoinEvent thisEvent = ((GuildJoinEvent) event);

    List<GuildChannel> channels = thisEvent.getGuild().getChannels();
    GuildChannel channel =
        thisEvent.getGuild().getSystemChannel() == null
            ? channels.get(channels.size() - 1)
            : thisEvent.getGuild().getSystemChannel();

    if (!Objects.requireNonNull(thisEvent.getGuild().getBotRole())
        .getPermissions()
        .containsAll(Corby.permissions)) {
      ((TextChannel) channel)
          .sendMessage(
              new EmbedBuilder()
                  .setColor(Corby.config.defaultColor)
                  .setDescription(
                      "It looks like you added me to your server without required rights, this is necessary for the bot to work correctly, please invite me using this [link]("
                          + Corby.config.inviteUrl
                          + "). I will log out of your server now.")
                  .setFooter(Corby.config.botName, Corby.config.botPfpUrl)
                  .build())
          .queue((message -> message.addReaction(Corby.config.emoteTrash).queue()));
      thisEvent.getGuild().leave().queue();
      return;
    }

    ((TextChannel) channel)
        .sendMessage(
            new EmbedBuilder()
                .setColor(Corby.config.defaultColor)
                .setDescription(
                    "Thank you for inviting me to your server!"
                        + "\nI can help you with moderation and administration of your server and much more."
                        + "\nYou can find out the full list of commands by simply writing to any chat `"
                        + Corby.config.botPrefixDefault
                        + "help`, you can change the prefix with the command `"
                        + Corby.config.botPrefixDefault
                        + "prefix`")
                .setFooter(Corby.config.botName, Corby.config.botPfpUrl)
                .build())
        .queue((message -> message.addReaction(Corby.config.emoteTrash).queue()));
  }
}
