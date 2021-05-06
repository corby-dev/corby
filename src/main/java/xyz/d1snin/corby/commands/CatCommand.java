/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.simple.parser.ParseException;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.utils.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.JSONReader;

import java.io.IOException;
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
                                .setDescription(
                                    "**Fact about cats:** "
                                        + reader.readFromURL(
                                            "text",
                                            new URL(
                                                "https://cat-fact.herokuapp.com/facts/random?animal_type=cat&amount=1"),
                                            true))
                                .setColor(Corby.config.defaultColor)
                                .setImage(
                                    reader.readFromURL(
                                        "url",
                                        new URL("https://api.thecatapi.com/v1/images/search"),
                                        false))
                                .setFooter(
                                    e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(),
                                    e.getAuthor().getEffectiveAvatarUrl())
                                .build())
                        .queue();
                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }
            });
  }
}
