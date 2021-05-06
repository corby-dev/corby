package xyz.d1snin.corby.commands.misc;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;

public class StealCommand extends Command {

  public StealCommand() {
    this.use = "steal";
    this.permissions = new Permission[] {Permission.MANAGE_EMOTES};
    this.botPermissions = new Permission[] {Permission.MANAGE_EMOTES};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) {
    MessageChannel channel = e.getChannel();
    if (args.length == 3) {
      String name = args[2];
      if (name.length() <= 32 && name.length() >= 2) {
        try {
          URL url = new URL(args[1]);

          URLConnection connection = url.openConnection();

          // To fix discord weirdness
          connection.setRequestProperty("User-Agent", "");

          InputStream stream = connection.getInputStream();

          e.getGuild().createEmote(name, Icon.from(stream)).queue(null, fail -> {
            channel.sendMessage(fail.getMessage()).queue();
          });
        } catch (IOException ioException) {
          throw new UncheckedIOException(ioException);
        }
      } else {
        final String nameSizeMessage = "Name must be between (inclusive) 2 and 32 characters in length.";
        channel.sendMessage(nameSizeMessage).queue();
      }
    } else {
      final String argumentAmountMessage = "Too many or too few arguments!";
      channel.sendMessage(argumentAmountMessage).queue();
    }
  }
}
