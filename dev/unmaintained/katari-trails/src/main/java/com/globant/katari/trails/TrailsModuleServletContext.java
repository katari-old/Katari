/* vim: set ts=2 et sw=2 cindent fo=qroca: */
package com.globant.katari.trails;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.lang.Validate;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * Wrapper for the ServletContext that belongs to Trails Modules.
 *
 * It's main responsability is to hide the applicationContext of the entire
 * application. Every time the attribute of the applicationContext is requested
 * to this ServletContext, the ServletContext responds with an
 * ApplicationContext created with the "module-beans.xml" file of the module
 * and is child of the application context.<br>
 */
@SuppressWarnings(value = { "deprecation" })
public final class TrailsModuleServletContext implements ServletContext {

  /** The servlet context to which most of the methods will delegate the
   * implementation.
   *
   * This is usually the web container provided servlet context.  It is never
   * null.
   */
  private ServletContext delegate;

  /** Bean factory child of the application context.
   *
   * It's created the first time that it's attribute is request. Cannot be
   * null.
   */
  private BeanFactory beanFactory;

  /**
   * Builds a servlet context for a trails module.
   *
   * @param theBeanFactory Bean factory child of the application context. It's
   * created the first time that it's attribute is request. Cannot be null.
   *
   * @param theDelegate The servlet context that this wrapper delegates most of
   * the requests to. It cannot be null.
   */
  TrailsModuleServletContext(final BeanFactory theBeanFactory,
      final ServletContext theDelegate) {
    Validate.notNull(theDelegate, "The delegate cannot be null");
    Validate.notNull(theBeanFactory);
    delegate = theDelegate;
    beanFactory = theBeanFactory;
  }

  /**
   * {@inheritDoc}
   */
  public void removeAttribute(final String name) {
    delegate.removeAttribute(name);
  }

  /**
   * {@inheritDoc}
   */
  public void log(final String message, final Throwable throwable) {
    delegate.log(message, throwable);
  }

  /**
   * {@inheritDoc}
   */
  public void log(final Exception exception, final String msg) {
    delegate.log(exception, msg);
  }

  /**
   * {@inheritDoc}
   */
  public void log(final String msg) {
    delegate.log(msg);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Enumeration getServlets() {
    return delegate.getServlets();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Enumeration getServletNames() {
    return delegate.getServletNames();
  }

  /**
   * {@inheritDoc}
   */
  public String getServletContextName() {
    return delegate.getServletContextName();
  }

  /**
   * {@inheritDoc}
   */
  public Servlet getServlet(final String name) throws ServletException {
    return delegate.getServlet(name);
  }

  /**
   * {@inheritDoc}
   */
  public String getServerInfo() {
    return delegate.getServerInfo();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Set getResourcePaths(final String path) {
    return delegate.getResourcePaths(path);
  }

  /**
   * {@inheritDoc}
   */
  public InputStream getResourceAsStream(final String path) {
    return delegate.getResourceAsStream(path);
  }

  /**
   * {@inheritDoc}
   */
  public URL getResource(final String path) throws MalformedURLException {
    return delegate.getResource(path);
  }

  /**
   * {@inheritDoc}
   */
  public RequestDispatcher getRequestDispatcher(final String path) {
    return delegate.getRequestDispatcher(path);
  }

  /**
   * {@inheritDoc}
   */
  public String getRealPath(final String path) {
    return delegate.getRealPath(path);
  }

  /**
   * {@inheritDoc}
   */
  public RequestDispatcher getNamedDispatcher(final String name) {
    return delegate.getNamedDispatcher(name);
  }

  /**
   * {@inheritDoc}
   */
  public int getMinorVersion() {
    return delegate.getMinorVersion();
  }

  /**
   * {@inheritDoc}
   */
  public String getMimeType(final String file) {
    return delegate.getMimeType(file);
  }

  /**
   * {@inheritDoc}
   */
  public int getMajorVersion() {
    return delegate.getMajorVersion();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Enumeration getInitParameterNames() {
    return delegate.getInitParameterNames();
  }

  /**
   * {@inheritDoc}
   */
  public String getInitParameter(final String name) {
    return delegate.getInitParameter(name);
  }

  /**
   * {@inheritDoc}
   */
  public ServletContext getContext(final String uripath) {
    return delegate.getContext(uripath);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Enumeration getAttributeNames() {
    return delegate.getAttributeNames();
  }

  /**
   * {@inheritDoc}
   */
  public void setAttribute(final String name, final Object object) {
    if (name.equals(
          WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)) {
      throw new IllegalArgumentException("Illegal operation: "
          + "cannot set the application context for this servlet context");
    } else {
      delegate.setAttribute(name, object);
    }
  }

  /**
   * {@inheritDoc}
   */
  public Object getAttribute(final String name) {
    if (name
        .equals(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)) {
      return beanFactory;
    } else {
      return delegate.getAttribute(name);
    }
  }
}

