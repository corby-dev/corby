/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.model.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import xyz.d1snin.corby.Corby;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Starboard implements MongoSerializable {

  private Guild guild;

  private TextChannel channel;

  private int stars;

  private boolean status;

  @Override
  public DBObject toDBObject() {
    return new BasicDBObject()
        .append("guild", getGuild().getId())
        .append("channel", getChannel().getId())
        .append("stars", getStars())
        .append("status", isStatus());
  }

  @Override
  public MongoSerializable fromDBObject(DBObject object) {
    setGuild(Corby.getApi().getGuildById(object.get("guild").toString()));
    setChannel(Corby.getApi().getTextChannelById(object.get("channel").toString()));
    setStars((int) object.get("stars"));
    setStatus((boolean) object.get("status"));

    return this;
  }
}
