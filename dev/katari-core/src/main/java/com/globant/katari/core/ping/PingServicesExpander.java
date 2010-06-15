/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.ping;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;

/** Adds additional services to a PingServices.
 *
 * This class is intended to be used in spring. Each instance of this bean will
 * be initialized with an existing PingServices instance and a list of
 * PingService instances. Each instance of this class will add the list to the
 * PingServices instance.
 */
public class PingServicesExpander {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(PingServicesExpander.class);

  /** Adds additional PingService instances to the PingServices object.
   *
   * @param pingServices the holder of ping services. It cannot be null.
   *
   * @param additionalPingServices list of additional ping services. It cannot
   * be null.
   */
  public PingServicesExpander(final PingServices pingServices,
      final List<PingService> additionalPingServices) {
    Validate.notNull(pingServices, "The ping services holder cannot be null");
    Validate.notNull(additionalPingServices,
        "The list of additional ping services cannot be null");
    log.trace("Entering PingServicesExpander");
    pingServices.addPingServices(additionalPingServices);
    log.trace("Leaving PingServicesExpander");
  }
}

