package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.database.managers.GuildSettingsManager;

import java.time.Instant;
import java.util.Objects;

public class ReactionUpdateEvent extends ListenerAdapter {
    @Override
    public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
        try {

            if (event.getReaction().getReactionEmote().getName().equals(Corby.config.emote_trash)
                    && !event.getReaction().isSelf()) {
                if (event.retrieveMessage().complete().getReactions().get(0).isSelf()) {
                    event.retrieveMessage().queue((message -> message.delete().queue()));
                }
                return;
            }
            if (event.getReaction().getReactionEmote().getName().equals(Corby.config.emote_star)) {

                if (GuildSettingsManager.getGuildStarboardChannel(event.getGuild()) == null) return;
                if (!GuildSettingsManager.getGuildStarboardIsEnabled(event.getGuild())) return;

                Message msg = event.retrieveMessage().complete();
                MessageReaction reaction = msg.getReactions().get(0);
                
                if(msg.getAuthor().getAsTag().equals("Corby#3833")) return; // <- stops from starboarding itself idk if its useful or if theres a way to check the current name without hardcoding it but idk how jda works

                for (MessageReaction r : msg.getReactions()) {
                    if (r.getReactionEmote().getName().equals(Corby.config.emote_star)) {
                        reaction = r;
                    }
                }

                if (reaction.getCount() == GuildSettingsManager.getGuildStarboardStars(event.getGuild())) {
                    Objects.requireNonNull(GuildSettingsManager.getGuildStarboardChannel(event.getGuild())).sendMessage(new EmbedBuilder()
                            .setAuthor(msg.getAuthor().getAsTag(), msg.getJumpUrl(), msg.getAuthor().getAvatarUrl())
                            .setDescription("[[context]](" + msg.getJumpUrl() + ")" +
                                    "\n\n" + msg.getContentRaw() +
                                    "\n" + (msg.getAttachments().isEmpty() ? "" : msg.getAttachments().get(0).getUrl()))
                            .setTimestamp(Instant.now())
                            .setColor(Corby.config.starboard_color)
                            .setFooter(Corby.config.bot_name, Corby.config.bot_pfp_url).build()).queue();
                }
            }
        } catch (IndexOutOfBoundsException ignored) {}
    }
}
