/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.simple.parser.ParseException;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.JSONReader;

import java.io.IOException;
import java.net.URL;

public class CatCommand extends Command {

  public CatCommand() {
    this.alias = "cat";
    this.description = "Gives you a picture of a cat";
    this.category = Category.FUN;
    this.usages = new String[] {"%scat"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws IOException, ParseException {

    JSONReader reader = new JSONReader();

    e.getTextChannel()
        .sendMessage(Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), "Fetching..."))
        .queue(
            message -> {
              try {
                message
                    .editMessage(
                        new EmbedBuilder()
                            .setAuthor(
                                e.getAuthor().getName() + " | ID: " + e.getAuthor().getId(),
                                e.getAuthor().getEffectiveAvatarUrl(),
                                e.getAuthor().getEffectiveAvatarUrl())
                            .setDescription(
                                reader.readFromURL(
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
                                Corby.config.botName + " | " + Thread.currentThread().getName(),
                                Corby.config.botPfpUrl)
                            .build())
                    .queue();
              } catch (IOException | ParseException ioException) {
                ioException.printStackTrace();
              }
            });
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length <= 1;
  }
}
