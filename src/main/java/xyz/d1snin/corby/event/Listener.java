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
import org.jetbrains.annotations.NotNull;
import xyz.d1snin.corby.Corby;
import xyz.d1snin.corby.utils.ExceptionUtils;

import java.io.IOException;

public abstract class Listener implements EventListener {

  public Class<? extends GenericEvent> event = null;

  public abstract void perform(GenericEvent event) throws IOException;

  @Override
  public void onEvent(@NotNull GenericEvent thisEvent) {
    if (event != null) {

      if (event.equals(thisEvent.getClass())) {

        Corby.getService()
            .execute(
                () -> {
                  try {
                    perform(thisEvent);
                  } catch (Exception e) {
                    ExceptionUtils.processException(e);
                  }
                });
      }
    }
  }
}
