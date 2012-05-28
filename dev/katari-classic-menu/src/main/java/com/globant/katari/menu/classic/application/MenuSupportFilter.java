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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.core.security.MenuAccessFilterer;
import com.globant.katari.core.web.MenuNode;
import com.globant.katari.core.web.ModuleContextRegistrar;

/** This filter makes available the MenuDisplayHelper for the menu rendering
 * logic, and puts the current selected menu entry in a user's cookie.
 */
public final class MenuSupportFilter implements Filter {

  /** The class logger.
  */
  private static Logger log = LoggerFactory.getLogger(MenuSupportFilter.class);

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

  /** Stores the MenuDisplayHelper in the request an the selected menu in
   * a cookie.
   *
   * This looks for the menu tree in the registrar and filter the menu nodes
   * according to the user permissions using a
   * <code>MenuAccessFilterer</code>, creates a new
   * MenuDisplayHelper and adds it to the request attribute named
   * '::menu-display-helper'.
   *
   * Also looks for a request parameter named selected-module-entry and, if
   * found, it copies the parameter value to a cookie named
   * 'selected-module-entry'.
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

    if (!(response instanceof HttpServletResponse)) {
      throw new ServletException("This filter can only be applied to http"
          + " responses.");
    }

    HttpServletRequest servletRequest = (HttpServletRequest) request;
    HttpServletResponse servletResponse = (HttpServletResponse) response;

    String entry = servletRequest.getParameter("selected-module-entry");

    if (entry != null) {
      setMenuPathCookie(servletRequest, servletResponse, entry);
    } else {
      // Obtain the menu entry from the received cookie.
      Cookie[] cookies = servletRequest.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if (cookie.getName().equals("selected-module-entry")) {
            entry = cookie.getValue();
          }
        }
      }
    }

    if (entry == null) {
      // No menu entry, we set it to "", as expected by MenuDisplayHelper.
      entry = "";
    }

    MenuDisplayHelper helper;
    helper = new MenuDisplayHelper(registrar.getMenuBar(), entry, filterer);
    servletRequest.setAttribute("::menu-display-helper", helper);

    chain.doFilter(request, response);
    log.trace("Leaving doFilter");
  }

  /** Sets the 'selected-module-entry' cookie to send to the client, with entry
   * as value, for the request context path.
   *
   * @param servletRequest the servlet request to obtain the path from. It
   * cannot be null.
   *
   * @param servletResponse the servlet response to use to send the cookie to
   * the client. It cannot be null.
   *
   * @param entry the current menu entry path, to send to the client as the
   * cookie value.
   */
  private void setMenuPathCookie(final HttpServletRequest servletRequest,
      final HttpServletResponse servletResponse, final String entry) {
    String path = (String) servletRequest.getAttribute("baseweb");
    if (path == null) {
      path = servletRequest.getContextPath();
    }
    Cookie cookie = new Cookie("selected-module-entry", entry);
    cookie.setPath(path);
    servletResponse.addCookie(cookie);
  }
}

