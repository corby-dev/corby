/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.fun;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jdk.nashorn.api.scripting.URLReader;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.net.MalformedURLException;
import java.net.URL;

public class CatCommand extends Command {

  public CatCommand() {
    this.alias = "cat";
    this.description = "Gives you a picture of a cat";
    this.category = Category.FUN;
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {
    e.getTextChannel()
        .sendMessage(
            Embeds.create(EmbedTemplate.DEFAULT, e.getAuthor(), "Fetching...", e.getGuild(), null))
        .queue(
            message -> {
              try {
                message
                    .editMessage(
                        Embeds.create(
                            EmbedTemplate.DEFAULT,
                            e.getAuthor(),
                            getFact(),
                            e.getGuild(),
                            getPicture()))
                    .queue();
              } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
              }
            });
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length <= 1;
  }

  private String getFact() throws MalformedURLException {
    while (true) {
      JsonElement root =
          JsonParser.parseReader(
              new URLReader(
                  new URL("https://cat-fact.herokuapp.com/facts/random?animal_type=cat&amount=1")));
      JsonElement verifiedElement =
          root.getAsJsonObject().get("status").getAsJsonObject().get("verified");
      if (!verifiedElement.isJsonNull() && verifiedElement.getAsBoolean()) {
        return root.getAsJsonObject().get("text").getAsString();
      }
    }
  }

  private String getPicture() throws MalformedURLException {
    return JsonParser.parseReader(
            new URLReader(new URL("https://api.thecatapi.com/v1/images/search")))
        .getAsJsonArray()
        .get(0)
        .getAsJsonObject()
        .get("url")
        .getAsString();
  }
}
