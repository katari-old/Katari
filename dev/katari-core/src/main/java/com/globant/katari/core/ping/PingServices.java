/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.ping;

import java.util.List;
import java.util.LinkedList;

import org.apache.commons.lang.Validate;

/** Holds all the ping services that will be ran when the user requests a ping,
 * usually hitting .../ping url.
 *
 * This is intended to be used as a bean in spring. Ping services can be added
 * incrementally, giving the possibility of adding additional ping services to
 * an katari module. This is done with the help of a PingServiceExpander.
 */
public class PingServices {

  /** The list of registered ping services.
   *
   * This is never null.
   */
  private List<PingService> pingServices = new LinkedList<PingService>();

  /** Adds additional ping services at the end of the list of currently defined
   * ping services.
   *
   * @param additionalPingServices additional ping services. It cannot be null.
   */
  public void addPingServices(final List<PingService> additionalPingServices) {
    Validate.notNull(pingServices,
        "The list of additional ping services cannot be null");
    pingServices.addAll(additionalPingServices);
  }

  /** Calls ping on all the ping services and returns the result of all calls.
   *
   * @return a list of PingResults, with one element per service. It never
   * returns null.
   */
  public List<PingResult> ping() {
    List<PingResult> results = new LinkedList<PingResult>();
    for (PingService ping : pingServices) {
      results.add(ping.ping());
    }
    return results;
  }
}

