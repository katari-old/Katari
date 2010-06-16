/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A servlet context that wraps the web container servlet context.
 *
 * This servlet context makes the module unaware of the module container. It
 * changes the context path to include the url fragment where the module is
 * mapped.
 */
public final class ModuleServletContext extends ServletContextWrapper {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      ModuleServletContext.class);

  /** The module container servlet.
   *
   * It is never null.
   */
  private ModuleContainerServlet container;

  /** The servlet context to which most of the methods will delegate the
   * implementation.
   *
   * This is usually the web container provided servlet context. It is never
   * null.
   */
  private ServletContext delegate;

  /** The module name.
   *
   * It is never null.
   */
  private String moduleName;

  /** Builds a servlet context for a module.
   *
   * @param theContainer The servlet that acts as container for the modules.
   * The container knows how to dispatch requests to different modules. It
   * cannot be null.
   *
   * @param theDelegate The servlet context to which most of the methods will
   * delegate the implementation. It cannot be null.
   *
   * @param module The module name. It cannot be null.
   */
  ModuleServletContext(final ModuleContainerServlet theContainer, final
      ServletContext theDelegate, final String module) {
    super(theDelegate);
    Validate.notNull(theContainer, "The container cannot be null");
    Validate.notNull(theDelegate, "The delegate cannot be null");
    Validate.notNull(module, "The module name cannot be null");

    container = theContainer;
    delegate = theDelegate;
    moduleName = module;
  }

  /** Returns a RequestDispatcher object that acts as a wrapper for the
   * resource located at the given path.
   *
   * @param path The path to forward to or include.
   *
   * @return Returns a RequestDispatcher that can forward or include the given
   * path.
   */
  public RequestDispatcher getRequestDispatcher(final String path) {
    if (log.isTraceEnabled()) {
      log.trace("Entering getRequestDispatcher('" + path + "')");
    }
    // RequestDispatcher dispatcher = delegate.getRequestDispatcher(path);

    RequestDispatcher dispatcher;
    dispatcher = new ModuleRequestDispatcher(container, moduleName, path);

    log.trace("Leaving getRequestDispatcher");
    return dispatcher;
  }

  /** Returns a URL to the resource that is mapped to a specified path.
   *
   * In this implementation, if the resource is not found relative to the
   * context, then it is search in the classloader that loaded the servlet.
   *
   * This is done to allow serlvet configurations like struts to be loaded from
   * the classpath. There was a portability problem between jetty and tomcat:
   * jetty could not find the struts configuration file if it's location
   * started with a slash. And without the leading slash, it did not work in
   * tomcat.
   *
   * TODO Decide if we should try the context class loader before the servlet
   * classloader.
   *
   * @param path Path of the content resource.
   *
   * @return Returns a URL object allowing access to any content resource
   * requested.
   *
   * @throws MalformedURLException if the resource path is not properly formed.
   */
  public URL getResource(final String path) throws MalformedURLException {
    if (log.isTraceEnabled()) {
      log.trace("Entering getResource('" + path + "')");
    }

    URL url = null;
    try {
      url = delegate.getResource(path);
    } catch (Exception e) {
      log.error("Not found in delegate", e);
    }
    if (url == null) {
      try {
        url = getClass().getResource(path);
      } catch (Exception e) {
        log.error("Not ound in class loader", e);
      }
    }
    if (url == null) {
      try {
        url = getClass().getResource("/" + path);
      } catch (Exception e) {
        log.error("Not ound in class loader with /", e);
      }
    }

    log.trace("Leaving getResource");
    return url;
  }
}

