/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

/** Http servlet that acts as a bridge between Katari and other web frameworks.
 *
 * Basically, it loads a local application context (like DispatcherServlet
 * does) and makes it available for the underlying servlet. All requests are
 * delegated to an {@link Servlet} instance.
 *
 * @author pablo.saavedra
 */
public class KatariWrapperServlet extends HttpServlet {

  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The {@link FacesServlet} to delegate to.
   */
  private Servlet delegate;

  /**
   * A local web application context.
   */
  private XmlWebApplicationContext appContext;

  /**
   * The context parameters to override in the servlet context.
   */
  private Map<String, String> initParameterOverrides = new HashMap
    <String, String>();

  /** Creates a new {@link KatariWrapperServlet} wrapping the given
   * FacesServlet.
   *
   * @param theDelegate The faces servlet to wrap, cannot be null.
   */
  public KatariWrapperServlet(final Servlet theDelegate) {
    Validate.notNull(theDelegate, "The faces servlet cannot be null");
    this.delegate = theDelegate;
  }

  /**
   * Services the given request and renders the response.
   * <p>
   * Calls to this method are simply passed to the inner delegate. However,
   * subclasses get a chance to modify the request and response overriding the
   * {@link #wrapRequest(HttpServletRequest)} and
   * {@link #wrapResponse(HttpServletResponse)} methods.
   * @param req
   *          The HTTP servlet request.
   * @param resp
   *          The HTTP servlet response.
   * @throws ServletException
   *           as part of the servlet contract.
   * @throws IOException
   *           as part of the servlet contract.
   * @see #wrapRequest(HttpServletRequest)
   * @see #wrapResponse(HttpServletResponse)
   * @see #service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
   */
  @Override
  protected void service(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException {
    HttpServletRequest wrappedRequest = wrapRequest(req);
    HttpServletResponse wrappedResponse = wrapResponse(resp);
    delegate.service(wrappedRequest, wrappedResponse);
  }

  /** Gives subclasses a chance to wrap the original {@link
   * HttpServletResponse}.
   *
   * This method must never return null.
   *
   * @param resp
   *          The response to wrap,.
   * @return The wrapped response, or the original one if no wrapping is
   *         needed, never null.
   */
  protected HttpServletResponse wrapResponse(final HttpServletResponse resp) {
    return resp;
  }

  /**
   * Gives subclasses a chance to wrap the original {@link HttpServletRequest}.
   * This method must never return null.
   * @param req
   *          The request to wrap,.
   * @return The wrapped request, or the original one if no wrapping is needed,
   *         never null.
   */
  protected HttpServletRequest wrapRequest(final HttpServletRequest req) {
    return req;
  }

  /**
   * Destroys the servlet.
   * <p>
   * This class does the following, in order:
   * <ol>
   * <li>Destroys the delegate servlet</li>
   * <li>Destroys the local application context</li>
   * <li>Calls super.destroy()</li>
   * </ol>
   */
  @Override
  public void destroy() {
    delegate.destroy();
    appContext.destroy();
    super.destroy();
  }

  /**
   * Initializes the servlet.
   * <p>
   * The initialization process consists of creating a
   * {@link WebApplicationContext} by loading the xml configuration file
   * located at the path indicated by the <code>contextConfigLocation</code>
   * initialization parameter.
   * <p>
   * The application context will be stored in a {@link ScopedServletContext},
   * under {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
   * key, and all the configured extra init parameters will be loaded into the
   * scoped servlet context too.
   * <p>
   * The new servlet context will be passed into the delegate servlet's init
   * method to complete initialization.
   * @param config
   *          The original servlet config, cannot be null.
   * @throws ServletException
   *           in case of an initialization error.
   * @see ScopedServletContext
   */
  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);
    String location = config.getInitParameter("contextConfigLocation");
    ServletContext ctx = config.getServletContext();
    appContext = new XmlWebApplicationContext();
    appContext.setConfigLocation(location);
    appContext.setServletContext(ctx);
    appContext.setParent(WebApplicationContextUtils
        .getRequiredWebApplicationContext(ctx));
    appContext.refresh();
    ScopedServletContext context = new ScopedServletContext(ctx);
    context.setAttribute(
        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
        appContext);
    addInitParameters(context);
    ServletConfig configWrapper = new ServletConfigWrapper(config, context);
    delegate.init(configWrapper);
  }

  /**
   * Adds the configured init parameters to the given servlet context.
   * @param context
   *          The servlet context to add the parameters to, cannot be null.
   * @see #setInitParameterOverrides(Map)
   */
  protected void addInitParameters(final ScopedServletContext context) {
    for (Entry<String, String> param : initParameterOverrides.entrySet()) {
      context.addInitParameter(param.getKey(), param.getValue());
    }
  }

  /**
   * Sets the init parameters to override in the servlet context.
   * @param overrides
   *          The map of parameters to add, cannot be null.
   */
  public void setInitParameterOverrides(final Map<String, String> overrides) {
    Validate.notNull(overrides);
    this.initParameterOverrides = overrides;
  }
}

