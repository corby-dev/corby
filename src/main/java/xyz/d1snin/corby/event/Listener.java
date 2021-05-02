package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class Listener implements EventListener {

  protected abstract void execute(GenericEvent event);

  @Override
  public void onEvent(GenericEvent event) {

    Class<? extends GenericEvent> thisEvent = event.getClass();

    if (this.getClass().getAnnotation(xyz.d1snin.corby.annotation.EventListener.class) != null) {
      if (Arrays.asList(
              this.getClass()
                  .getAnnotation(xyz.d1snin.corby.annotation.EventListener.class)
                  .event())
          .contains(thisEvent)) {
        execute(thisEvent.cast(event));
      }
    }
  }
}
