package com.globant.katari.core.web;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/** Initializes the Mapped Diagnostic Context every time a request begins.
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class MappedDiagnosticContextListener implements ServletRequestListener {

  /** The class logger.*/
  private static Logger log = LoggerFactory.getLogger(
      MappedDiagnosticContextListener.class);

  /** {@inheritDoc}. */
  public void requestDestroyed(final ServletRequestEvent event) {
    log.trace("Entering requestDestroyed");
    log.trace("Leaving requestDestroyed");
  }

  /** {@inheritDoc}. */
  public void requestInitialized(final ServletRequestEvent event) {
    log.trace("Entering requestInitialized");
    MDC.clear();
    log.trace("Leaving requestInitialized");
  }
}
