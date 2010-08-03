/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Makes the weblet renderer available as a request parameter.
 *
 * This is used to access the weblet renderer from the freemarker decorators.
 */
public final class WebletSupportFilter implements Filter {

  /** The class logger.
  */
  private static Logger log = LoggerFactory.getLogger(
      WebletSupportFilter.class);

  /** The servlet context.
   *
   * This is not null once the server calls init.
   */
  private ServletContext servletContext;

  /** Initializes the filter.
   *
   * The implementation of this operation is empty.
   *
   * @param filterConfig The provided filter configuration.
   *
   * @throws ServletException in case of error.
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    servletContext = filterConfig.getServletContext();
  }

  /** Called by the container when the filter is about to be destroyed.
   *
   * The implementation of this operation is empty.
   */
  public void destroy() {
  }

  /** Stores the available weblets in the request.
   *
   * It stores the weblet renderer in the request attribute named
   * '::weblet-renderer'.
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
    log.trace("Entering doFilter");

    if (!(request instanceof HttpServletRequest)) {
      throw new ServletException("This filter can only be applied to http"
          + " requests.");
    }

    HttpServletRequest servletRequest = (HttpServletRequest) request;

    servletRequest.setAttribute("::weblet-renderer",
        new WebletRenderer(servletContext));

    chain.doFilter(request, response);
    log.trace("Leaving doFilter");
  }
}

