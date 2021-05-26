/*                          GNU GENERAL PUBLIC LICENSE
 *                            Version 3, 29 June 2007
 *
 *        Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 *            Everyone is permitted to copy and distribute verbatim copies
 *             of this license document, but changing it is not allowed.
 */

package xyz.d1snin.corby.event;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.utils.ExceptionUtils;

import java.util.Arrays;

public abstract class Listener implements EventListener {

  protected abstract void perform(GenericEvent event);

  @Override
  public void onEvent(GenericEvent event) {

    Class<? extends GenericEvent> thisEvent = event.getClass();

    if (this.getClass().getAnnotation(xyz.d1snin.corby.annotation.EventListener.class) != null) {
      if (Arrays.asList(
              this.getClass()
                  .getAnnotation(xyz.d1snin.corby.annotation.EventListener.class)
                  .event())
          .contains(thisEvent)) {
        try {
          Corby.getService().execute(() -> perform(event));
        } catch (Exception e) {
          ExceptionUtils.processException(e);
        }
      }
    }
  }
}
