package com.globant.katari.core.ping;

import org.apache.commons.lang.Validate;

/** This class represents the result of doing a ping.
 *
 * @author demian.calcaprina
 */
public class PingResult {

  /** The status of the ping.
   *
   * It cannot be null.
   */
  private boolean isOk;

  /** The message to be shown. */
  private String message;

  /** Constructor method.
   *
   * @param isPingSuccessful the status of the ping, true if it succeeded..
   *
   * @param theMessage the message to be shown. It cannot be null.
   */
  public PingResult(final boolean isPingSuccessful, final String theMessage) {
    Validate.notNull(theMessage);
    isOk = isPingSuccessful;
    message = theMessage;
  }

  /** Method to get the message.
   *
   * @return the message. It never returns null.
   */
  public String getMessage() {
    return message;
  }

  /** Method to get the status of the ping.
   *
   * @return if the ping is OK.
   */
  public boolean isOk() {
    return isOk;
  }
}

