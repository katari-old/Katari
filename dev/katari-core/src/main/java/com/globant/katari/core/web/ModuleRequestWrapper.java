/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.RequestDispatcher;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Wraps a request to hide the module from the delegated servlet.
 */
class ModuleRequestWrapper extends HttpServletRequestWrapper {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(ModuleRequestWrapper.class);

  /** The path where the application is mapped.
   */
  private String contextPath;

  /** The name of the module that receives the request.
   */
  private String moduleName;

  /** The fragment of the url that matched the servlet mapping.
   */
  private String servletPath;

  /** The fragment of the url that follows the servlet path.
   */
  private String pathInfo;

  /** The wrapped request.
   *
   * It is never null.
   */
  private HttpServletRequest wrappedRequest;

  /** Constructor, builds a new module request wrapper.
   *
   * @param theRequest The request wrapped by this object.
   *
   * @param theModuleName The name of the module that receives the wrapped
   * request. It cannot be null.
   *
   * @param theServletPath The fragment of the url that matched the servlet
   * mapping. It cannot be null.
   */
  public ModuleRequestWrapper(final HttpServletRequest theRequest, final String
      theModuleName, final String theServletPath) {
    super(theRequest);
    if (log.isTraceEnabled()) {
      log.trace("Entering ModuleRequestWrapper(..., '" + theModuleName
          + "', '" + theServletPath + "')");
    }
    Validate.notNull(theModuleName, "The module name cannot be null");
    Validate.notNull(theServletPath, "The servlet path cannot be null");
    Validate.notNull(theRequest, "The request cannot be null");

    wrappedRequest = theRequest;
    moduleName = theModuleName;

    if (isIncluded()) {
      contextPath = (String) theRequest.getAttribute(
          "javax.servlet.include.context_path")
        + (String) theRequest.getAttribute(
            "javax.servlet.include.servlet_path")
        + "/" + moduleName;
    } else {
      contextPath = theRequest.getContextPath() + theRequest.getServletPath()
        + "/" + moduleName;
    }

    servletPath = theServletPath;
    if (isIncluded()) {
      pathInfo = (String) theRequest.getAttribute(
          "javax.servlet.include.path_info");
    } else {
      pathInfo = theRequest.getPathInfo();
    }

    // Strips the /<module><servletPath> from the pathinfo.
    if (pathInfo != null && pathInfo.length() != 0) {
      pathInfo = pathInfo.substring(moduleName.length()
          + servletPath.length() + 1);
    }
    if (pathInfo != null && pathInfo.length() == 0) {
      pathInfo = null;
    }
    log.trace("Leaving ModuleRequestWrapper");
  }

  /** Returns the wrapped request.
   *
   * @return Returns the wrapped request. It never returns null.
   */
  public final HttpServletRequest getWrappedRequest() {
   return wrappedRequest;
  }

  /** Returns the servlet plus module name as the context path to hide the
   * existence of the module.
   *
   * @return Returns the context path corresponding to the module.
   */
  public final String getContextPath() {
    log.trace("Entering getContextPath");
    String result;
    if (isIncluded()) {
      result = wrappedRequest.getContextPath();
    } else {
      result = contextPath;
    }
    if (log.isTraceEnabled()) {
      log.trace("Leaving getContextPath with " + result);
    }
    return result;
  }

  /** Returns the fragment of the url that matched the servlet mapping.
   *
   * @return Returns the servlet path. It never returns null.
   */
  public final String getServletPath() {
    log.trace("Entering getServletPath");
    String result;
    if (isIncluded()) {
      result = wrappedRequest.getServletPath();
    } else {
      result = servletPath;
    }
    if (log.isTraceEnabled()) {
      log.trace("Leaving getServletPath with " + result);
    }
    return result;
  }

  /** Returns the path fragment after the module name and servlet path.
   *
   * @return Returns the path fragment.
   */
  public final String getPathInfo() {
    log.trace("Entering getPathInfo");
    String result;
    if (isIncluded()) {
      result = wrappedRequest.getPathInfo();
    } else {
      result = pathInfo;
    }
    if (log.isTraceEnabled()) {
      log.trace("Leaving getPathInfo with " + result);
    }
    return result;
  }

  /** Returns the value of the named attribute as an Object, or null if no
   * attribute of the given name exists.
   *
   * @param name a String specifying the name of the attribute.
   *
   * @return an Object containing the value of the attribute, or null if the
   * attribute does not exist.
   */
  public Object getAttribute(final String name) {
    Object result = wrappedRequest.getAttribute(name);
    if (isIncluded()) {
      if (name.equals("javax.servlet.include.context_path")) {
        result = contextPath;
      }
      if (name.equals("javax.servlet.include.servlet_path")) {
        result = servletPath;
      }
      if (name.equals("javax.servlet.include.path_info")) {
        result = pathInfo;
      }
    }
    return result;
  }

  /** Returns a request dispatcher capable of forwarding to a module.
   *
   * @todo This is not yet implemented.
   *
   * @param path The pathname to the resource. If it is relative, it must be
   * relative against the current servlet.
   *
   * @return Returns a request dispatcher able to forward to the specified
   * path.
   */
  public final RequestDispatcher getRequestDispatcher(final String path) {
    if (log.isTraceEnabled()) {
      log.trace("Entering getRequestDispatcher('" + path + "')");
    }
    RequestDispatcher dispatcher = super.getRequestDispatcher(path);

    dispatcher = new RootRequestDispatcher(path.startsWith("/WEB-INF"),
        dispatcher);

    log.trace("Leaving getRequestDispatcher");
    return dispatcher;
  }

  /** Checks if the wrapped request correponds to a servlet include.
   *
   * @return true if it is an include, false otherwise.
   */
  private boolean isIncluded() {
    return wrappedRequest.getAttribute("javax.servlet.include.request_uri")
      != null;
  }
}

