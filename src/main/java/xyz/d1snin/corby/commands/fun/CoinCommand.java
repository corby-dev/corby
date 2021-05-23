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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class CoinCommand extends Command {

  public CoinCommand() {
    this.alias = "coin";
    this.description = "Searches for information about cryptocurrency among 7000+ coins!";
    this.category = Category.FUN;
    this.usages = new String[] {"<Cryptocurrency Coin (Example: btc, eth>"};
  }

  private static JsonArray array;

  @Override
  protected void execute(MessageReceivedEvent e, String[] args) throws MalformedURLException {
    array = getJsonArray();

    e.getTextChannel()
        .sendMessage(
            Embeds.create(
                EmbedTemplate.DEFAULT,
                e.getAuthor(),
                String.format("Searching among %d coins...", array.size()),
                e.getGuild(),
                null))
        .queue(
            message -> {
              String coinId = null;
              String[] targets = new String[] {"symbol", "id"};
              for (String s : targets) {
                String id = null;
                try {
                  id = getId(s, args[1].toLowerCase());
                } catch (MalformedURLException malformedURLException) {
                  malformedURLException.printStackTrace();
                }
                if (id == null) {
                  continue;
                }
                coinId = id;
              }

              if (coinId == null) {
                message
                    .editMessage(
                        Embeds.create(
                            EmbedTemplate.ERROR,
                            e.getAuthor(),
                            String.format(
                                "We searched among %d coins but did not find any matches.",
                                array.size()),
                            e.getGuild(),
                            null))
                    .queue();
                return;
              }
              JsonObject coin = null;
              try {
                coin = getCoin(coinId);
              } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
              }

              try {
                assert coin != null;
                String name = coin.get("name").getAsString();

                JsonElement alg = coin.get("hashing_algorithm");
                String algorithm = alg.isJsonNull() ? "No data available." : alg.getAsString();

                JsonElement cat = coin.get("categories");
                JsonArray categories = cat.isJsonNull() ? null : cat.getAsJsonArray();

                JsonElement img = coin.get("image").getAsJsonObject().get("large");
                String image = img.isJsonNull() ? null : img.getAsString();
                double price =
                    coin.get("market_data")
                        .getAsJsonObject()
                        .get("current_price")
                        .getAsJsonObject()
                        .get("usd")
                        .getAsDouble();
                double highPrice =
                    coin.get("market_data")
                        .getAsJsonObject()
                        .get("high_24h")
                        .getAsJsonObject()
                        .get("usd")
                        .getAsDouble();
                double lowPrice =
                    coin.get("market_data")
                        .getAsJsonObject()
                        .get("low_24h")
                        .getAsJsonObject()
                        .get("usd")
                        .getAsDouble();

                StringBuilder categoryString = new StringBuilder();
                if (categories != null && categories.size() > 0) {
                  Iterator<JsonElement> iterator = categories.iterator();
                  while (iterator.hasNext()) {
                    categoryString
                        .append(iterator.next().getAsString())
                        .append(iterator.hasNext() ? ", " : "");
                  }
                } else {
                  categoryString.append("No data available.");
                }

                message
                    .editMessage(
                        Embeds.create(
                            EmbedTemplate.SUCCESS,
                            e.getAuthor(),
                            String.format(
                                "**Search results for coin:** *%s*\n\n**Name:**\n*%s*\n\n**Hashing Algorithm:**\n*%s*\n\n**Coin Categories:**\n*%s*\n\n**Current Price:**\n*$%f*\n\n**Highest price in 24h:**\n*$%f*\n\n**Lowest Price in 24h:**\n*$%f*",
                                args[1].toUpperCase(),
                                name,
                                algorithm,
                                categoryString,
                                price,
                                highPrice,
                                lowPrice),
                            e.getGuild(),
                            image))
                    .queue();
              } catch (Exception exception) {
                exception.printStackTrace();
                message
                    .editMessage(
                        Embeds.create(
                            EmbedTemplate.ERROR,
                            e.getAuthor(),
                            "Something went wrong while searching for information about this coin.",
                            e.getGuild(),
                            null))
                    .queue();
              }
            });
  }

  @Override
  protected boolean isValidSyntax(MessageReceivedEvent e, String[] args) {
    return args.length == 2;
  }

  private static String getId(String targetName, String query) throws MalformedURLException {
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
}
