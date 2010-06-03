/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang.Validate;


/** This listener listens for destroyed sessions to notify the {@link
 * CasTicketRegistry} to remove the session and it's related ticket.
 *
 * @author pruggia
 */
public class CasSessionListener implements HttpSessionListener,
    ServletContextListener {

  /** Registry used to bind CAS tickets with user sessions.
   *
   * It is never null
   */
  private CasTicketRegistry casTicketRegistry;

  /** Constructor, builds a CasSessionListener.
   *
   * @param theTicketRegistry The ticket registry to be used. Cannot be null.
   */
  public CasSessionListener(final CasTicketRegistry theTicketRegistry) {
    Validate.notNull(theTicketRegistry);
    casTicketRegistry = theTicketRegistry;
  }

  /** This method does nothing.
   *
   * {@inheritDoc}
   */
  public void sessionCreated(final HttpSessionEvent se) {
    // nothing to do
  }

  /** Removes the session and its ticket from the ticket registry.
   *
   * {@inheritDoc}
   */
  public void sessionDestroyed(final HttpSessionEvent se) {
    casTicketRegistry.removeSession(se.getSession());
  }

  /** This method does nothing.
   *
   * {@inheritDoc}
   */
  public void contextDestroyed(final ServletContextEvent sce) {
    // nothing to do
  }

  /** This method does nothing.
   *
   * {@inheritDoc}
   */
  public void contextInitialized(final ServletContextEvent sce) {
    // noting to do.
  }
}

