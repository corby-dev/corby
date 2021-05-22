package xyz.d1snin.corby.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import xyz.d1snin.corby.commands.Command;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
public class Cooldown {
  private User user;
  private int cooldown;
  private Command command;
}
