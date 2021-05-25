package xyz.d1snin.corby.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;

@Getter
@AllArgsConstructor
public class Prefix {
  private final Guild guild;

  @Setter private String prefix;

  @Override
  public String toString() {
    return prefix;
  }
}
