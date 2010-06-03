/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.Validate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** A filter that adds to the request attributes that will be used by
 * freemarker macros.
 *
 * This filter puts the SecureUrlAccessHelper as an attribute named
 * secureUrlHelper, and a boolean (under attribute named securityDebug) that
 * indicates if the security debug feature is enabled.
 *
 * To enable the security debug feature, add securityDebug=true as a request
 * parameter, for example:
 *
 * http://localhost/katari-web/module/user/users.do?securityDebug=true
 *
 * After that, debug mode is enabled until the request includes a parameter
 * securityDebug=false.
 *
 * @author gerardo.bercovich
 */
public class SecureUrlMacroFilter implements Filter {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(SecureUrlMacroFilter.class);

  /** The secure url macro helper.
   *
   * It is never null.
   */
  private final SecureUrlAccessHelper helper;

  /** The constructor for an application with no database.
   *
   * @param theHelper the secure url macro helper instance. It cannot be null.
   */
  public SecureUrlMacroFilter(final SecureUrlAccessHelper theHelper) {
    Validate.notNull(theHelper, "The SecureUrlAccessHelper cannot be null");
    helper = theHelper;
  }

  /** Puts the helper in the request and continues with the chain.
   *
   * {@inheritDoc}
   */
  public void doFilter(final ServletRequest request,
      final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {

    log.trace("Entering doFilter");

    if (!(request instanceof HttpServletRequest)) {
      throw new ServletException("This filter can only be applied to http"
          + " requests.");
    }

    HttpServletRequest servletRequest = (HttpServletRequest) request;

    String debugParameter = request.getParameter("securityDebug");
    if (debugParameter != null) {
      // Only consider the debugParameter in a development database. This is
      // checked after the debugParameter because checkForDevelopmentDatabase
      // hits the database.
      HttpSession session = servletRequest.getSession();
      if (Boolean.valueOf(debugParameter)) {
        log.debug("Enabling security debug mode.");
        session.setAttribute("securityDebug", debugParameter);
      } else {
        log.debug("Disabling security debug mode.");
        session.removeAttribute("securityDebug");
      }
    }
    servletRequest.setAttribute("secureUrlHelper", helper);

    chain.doFilter(request, response);

    log.trace("Leaving doFilter");
  }

  /** Initializes the filter.
   *
   * {@inheritDoc}
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
  }

  /** This operation is empty.
   *  Enviroment
   * {@inheritDoc}
   */
  public void destroy() {
  }
}
