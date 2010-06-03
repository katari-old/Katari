/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.menu.classic.application;

import java.io.IOException;
import java.util.List;

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

import com.globant.katari.core.security.MenuAccessFilterer;
import com.globant.katari.core.web.MenuNode;
import com.globant.katari.core.web.ModuleContextRegistrar;

/** This filter makes available the MenuDisplayHelper for the menu rendering
 * logic, and puts the current selected menu entry in the user's session.
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

  /** Stores the MenuDisplayHelper in the request an the selected menu in the
   * session.
   *
   * This looks for the menu tree in the registrar and filter the menu nodes
   * according to the user permissions using a
   * <code>MenuAccessFilterer</code>, creates a new
   * MenuDisplayHelper and adds it to the request attribute named
   * '::menu-display-helper'.
   *
   * Also looks for a request parameter named selected-module-entry and, if
   * found, it copies the parameter value to the session attribute named
   * '::selected-module-entry'.
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
    HttpSession session = servletRequest.getSession();

    String entry = servletRequest.getParameter("selected-module-entry");

    if (entry != null) {
      session.setAttribute("::selected-module-entry", entry);
    }

    entry = (String) session.getAttribute("::selected-module-entry");
    if (entry == null) {
      entry = obtainMenuEntry();
      session.setAttribute("::selected-module-entry", entry);
    }

    MenuDisplayHelper helper = new MenuDisplayHelper(registrar.getMenuBar(),
        filterer);
    servletRequest.setAttribute("::menu-display-helper", helper);

    chain.doFilter(request, response);
    log.trace("Leaving doFilter");
  }

  /**
   * Obtains the first menu entry available for the current user.
   * <p>
   * All the child nodes are filtered according to the user's permissions, and
   * the first menu node's path from the filtered list is returned.
   * </p>
   * <p>
   * In case the user has no available menu nodes, an empty string is returned.
   * </p>
   * @return The path for the first available menu node for the current user,
   *         or an empty string if no menu nodes are available. Never returns
   *         null.
   */
  private String obtainMenuEntry() {
    List<MenuNode> childFilteredNodes = filterer.filterMenuNodes(registrar
        .getMenuBar().getChildNodes());
    if (childFilteredNodes.isEmpty()) {
      log.warn("No menu nodes are accesible for the current user");
      return "";
    }
    return childFilteredNodes.get(0).getPath();
  }
}

