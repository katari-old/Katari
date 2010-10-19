/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.ping;

import com.globant.katari.core.ping.PingService;
import com.globant.katari.core.ping.PingResult;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;

/** Gives information about the current application configuration, in
 * particular, debug mode and html validation mode.
 */
public class PingConfiguration implements PingService {

  /** True if the app is in debug mode, obtained from spring.
   */
  private boolean debugMode = false;

  /** True if katari validates all html sent to the client.
   */
  private boolean isHtmlValidationEnabled = false;

  /** Constructor.
   *
   * @param theDebugMode the application wide debug mode flag.
   *
   * @param isValidationEnabled true if html is being validated.
   */
  PingConfiguration(final boolean theDebugMode, final boolean
      isValidationEnabled) {
    debugMode = theDebugMode;
    isHtmlValidationEnabled = isValidationEnabled;
  }

  /** Gives information about the current memory usage.
   *
   * @return the status of the memory.
   */
  public PingResult ping() {

    StringBuilder message = new StringBuilder();

    if (debugMode) {
      message.append("Debug mode is on").append("\n");
    } else {
      message.append("Debug mode is off").append("\n");
    }
    if (isHtmlValidationEnabled) {
      message.append("Html validation is enabled").append("\n");
    } else {
      message.append("Html validation is disabled").append("\n");
    }
    return new PingResult(true, message.toString());
  }
}

