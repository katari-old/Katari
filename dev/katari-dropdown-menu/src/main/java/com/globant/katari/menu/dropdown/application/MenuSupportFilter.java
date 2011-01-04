/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.menu.dropdown.application;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.core.security.MenuAccessFilterer;
import com.globant.katari.core.web.ModuleContextRegistrar;

/** This filter makes available a UserMenuNode in the user request.
 *
 * The UserMenuNode wraps the root menu node with all the descendants available
 * to the logged in user.
 */
public final class MenuSupportFilter implements Filter {

  /** The class logger.
  */
  private static Log log = LogFactory.getLog(MenuSupportFilter.class);

  /** The application module context registrar.
   *
   * This is never null.
   */
  private ModuleContextRegistrar registrar;

  /** The Menu Access Filterer.
  *
  * This is never null.
  */
  private MenuAccessFilterer filterer;

  /** Builds a MenuSupportFilter.
   *
   * @param theRegistrar The application module context registrar. It cannot be
   * null.
   *
   * @param theFilterer The Menu Access Filtere used to filter the menu nodes
   * according to the user permissions. It cannot be null.
   */
  public MenuSupportFilter(final ModuleContextRegistrar theRegistrar,
      final MenuAccessFilterer theFilterer) {
    Validate.notNull(theRegistrar, "The registrar cannot be null.");
    Validate.notNull(theFilterer, "The filterer cannot be null.");
    registrar = theRegistrar;
    filterer = theFilterer;
  }

  /** Initializes the filter.
   *
   * The implementation of this operation is empty.
   *
   * @param theFilterConfig The provided filter configuration.
   */
  public void init(final FilterConfig theFilterConfig) {
  }

  /** Called by the container when the filter is abount to be destroyed.
   *
   * This operation is empty.
   */
  public void destroy() {
  }

  /** Stores a UserMenuNode in the request.
   *
   * This looks for the menu tree in the registrar and filters the menu nodes
   * according to the user permissions using a <code>MenuAccessFilterer</code>,
   * creates a new UserMenuNode and adds it to the request attribute named
   * '::menu-display-helper'.
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

    UserMenuNode root = new UserMenuNode(registrar.getMenuBar(), filterer);

    servletRequest.setAttribute("com.globant.katari.menu.dropdown.tree", root);

    chain.doFilter(request, response);
    log.trace("Leaving doFilter");
  }
}

