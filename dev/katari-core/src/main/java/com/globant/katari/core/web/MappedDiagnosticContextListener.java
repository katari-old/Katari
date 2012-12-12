/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.core.web;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/** Cleans up slf4j Mapped Diagnostic Context every time a request ends.
 *
 * If in your application you use MDC to collect data to add to log entries,
 * then you must be sure to clear all the MDC information when it is no longer
 * relevant, otherwise you have a local storage leak.
 *
 * This listener takes care of clearing the MDC, thus freeing the thread local
 * storage, after the request is finished.
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class MappedDiagnosticContextListener implements ServletRequestListener {

  /** The class logger.*/
  private static Logger log = LoggerFactory.getLogger(
      MappedDiagnosticContextListener.class);

  /** {@inheritDoc}. */
  public void requestInitialized(final ServletRequestEvent event) {
    log.trace("Entering requestInitialized");
    log.trace("Leaving requestInitialized");
  }

  /** {@inheritDoc}. */
  public void requestDestroyed(final ServletRequestEvent event) {
    log.trace("Entering requestDestroyed");
    MDC.clear();
    log.trace("Leaving requestDestroyed");
  }
}

