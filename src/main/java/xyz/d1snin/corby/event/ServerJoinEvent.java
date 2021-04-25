package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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
        GuildChannel channel = event.getGuild().getSystemChannel() == null ? channels.get(channels.size() - 1) : event.getGuild().getSystemChannel();

        if (!Objects.requireNonNull(event.getGuild().getBotRole()).getPermissions().contains(Permission.ADMINISTRATOR)) {
            ((TextChannel) channel).sendMessage(new EmbedBuilder()
            .setColor(Corby.config.default_color)
            .setDescription("It looks like you added me to your server without admin rights, this is necessary for the bot to work correctly, please invite me using this [link](" + Corby.config.invite_url + "). I will log out of your server now.")
            .setFooter(Corby.config.bot_name, Corby.config.bot_pfp_url).build()).queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
            event.getGuild().leave().queue();
            return;
        }

        ((TextChannel) channel).sendMessage(new EmbedBuilder()
                .setColor(Corby.config.default_color)
                .setDescription("Thank you for inviting me to your server!"
                        + "\nI can help you with moderation and administration of your server and much more."
                        + "\nYou can find out the full list of commands by simply writing to any chat `" + Corby.config.bot_prefix_default + "help`, you can change the prefix with the command `" + Corby.config.bot_prefix_default + "prefix`")
                .setFooter(Corby.config.bot_name, Corby.config.bot_pfp_url).build()).queue((message -> message.addReaction(Corby.config.emote_trash).queue()));
    }
}
