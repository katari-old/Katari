package com.globant.katari.core.ping;

/** Provides an interface to check the status of any item of the application.
 *
 * @author demian.calcaprina
 */
public interface PingService {

  /** This method checks the state of any item of the application.
   *
   * @return String the Status of an item of the application
   */
  PingResult ping();

}
