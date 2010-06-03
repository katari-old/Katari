/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.Validate;

/** A module request dispatcher that forwards to a servlet in a module.
 *
 * It makes the forwarding path relative to the module that initiated the
 * forward.
 */
class ModuleRequestDispatcher implements RequestDispatcher {

  /** A request wrapper that presents to the servlets in the module, the module
   * name as if it was the servlet context.
   */
  private static class RequestWrapper extends HttpServletRequestWrapper {

    /** The synthesized servlet path.
     */
    private String servletPath;

    /** The synthesized path info.
     */
    private String pathInfo;

    /** Creates a request wrapper.
     *
     * @param request The wrapped request.
     *
     * @param theServletPath The synthesized servlet path.
     *
     * @param thePathInfo The synthesized path info.
     */
    public RequestWrapper(final HttpServletRequest request, final String
        theServletPath, final String thePathInfo) {
      super(request);
      servletPath = theServletPath;
      pathInfo = thePathInfo;
    }

    /** The servlet path.
     *
     * @return Returns the servlet path.
     */
    public String getServletPath() {
      return servletPath;
    }

    /** The path info.
     *
     * @return Returns the path info.
     */
    public String getPathInfo() {
      return pathInfo;
    }
  }

  /** The module container servlet.
   *
   * It is never null.
   */
  private ModuleContainerServlet moduleContainer;

  /** The name of the module that contains the servlet.
   */
  private String moduleName;

  /** The url fragment where the servlet is mapped.
   */
  // private String servletPath;

  /** The fragment of the url after the servlet path.
   */
  // private String pathInfo;

  /** The path to forward to.
   */
  private String path;

  /** Creates a module request dispatcher.
   *
   * @param theModuleName The name of the module. The forwarding path is
   * relative to this module.
   *
   * @param theModuleContainer The module container servlet. It cannot be null.
   *
   * @param thePath The path to forward the request to.
   */
  public ModuleRequestDispatcher(final ModuleContainerServlet
      theModuleContainer, final String theModuleName, final String thePath) {
    Validate.notNull(theModuleContainer, "The container servlet cannot be"
        + " null");
    moduleContainer = theModuleContainer;
    moduleName = theModuleName;
    path = thePath;
  }

  /** Forwards the request to another path.
   *
   * @param request The request to forward.
   *
   * @param response The response object where the response is generated.
   *
   * @throws IOException if an input or output exception occurs.
   *
   * @throws ServletException if some other error occurs.
   */
  public void forward(final ServletRequest request, final ServletResponse
      response) throws ServletException, IOException {

    if (!(request instanceof HttpServletRequest)) {
      throw new ServletException("I can only forward http requests.");
    }

    HttpServletRequest httpRequest = (HttpServletRequest) request;

    ModuleContainerServlet.ServletData servletData;
    servletData = moduleContainer.getServletFromUri("/" + moduleName + path);
    ServletRequest wrapper = new RequestWrapper(httpRequest,
        servletData.getServletPath(), null);
    servletData.getServlet().service(wrapper, response);

    /*
       ModuleContainerServlet.ServletData servletData = null;
       ServletRequest servletRequest = null;
       if (path.startsWith("/WEB-INF")) {
       super.forward(request, response);
       } else {
       servletRequest = new RequestWrapper((HttpServletRequest) request,
       servletData.getServletPath(), null);
       servletData = moduleContainer.getServletFromUri("/" + moduleName + path);
       }
       servletData.getServlet().service(servletRequest, response);
       */
  }

  /** Forwards the request and includes the result in the current output.
   *
   * TODO This operation is not implemented an throws an exception if called.
   *
   * @param request The request to forward.
   *
   * @param response The response object where the response is generated.
   */
  public void include(final ServletRequest request, final ServletResponse
      response) {
    throw new RuntimeException("include not implemented");
  }
}

