/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This servlet is the main entry to the modules of a web application.
 *
 * It dispatches all requests to a spring configured servlet defind under the
 * bean named, by default 'katari.moduleContainer' in the web application
 * context.  This is usualy a ModuleContainerServlet, but it can be any
 * servlet.
 *
 * The specific bean name is configured using the servletBeanName
 * initialization parameter.
 *
 * This should be the only spring aware servlet in an application.
 */
public final class SpringBootstrapServlet extends HttpServlet {

  /** The serialization version number.
   *
   * This number must change every time a new serialization incompatible change
   * is introduced in the class.
   */
  private static final long serialVersionUID = 20071005;

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(SpringBootstrapServlet.class);

  /** The target servlet that this servlet delegates all requests.
   *
   * This is null until initialization.
   */
  private HttpServlet delegate = null;

  /** Called by the servlet container to indicate to a servlet that the
   * servlet is being placed into service.
   *
   * @param config The servlet configuration and initialization parameters.
   * This object is created by the container.
   *
   * @throws ServletException if an error occurs.
   */
  public void init(final ServletConfig config) throws ServletException {
    log.trace("Entering init");
    super.init(config);
    String beanName = getBeanName();
    WebApplicationContext context = getWebApplicationcontext();
    delegate = (HttpServlet) context.getBean(beanName);
    if (delegate == null) {
      throw new ServletException("No bean named " + beanName
          + " found in spring application context."
          + " Please initialize this servlet correctly.");
    }
    delegate.init(config);
    log.trace("Leaving init");
  }

  /** Called by the servlet container to allow the servlet to respond to a
   * request.
   *
   * It delegates all the requests to a spring configured servlet defined under
   * the bean named 'katari.moduleContainer'.
   *
   * @param request The HttpServletRequest object that contains the client's
   * request.
   *
   * @param response The HttpServletResponse object that contains the
   * servlet's response
   *
   * @throws IOException if an input or output exception occurs.
   *
   * @throws ServletException if another error occurs.
   */
  protected void service(final HttpServletRequest request, final
      HttpServletResponse response) throws ServletException, IOException {
    log.trace("Entering service");
    delegate.service(request, response);
    log.trace("Leaving service");
  }

  /** Called by the web container to indicate to a servlet that it is being
   * taken out of service.
   *
   * It passes the message to the delegate.
   */
  public void destroy() {
    delegate.destroy();
  }

  /** Returns the spring web application context.
   *
   * @return Returns the spring web application context.
   */
  private WebApplicationContext getWebApplicationcontext() {
    return WebApplicationContextUtils.getWebApplicationContext(
        getServletContext());
  }

  /** Obtains, from the servlet configuation, the name of the spring bean to
   * delegate the requests to.
   *
   * The name of the bean is specified with the servletBeanName init parameter.
   * The bean must implement the HttpServlet interface.
   *
   * @return the bean name as specified in the servlet configuration. If the
   * bean name was not specified, it returns 'katari.moduleContainer'.
   */
  private String getBeanName() {
    String beanName = getInitParameter("servletBeanName");
    if (beanName == null) {
      return "katari.moduleContainer";
    } else {
      return beanName;
    }
  }
}

