package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.d1snin.corby.Corby;

public class ReactionUpdateEvent extends ListenerAdapter {
    @Override
    public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
        if (event.getReaction().getReactionEmote().getName().equals(Corby.config.emote_trash)
                && !event.getReaction().isSelf()) {
            event.retrieveMessage().queue((message -> message.delete().queue()));
        }
    }
}
