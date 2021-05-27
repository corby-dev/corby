/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.commands.fun;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.api.scripting.URLReader;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.d1snin.corby.commands.Command;
import xyz.d1snin.corby.enums.Category;
import xyz.d1snin.corby.enums.EmbedTemplate;
import xyz.d1snin.corby.utils.Embeds;
import xyz.d1snin.corby.utils.OtherUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class CoinCommand extends Command {

  private static JsonArray array;

  public CoinCommand() {
    this.alias = "coin";
    this.description = "Searches for information about cryptocurrency among 7000+ coins!";
    this.category = Category.FUN;
    this.usages = new String[] {"<Cryptocurrency Coin (Example: btc, eth)>"};
  }

  private static String getId(String targetName, String query) {
    for (JsonElement element : array) {
      if (element.getAsJsonObject().get(targetName).getAsString().toLowerCase().equals(query)) {
        return element.getAsJsonObject().get("id").getAsString();
      }
    }
    return null;
  }

  private static JsonObject getCoin(String id) throws MalformedURLException {
    return JsonParser.parseReader(
            new URLReader(new URL("https://api.coingecko.com/api/v3/coins/" + id)))
        .getAsJsonObject();
  }

  private static JsonArray getJsonArray() throws MalformedURLException {
    return JsonParser.parseReader(
            new URLReader(new URL("https://api.coingecko.com/api/v3/coins/list")))
        .getAsJsonArray();
  }

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws MalformedURLException {
    array = getJsonArray();

    String coinId = null;
    String[] targets = new String[] {"symbol", "id"};
    for (String s : targets) {
      String id;
      id = getId(s, args[1].toLowerCase());
      if (id == null) {
        continue;
      }
      coinId = id;
    }

    if (coinId == null) {
      e.getTextChannel()
          .sendMessage(
              Embeds.create(
                  EmbedTemplate.ERROR,
                  e.getAuthor(),
                  String.format(
                      "We searched among %d coins but did not find any matches.", array.size()),
                  e.getGuild()))
          .queue();
      return;
    }
    JsonObject coin = null;
    try {
      coin = getCoin(coinId);
    } catch (MalformedURLException malformedURLException) {
      malformedURLException.printStackTrace();
    }

    assert coin != null;
    String name = coin.get("name").getAsString();
    String img = coin.get("image").getAsJsonObject().get("large").getAsString();

    JsonObject market = coin.get("market_data").getAsJsonObject();

    JsonElement priceElement = market.get("current_price").getAsJsonObject().get("usd");
    String price = priceElement == null ? null : "$" + priceElement.getAsString();

    JsonElement highPriceElement = market.get("high_24h").getAsJsonObject().get("usd");
    String highPrice = highPriceElement == null ? null : "$" + highPriceElement.getAsString();

    JsonElement lowPriceElement = market.get("low_24h").getAsJsonObject().get("usd");
    String lowPrice = lowPriceElement == null ? null : "$" + lowPriceElement.getAsString();

    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.SUCCESS,
                e.getAuthor(),
                String.format(
                    "%s\n\n%s\n%s\n%s\n%s",
                    OtherUtils.formatMessageKeyText("Search results for coin", coinId),
                    OtherUtils.formatMessageKeyText("Name", name),
                    OtherUtils.formatMessageKeyText(
                        "Current price", (price == null ? "No data available." : price)),
                    OtherUtils.formatMessageKeyText(
                        "Highest price in 24h",
                        (highPrice == null ? "No data available." : highPrice)),
                    OtherUtils.formatMessageKeyText(
                        "Lowest Price in 24h",
                        (lowPrice == null ? "No data available." : lowPrice))),
                e.getGuild(),
                null,
                img))
        .queue();
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length == 2;
  }
}
