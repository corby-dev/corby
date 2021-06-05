/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2021, Corby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.MongoPrefixManager;
import xyz.d1snin.corby.model.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.util.List;
import java.util.Objects;

public class ServerJoinEvent extends Listener {

  public ServerJoinEvent() {
    this.event = GuildJoinEvent.class;
  }

  @Override
  public void perform(GenericGuildEvent event) {

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
