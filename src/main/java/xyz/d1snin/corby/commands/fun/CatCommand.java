package xyz.d1snin.corby.commands.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.JSONReader;

import java.net.MalformedURLException;
import java.net.URL;

public class CatCommand extends Command {

  public CatCommand() {
    this.use = "cat";
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {

    JSONReader reader = new JSONReader();

    e.getTextChannel()
        .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), "Fetching..."))
        .queue(
            message -> {
              try {
                message
                    .editMessage(
                        new EmbedBuilder()
                            .setDescription("Here is your cat ФwФ")
                            .setColor(Corby.config.defaultColor)
                            .setImage(
                                reader.readFromURL(
                                    "url", new URL("https://api.thecatapi.com/v1/images/search")))
                            .setFooter(
                                e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(),
                                e.getAuthor().getEffectiveAvatarUrl())
                            .build())
                    .queue();
              } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
              }
            });
  }
}
