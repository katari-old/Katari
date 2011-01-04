/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Set;
import java.util.Enumeration;

import java.net.URL;
import java.net.MalformedURLException;

import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.commons.lang.Validate;

/** A servlet context that wraps the web container servlet context.
 *
 * This wrapper is compatible with the 2.5 servlet spec.
 */
@SuppressWarnings(value = { "deprecation" })
public class ServletContextWrapper implements ServletContext {

  /** The servlet version supported, major.
   */
  private static final int SERVLET_VERSION_MAJOR = 2;

  /** The servlet version supported, minor.
   */
  private static final int SERVLET_VERSION_MINOR = 4;

  /** The servlet context to which all methods will delegate the
   * implementation.
   *
   * This is usually the web container provided servlet context. It is never
   * null.
   */
  private ServletContext delegate;

  /** Builds a servlet context for a module.
   *
   * @param theDelegate The servlet context to which most of the methods will
   * delegate the implementation. It cannot be null.
   */
  public ServletContextWrapper(final ServletContext theDelegate) {
    Validate.notNull(theDelegate, "The delegate cannot be null");

    delegate = theDelegate;
  }

  /** Obtains the delegate wrapped by this object.
   *
   * @return the delegate. It never returns null.
   */
  public ServletContext getDelegate() {
    return delegate;
  }

  /** Returns an atribute by name.
   *
   * @param name The name of the attribute. It cannot be null.
   *
   * @return Returns the servlet container attribute with the given name, or
   * null if there is no attribute by that name.
   */
  public Object getAttribute(final String name) {
    return delegate.getAttribute(name);
  }

  /** Returns all the attribute names.
   *
   * @return Returns an Enumeration containing the attribute names available
   * within this servlet context.
   */
  @SuppressWarnings("unchecked")
  public Enumeration getAttributeNames() {
    return delegate.getAttributeNames();
  }

  /** {@inheritDoc}
   */
  public ServletContext getContext(final String uripath) {
    return delegate.getContext(uripath);
  }

  /** {@inheritDoc}
   */
  public String getContextPath() {
    return delegate.getContextPath();
  }

  /** {@inheritDoc}
   */
  public String getInitParameter(final String name) {
    return delegate.getInitParameter(name);
  }

  /** {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Enumeration getInitParameterNames() {
    return delegate.getInitParameterNames();
  }

  /** {@inheritDoc}
   */
  public String getMimeType(final String file) {
    return delegate.getMimeType(file);
  }

  /** {@inheritDoc}
   *
   * Updating the servlet version forces a change in this operation.
   */
  public int getMajorVersion() {
    return SERVLET_VERSION_MAJOR;
  }

  /** {@inheritDoc}
   *
   * Updating the servlet version forces a change in this operation.
   */
  public int getMinorVersion() {
    return SERVLET_VERSION_MINOR;
  }

  /** {@inheritDoc}
   */
  public RequestDispatcher getNamedDispatcher(final String name) {
    return delegate.getNamedDispatcher(name);
  }

  /** Returns a String containing the real path for a given virtual path.
   *
   * @param path The virtul path.
   *
   * @return the real path.
  */
  public String getRealPath(final String path) {
    return delegate.getRealPath(path);
  }

  /** {@inheritDoc}
   */
  public RequestDispatcher getRequestDispatcher(final String path) {
    return delegate.getRequestDispatcher(path);
  }

  /** {@inheritDoc}
   */
  public URL getResource(final String path) throws MalformedURLException {
    return delegate.getResource(path);
  }

  /** {@inheritDoc}
   */
  public InputStream getResourceAsStream(final String path) {
    return delegate.getResourceAsStream(path);
  }

  /** {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Set getResourcePaths(final String path) {
    return delegate.getResourcePaths(path);
  }

  /** Returns the name and version of the servlet container on which the
   * servlet is running.
   *
   * @return a string with the container name and version.
   */
  public String getServerInfo() {
    return delegate.getServerInfo();
  }

  /** {@inheritDoc}
   */
  public Servlet getServlet(final String name) throws ServletException {
    return delegate.getServlet(name);
  }

  /** Returns the name of this web application corresponding to this
   * ServletContext as specified in the deployment descriptor for this web
   * application by the display-name element.
   *
   * @return a string with the name of the application.
   */
  public String getServletContextName() {
    return delegate.getServletContextName();
  }

  /** Deprecated. As of Java Servlet API 2.1, with no replacement.
   *
   * This method was originally defined to return an Enumeration of all the
   * servlet names known to this context. In this version, this method always
   * returns an empty Enumeration and remains only to preserve binary
   * compatibility. This method will be permanently removed in a future
   * version of the Java Servlet API.
   *
   * @return Returns an empty enumeration.
   */
  @SuppressWarnings("unchecked")
  public Enumeration getServletNames() {
    return delegate.getServletNames();
  }

  /** Deprecated. As of Java Servlet API 2.0, with no replacement.
   *
   * This method was originally defined to return an Enumeration of all the
   * servlets known to this servlet context. In this version, this method
   * always returns an empty enumeration and remains only to preserve binary
   * compatibility. This method will be permanently removed in a future
   * version of the Java Servlet API.
   *
   * @return Returns an empty enumeration.
   */
  @SuppressWarnings("unchecked")
  public Enumeration getServlets() {
    return delegate.getServlets();
  }

  /** Deprecated. As of Java Servlet API 2.1, use log(String message,
   * Throwable throwable) instead.
   *
   * This method was originally defined to write an exception's stack trace
   * and an explanatory error message to the servlet log file.
   *
   * @param exception the exception to log.
   *
   * @param msg a String specifying the message to be written to the log file.
   */
  public void log(final Exception exception, final String msg) {
    delegate.log(exception, msg);
  }

  /** Writes the specified message to a servlet log file, usually an event
   * log.
   *
   * @param msg a String specifying the message to be written to the log file.
   */
  public void log(final String msg) {
    delegate.log(msg);
  }

  /** Writes an explanatory message and a stack trace for a given Throwable
   * exception to the servlet log file.
   *
   * @param message a String specifying the message to be written to the log
   * file.
   *
   * @param throwable the Throwable error or exception.
   */
  public void log(final String message, final Throwable throwable) {
    delegate.log(message, throwable);
  }

  /** Removes the attribute with the given name from the servlet context.
   *
   * @param  name a String specifying the name of the attribute to be removed.
   */
  public void removeAttribute(final String name) {
    delegate.removeAttribute(name);
  }

  /** Binds an object to a given attribute name in this servlet context.
   *
   * @param name a String specifying the name of the attribute.
   *
   * @param object an Object representing the attribute to be bound.
   */
  public void setAttribute(final String name, final Object object) {
    delegate.setAttribute(name, object);
  }
}

