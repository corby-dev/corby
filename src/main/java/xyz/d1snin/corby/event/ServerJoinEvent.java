/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.util.List;
import java.util.Objects;

public class ServerJoinEvent extends Listener {

  public ServerJoinEvent() {
    this.event = GuildJoinEvent.class;
  }

  @Override
  public void perform(GenericEvent event) {

    GuildJoinEvent thisEvent = ((GuildJoinEvent) event);

    List<GuildChannel> channels = thisEvent.getGuild().getChannels();
    GuildChannel channel =
        thisEvent.getGuild().getSystemChannel() == null
            ? channels.get(channels.size() - 1)
            : thisEvent.getGuild().getSystemChannel();

    if (!Objects.requireNonNull(thisEvent.getGuild().getBotRole())
        .getPermissions()
        .containsAll(Corby.getPermissions())) {
      ((TextChannel) channel)
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.DEFAULT,
                  Corby.getFirstJda().getSelfUser(),
                  String.format(
                      "It looks like you added me to your server without required permissions, this is necessary for the bot to work correctly, please invite me using this [link](%s). I will log out of your server now.",
                      Corby.getConfig().getInviteUrl())))
          .queue();
      thisEvent.getGuild().leave().queue();
      return;
    }

    ((TextChannel) channel)
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT,
                Corby.getFirstJda().getSelfUser(),
                String.format(
                    "Thank you for inviting me to your server!\nI can help you with moderation and administration of your server and much more. \nYou can find out the full list of commands by simply writing to any chat `%shelp`.",
                    MongoPrefixManager.getPrefix(thisEvent.getGuild()))))
        .queue();
  }
}
