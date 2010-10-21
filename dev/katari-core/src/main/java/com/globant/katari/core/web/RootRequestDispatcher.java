/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A module request dispatcher that forwards to a servlet in a module.
 */
class RootRequestDispatcher implements RequestDispatcher {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      RootRequestDispatcher.class);

  /** If true, this request dispatcher disptaches to the wrapped request.
  */
  private boolean ignoreModule = false;

  /** The request dispatcher that is wrapped by this dispatcher.
   *
   * This is never null.
   */
  private RequestDispatcher delegate;

  /** Constructor, builds a new RootRequestDispatcher.
   *
   * @param ignoreModuleFlag If this flag is true, then it dispatches the
   * request to the original unwrapped request, effectively ignoring the
   * concept of a module.
   *
   * @param theDelegate The RequestDispatcher that this wrapper delegates its
   * methods to. It cannot be null.
   *
   * TODO Looks like the ignoreModuleFlag is of no use here, and always ignore
   * the module. As it is now, spring cannot formward to a katari module.
   */
  public RootRequestDispatcher(final boolean ignoreModuleFlag, final
      RequestDispatcher theDelegate) {
    Validate.notNull(theDelegate, "The delegate cannet be null");
    ignoreModule = ignoreModuleFlag;
    delegate = theDelegate;
  }

  /** Forwards a ServletRequest object from this servlet to a resource
   * (servlet, JSP file, or HTML file) on the server.
   *
   * You can use this method when one servlet does preliminary processing of a
   * request and lets another resource generate the response.
   *
   * The ServletRequest object has its path and other parameters adjusted to
   * be relative to the path of the target resource.
   *
   * You cannot use forward if the target resource has already returned a
   * ServletOutputStream or PrintWriter object to the servlet. In that
   * situation, forward throws an IllegalStateException.
   *
   * @param request a ServletRequest object that represents the request the
   * client makes of the servlet.
   *
   * @param response a ServletResponse object that represents the response
   * the servlet returns to the client.
   *
   * @throws ServletException if the target resource is a servlet and throws
   * an exception.
   *
   * @throws IOException if an input or output exception occurs.
   */
  public void forward(final ServletRequest request, final ServletResponse
      response) throws ServletException, IOException {
    if (!(request instanceof HttpServletRequest)) {
      throw new RuntimeException("Only http is supported.");
    }
    if (log.isTraceEnabled()) {
      log.trace("Entering forward('"
          + ((HttpServletRequest) request).getRequestURI() + "...')");
      log.trace("Request type is: " + request.getClass());
    }
    if (ignoreModule) {
      if (!(request instanceof ModuleRequestWrapper)) {
        throw new RuntimeException("The request must be of type"
            + " ModuleRequestWrapper");
      }
      delegate.forward(((ModuleRequestWrapper) request).getWrappedRequest(),
          response);
    } else {
      delegate.forward(request, response);
    }
    log.trace("Leaving forward");
  }

  /** This operation is not implemented and throws an exception if called.
   *
   * @param request a ServletRequest object that contains the client's
   * request.
   *
   * @param response a ServletResponse object that contains the servlet's
   * response.
   */
  public void include(final ServletRequest request, final ServletResponse
      response) {
    throw new RuntimeException("include not implemented");
  }
}

