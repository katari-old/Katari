/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.ServletResponse;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** This filter puts in the request various objects useful for an katari
 * applicaion, including 'baseweb', 'request' and 'response'.
 *
 * The 'baseweb' is an url where the web application points to.
 */
public final class RequestVariablesFilter implements Filter {

  /** The class logger.
  */
  private static Log log = LogFactory.getLog(RequestVariablesFilter.class);

  /** {@inheritDoc}
   *
   * This implementation does nothing.
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    log.trace("Entering init");
    log.trace("Leaving init");
  }

  /** Filters the request.
   *
   * Adds the following attributes to the request: baseweb, request and
   * response. baseweb is the original context path.
   *
   * @param request The http/https request to filter. It cannot be null.
   *
   * @param response The http/https response. It cannot be null.
   *
   * @param chain The filter chain. It cannot be null.
   *
   * @throws IOException in case of an io error.
   *
   * @throws ServletException in case of a generic error.
   */
  public void doFilter(final ServletRequest request, final ServletResponse
      response, final FilterChain chain) throws IOException, ServletException {

    if (request instanceof HttpServletRequest) {
      HttpServletRequest servletRequest = (HttpServletRequest) request;

      servletRequest.setAttribute("request", request);
      servletRequest.setAttribute("response", response);
      servletRequest.setAttribute("baseweb", servletRequest.getContextPath());
    }
    chain.doFilter(request, response);
  }

  /** {@inheritDoc}
   *
   * This implementation does nothing.
   */
  public void destroy() {
  }
}

