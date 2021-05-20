package xyz.d1snin.corby.commands.fun;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.api.scripting.URLReader;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;

import java.net.MalformedURLException;
import java.net.URL;

public class BitcoinCommand extends Command {

  public BitcoinCommand() {
    this.alias = "bitcoin";
    this.description = "Gets information about the price of bitcoin.";
    this.category = Category.FUN;
    this.usages = new String[] {"%sbitcoin", "%sbitcoin <Your Fiat Currency (Example: USD, RUB)>"};
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws MalformedURLException {
    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT, e.getAuthor(), "Looking at the data...", e.getGuild(), null))
        .queue(
            message -> {
              JsonElement root = null;
              try {
                root =
                    JsonParser.parseReader(
                        new URLReader(new URL("https://blockchain.info/en/ticker")));
              } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
              }

              assert root != null;
              JsonElement currency =
                  (args.length > 1
                      ? root.getAsJsonObject().get(args[1].toUpperCase())
                      : root.getAsJsonObject().get("USD"));

              if (currency == null) {
                message
                    .editMessage(
                        Embeds.create(
                            EmbedTemplate.ERROR,
                            e.getAuthor(),
                            String.format(
                                "Could not find this currency: %s", args[1].toUpperCase()),
                            e.getGuild(),
                            null))
                    .queue();
                return;
              }

              JsonObject infoByCurrency = currency.getAsJsonObject();

              String price = infoByCurrency.get("last").getAsString();
              String currencySymbol = infoByCurrency.get("symbol").getAsString();

              message
                  .editMessage(
                      Embeds.create(
                          EmbedTemplate.SUCCESS,
                          e.getAuthor(),
                          String.format(
                              "Bitcoin price now in %s: **%s %s**",
                              args.length > 1 ? args[1].toUpperCase() : "USD",
                              price,
                              currencySymbol),
                          e.getGuild(),
                          null))
                  .queue();
            });
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length < 3;
  }
}
