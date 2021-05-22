package xyz.d1snin.corby.commands.fun;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import jdk.nashorn.api.scripting.URLReader;
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
    this.description = "defines a word from *urbandictionary.com*.";
    this.category = Category.FUN;
    this.usages = new String[] {"%surban <Word>"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws MalformedURLException {
    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT, e.getAuthor(), "Looking at the data...", e.getGuild(), null))
        .queue(
            message -> {
              JsonArray array = null;
              try {
                array =
                    JsonParser.parseReader(
                            new URLReader(
                                new URL(
                                    String.format(
                                        "https://api.urbandictionary.com/v0/define?term=%s",
                                        args[1]))))
                        .getAsJsonObject()
                        .get("list")
                        .getAsJsonArray();
              } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
              }

              assert array != null;
              if (array.size() == 0) {
                message
                    .editMessage(
                        Embeds.create(
                            EmbedTemplate.ERROR,
                            e.getAuthor(),
                            "Could not find this word.",
                            e.getGuild(),
                            null))
                    .queue();
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
                      Embeds.create(
                          EmbedTemplate.SUCCESS,
                          e.getAuthor(),
                          String.format("**Definition:** %s", definition),
                          e.getGuild(),
                          null))
                  .queue();
            });
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length == 2;
  }
}
