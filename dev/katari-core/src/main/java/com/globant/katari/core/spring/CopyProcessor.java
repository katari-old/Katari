/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

/** A simple camel processor to copy its input to its output.
 *
 * This is the default camel endpoint for a route that has no destinations. It
 * is used as a 'bean:' endpoint when there are no 'listeners' for an event.
 */
public class CopyProcessor {

  /** Copy its input to its output.
   *
   * @param o what to copy.
   *
   * @return the parameter o.
   */
  public Object copy(final Object o) {
    return o;
  }
}

