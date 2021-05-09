/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.database.managers.PrefixManager;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;

public class StealCommand extends Command {

  public StealCommand() {
    this.alias = "steal";
    this.description = "Uploads emoji from a link or other emoji to your server";
    this.category = Category.MISC;
    this.usages = new String[] {"%ssteal <URL> <Name>", "%ssteal <Emoji> <Name>"};

    this.permissions = new Permission[] {Permission.MANAGE_EMOTES};
    this.botPermissions = new Permission[] {Permission.MANAGE_EMOTES};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws IOException, SQLException {

    final String usage = "Please use the following syntax: `%ssteal <URL or emote> <name>`";
    final String nameSizeMessage = "Name must be between 1 and 32 characters in length.";
    final String invalidUrl = "Provided URL is invalid.";
    final String success = "The emote `:%s:` has been successfully added!";
    final String failure = "Something went wrong while adding an emote, please try again.";
    final String incorrectUrl = "This format is not supported.";

    final List<Emote> emotes = e.getMessage().getEmotes();

    if (args.length < 3) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.ERROR,
                  e.getAuthor(),
                  String.format(usage, PrefixManager.getPrefix(e.getGuild()))))
          .queue();
      return;
    }

    final String name = args[2].toLowerCase();

    if (name.length() > 32 || name.length() < 1) {
      e.getTextChannel()
          .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), nameSizeMessage))
          .queue();
      return;
    }

    URL url;

    if (emotes.isEmpty()) {

      if (!args[1].endsWith(".jpg") && !args[1].endsWith(".png") && !args[1].endsWith(".jpeg")) {
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), incorrectUrl))
            .queue();
        return;
      }

      try {
        url = new URL(args[1]);
      } catch (MalformedURLException malformedURLException) {
        e.getTextChannel()
            .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), invalidUrl))
            .queue();
        return;
      }
    } else {
      url = new URL(emotes.get(0).getImageUrl());
    }

    URLConnection connection = url.openConnection();
    connection.setRequestProperty("User-Agent", "");

    try (InputStream stream = connection.getInputStream()) {
      e.getGuild()
          .createEmote(name, Icon.from(stream))
          .queue(
              successfully ->
                  e.getTextChannel()
                      .sendMessage(
                          Embeds.create(
                              EmbedTemplate.DEFAULT, e.getAuthor(), String.format(success, name)))
                      .queue(),
              fail ->
                  e.getTextChannel()
                      .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), failure))
                      .queue());
    } catch (FileNotFoundException exception) {
      e.getTextChannel()
          .sendMessage(Embeds.create(EmbedTemplate.ERROR, e.getAuthor(), invalidUrl))
          .queue();
    }
  }
}
