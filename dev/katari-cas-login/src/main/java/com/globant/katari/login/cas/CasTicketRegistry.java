/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;

import javax.servlet.http.HttpSession;

/** This object stores the tickets issued by CAS for a given service and store
 * its relation with the HTTPSession.<br>
 *
 * @author pruggia
 */
public class CasTicketRegistry {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(CasTicketRegistry.class);

  /**
   * Map with a Ticket String as the key and the HttpSession as the values.
   */
  private Map<String, HttpSession> ticketRegistry =
    new HashMap<String, HttpSession>();

  /**
   * Map with the SessionId String as the key and the Ticket String as the
   * values.
   */
  private Map<String, String> inverseRegistry = new HashMap<String, String>();

  /**
   * Retrieves the HttpSession that corresponds to the ticket given as
   * parameter.
   * @param ticket A string representing the ticket issued by CAS. Cannot be
   *        null.
   * @return An HttpSession or null if the ticket was not stored in this
   *         registry.
   */
  public HttpSession getSession(final String ticket) {
    Validate.notNull(ticket);
    return ticketRegistry.get(ticket);
  }

  /**
   * Stores in this registry a ticket and relates it to the given session.
   * @param ticket A string representing the ticket issued by CAS. Cannot be
   *        null.
   * @param session The HttpSession of the user that made the login. Cannot be
   *        null.
   */
  public void registerTicket(final String ticket, final HttpSession session) {
    Validate.notNull(ticket);
    Validate.notNull(session);
    this.ticketRegistry.put(ticket, session);
    this.inverseRegistry.put(session.getId(), ticket);
    if (log.isDebugEnabled()) {
      log.debug("Ticket Registered for SessionId " + session.getId()
          + ". Ticket: " + ticket);
    }
  }

  /**
   * Removes the HttpSession and the ticket related to it from this registry.
   * @param session The HttpSession of the user that made the login. Cannot be
   *        null.
   */
  public void removeSession(final HttpSession session) {
    Validate.notNull(session);
    String ticket = inverseRegistry.get(session.getId());
    this.inverseRegistry.remove(session.getId());
    this.ticketRegistry.remove(ticket);
    if (log.isDebugEnabled()) {
      log.debug("Ticket Unregistered for SessionId " + session.getId()
          + ". Ticket: " + ticket);
    }
  }
}

