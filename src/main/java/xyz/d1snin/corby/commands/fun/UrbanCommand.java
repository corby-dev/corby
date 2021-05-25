/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.fun;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import jdk.nashorn.api.scripting.URLReader;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.net.MalformedURLException;
import java.net.URL;

public class UrbanCommand extends Command {

  public UrbanCommand() {
    this.alias = "urban";
    this.description = "Defines a word from *urbandictionary.com*.";
    this.category = Category.FUN;
    this.usages = new String[] {"<Phrase>"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {
    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT, e.getAuthor(), "Looking at the data...", e.getGuild()))
        .queue(
            message -> {
              JsonArray array;
              MessageEmbed errorMessage =
                  Embeds.create(
                      EmbedTemplate.ERROR,
                      e.getAuthor(),
                      "Could not find this word.",
                      e.getGuild());
              try {
                array =
                    JsonParser.parseReader(
                            new URLReader(
                                new URL(
                                    String.format(
                                        "https://api.urbandictionary.com/v0/define?term=%s",
                                        getArgsString(1, e.getMessage())
                                            .replaceAll("\\s+", "%20")))))
                        .getAsJsonObject()
                        .get("list")
                        .getAsJsonArray();
              } catch (MalformedURLException | JsonIOException exception) {
                message.editMessage(errorMessage).queue();
                return;
              }

              assert array != null;
              if (array.size() == 0) {
                message.editMessage(errorMessage).queue();
                return;
              }

              String definition =
                  array
                      .get(0)
                      .getAsJsonObject()
                      .get("definition")
                      .getAsString()
                      .replace("[", "")
                      .replace("]", "");

              message
                  .editMessage(
                      definition.length() > 2000
                          ? Embeds.create(
                              EmbedTemplate.ERROR,
                              e.getAuthor(),
                              String.format(
                                  "It seems the definition is too big, you can see it [here](%s).",
                                  array.get(0).getAsJsonObject().get("permalink").getAsString()),
                              e.getGuild())
                          : Embeds.create(
                              EmbedTemplate.SUCCESS,
                              e.getAuthor(),
                              String.format("**Definition:** %s", definition),
                              e.getGuild()))
                  .queue();
            });
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length > 1;
  }
}
