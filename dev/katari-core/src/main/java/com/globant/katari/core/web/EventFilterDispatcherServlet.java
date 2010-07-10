/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.servlet.DispatcherServlet;

/** Subclass of DispatcherServlet that only considers context refresh events
 * that originate from it's own spring application context.
 *
 * There is some strange behaviour in spring mvc, version 3.0: the dispatcher
 * servlet reconfigures itself whenever it receives a context refresh event.
 * The new configuration is obtained from the application context that
 * originated the event. But, as in katari, all the DispatcherServlet instances
 * are created as a bean in the root app context, it receives the refresh event
 * from all app contexts. The result is that all DispatcherServlet instances
 * get configured with the same beans.
 *
 * This class should be used in module.xml instead of DispatcherServlet, at
 * least untill this problem is fixed in spring.
 */
public class EventFilterDispatcherServlet extends DispatcherServlet {

  /** The serialization version.
   */
  private static final long serialVersionUID = 1L;

  /** Receives a ContextRefreshedEvent and filters it out if it does not
   * originated from this DispatcherServlet application context.
   *
   * {@inheritDoc}
   */
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    if (event.getApplicationContext().equals(getWebApplicationContext())) {
      super.onApplicationEvent(event);
    }
  }
}

