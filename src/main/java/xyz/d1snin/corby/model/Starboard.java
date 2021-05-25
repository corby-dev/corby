package xyz.d1snin.corby.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

@Getter
@Builder
@AllArgsConstructor
public class Starboard {
  private final Guild guild;

  @Setter private TextChannel channel;

  @Setter private int stars;

  @Setter private boolean status;
}
