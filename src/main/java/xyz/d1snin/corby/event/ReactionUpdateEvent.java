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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ReactionUpdateEvent extends ListenerAdapter {

  private static final Set<MessageReaction> executed = new CopyOnWriteArraySet<>();

  @Override
  public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
    try {

      if (event.getReaction().getReactionEmote().getName().equals(Corby.config.emoteTrash)
          && !event.getReaction().isSelf()
          && !executed.contains(event.getReaction())) {
        if (event.retrieveMessage().complete().getReactions().get(0).isSelf()) {
          event.retrieveMessage().queue((message -> message.delete().queue()));
          executed.add(event.getReaction());
        }
        return;
      }

      if (event.getReaction().getReactionEmote().getName().equals(Corby.config.emoteStar)) {
        if (GuildSettingsManager.getGuildStarboardChannel(event.getGuild()) == null) return;
        if (!GuildSettingsManager.getGuildStarboardIsEnabled(event.getGuild())) return;
        Message msg = event.retrieveMessage().complete();
        if (msg.getAuthor().getId().equals(Corby.config.id)) return;
        MessageReaction reaction = msg.getReactions().get(0);
        for (MessageReaction r : msg.getReactions()) {
          if (r.getReactionEmote().getName().equals(Corby.config.emoteStar)) {
            reaction = r;
          }
        }
        if (reaction.getCount() == GuildSettingsManager.getGuildStarboardStars(event.getGuild())
            && !executed.contains(reaction)) {
          Objects.requireNonNull(GuildSettingsManager.getGuildStarboardChannel(event.getGuild()))
              .sendMessage(
                  new EmbedBuilder()
                      .setAuthor(
                          msg.getAuthor().getAsTag(),
                          msg.getJumpUrl(),
                          msg.getAuthor().getAvatarUrl())
                      .setDescription(
                          "[[context]]("
                              + msg.getJumpUrl()
                              + ")"
                              + "\n\n"
                              + msg.getContentRaw()
                              + "\n"
                              + (msg.getAttachments().isEmpty()
                                  ? ""
                                  : msg.getAttachments().get(0).getUrl()))
                      .setTimestamp(Instant.now())
                      .setColor(Corby.config.starboardColor)
                      .setFooter(Corby.config.botName, Corby.config.botPfpUrl)
                      .build())
              .queue();
          executed.add(reaction);
        }
      }

    } catch (IndexOutOfBoundsException ignored) {
    }
  }
}
