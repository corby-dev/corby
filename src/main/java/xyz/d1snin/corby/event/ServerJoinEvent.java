package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.d1snin.corby.Corby;

import java.util.List;
import java.util.Objects;

public class ServerJoinEvent extends ListenerAdapter {
  @Override
  public void onGuildJoin(GuildJoinEvent event) {

    List<GuildChannel> channels = event.getGuild().getChannels();
    GuildChannel channel =
        event.getGuild().getSystemChannel() == null
            ? channels.get(channels.size() - 1)
            : event.getGuild().getSystemChannel();

    if (!Objects.requireNonNull(event.getGuild().getBotRole())
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
      event.getGuild().leave().queue();
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
