/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.login.cas;

import org.acegisecurity.providers.cas.CasAuthenticationToken;
import org.acegisecurity.providers.cas.StatelessTicketCache;

/** An implementation of StatelessTicketCache that caches nothing.
 *
 * Use this implementation if you do want to force all unknown requests to be
 * validated through the cas server.
 */
public class DummyStatelessTicketCache implements StatelessTicketCache {

  /** Retrieves the CasAuthenticationToken associated with the specified
   * ticket.
   *
   * @param serviceTicket The ticket. This parameter is ignored.
   *
   * @return this always returns null.
   */
  public CasAuthenticationToken getByTicketId(final String serviceTicket) {
    return null;
  }

  /** Adds the specified CasAuthenticationToken to the cache.
   *
   * @param token The token to be added to the cache.
   *
   * In this implementation, this method does nothing.
   */
  public void putTicketInCache(final CasAuthenticationToken token) {
  }

  /** Removes the specified ticket from the cache, as per
   * removeTicketFromCache(String).
   *
   * @param token The token to remove from the cache.
   *
   * In this implementation, this method does nothing.
   */
  public void removeTicketFromCache(final CasAuthenticationToken token) {
  }

  /** Removes the specified ticket from the cache, meaning that future calls
   * will require a new service ticket.
   *
   * @param serviceTicket The ticket to be removed.
   *
   * In this implementation, this method does nothing.
   */
  public void removeTicketFromCache(final String serviceTicket) {
  }
}

