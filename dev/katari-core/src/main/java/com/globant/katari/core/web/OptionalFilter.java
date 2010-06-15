/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.Validate;

/** A wrapper filter that can enable or disable a wrapped filter at
 * configuration time.
 */
public class OptionalFilter implements Filter {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(OptionalFilter.class);

  /** The filter to enable or disable.
   *
   * It is never null.
   */
  private Filter targetFilter;

  /** Defines if the filter is enabled or not.
   */
  private boolean enabled;

  /** Constructor that wraps the filter to enable or disable.
   *
   * @param filter The target filter that will be enabled or disabled. It
   * cannot be null.
   *
   * @param isEnabled if true, it enables the filter, if false, it disables it.
   */
  public OptionalFilter(final Filter filter, final boolean isEnabled) {
    Validate.notNull(filter, "The target filter cannot be null.");
    targetFilter = filter;
    enabled = isEnabled;
  }

  /** Initializes the filter if enabled.
   *
   * @param filterConfig The provided filter configuration.
   *
   * @throws ServletException in case of error.
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    log.trace("Entering init");
    if (enabled) {
      targetFilter.init(filterConfig);
    }
    log.trace("Leaving init");
  }

  /** {@inheritDoc}
   *
   * Passes the request to the target filter only if enabled.
   */
  public void doFilter(final ServletRequest request, final ServletResponse
      response, final FilterChain chain) throws IOException,
      ServletException {

    log.trace("Entering doFilter.");

    if (enabled) {
      targetFilter.doFilter(request, response, chain);
    } else {
      chain.doFilter(request, response);
    }

    log.trace("Leaving doFilter.");
  }

  /** Destroys the target filter only if enabled.
   */
  public void destroy() {
    log.trace("Entering destroy");
    if (enabled) {
      targetFilter.destroy();
    }
    log.trace("Leaving destroy");
  }
}

