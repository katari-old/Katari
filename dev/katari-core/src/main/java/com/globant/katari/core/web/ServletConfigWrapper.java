/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.lang.Validate;

/** Provides a convenient implementation of the ServletConfig interface that
 * can be subclassed by developers wishing to adapt the servlet config.
 *
 * This class implements the Wrapper or Decorator pattern. Methods default to
 * calling through to the wrapped servlet config object.
 *
 * As a convenience, this class also provides a way to replace the servlet
 * context. This is the most common use of this clas in katari.
 */
public class ServletConfigWrapper implements ServletConfig {

  /** The wrapped servlet config.
   *
   * It is never null.
   */
  private ServletConfig delegate;

  /** The option servlet context to use.
   *
   * If not null, getServletContext returns this object, otherwise it returns
   * the ServletContext of the wrapper config.
   */
  private ServletContext context = null;

  /** Constructs a servletConfig object wrapping the given config.
   *
   * @param theDelegate The wrapped servlet config. It cannot be null.
   */
  public ServletConfigWrapper(final ServletConfig theDelegate) {
    Validate.notNull(theDelegate, "The servlet config cannot be null");
    delegate = theDelegate;
  }

  /** Constructs a servletConfig object wrapping the given config and servlet
   * context.
   *
   * @param theDelegate The wrapped servlet config. It cannot be null.
   *
   * @param theContext The ServletContext to return when {@link
   * #getServletContext()} is called. It cannot be null.
   */
  public ServletConfigWrapper(final ServletConfig theDelegate,
      final ServletContext theContext) {
    Validate.notNull(theDelegate, "The servlet config cannot be null");
    Validate.notNull(theContext, "The servlet context cannot be null");
    delegate = theDelegate;
    context = theContext;
  }

  /** {@inheritDoc}
   */
  public String getInitParameter(final String name) {
    return delegate.getInitParameter(name);
  }

  /** {@inheritDoc}
  */
  public Enumeration<?> getInitParameterNames() {
    return delegate.getInitParameterNames();
  }

  /** {@inheritDoc}
  */
  public ServletContext getServletContext() {
    if (context != null) {
      return context;
    } else {
      return delegate.getServletContext();
    }
  }

  /** {@inheritDoc}
   */
  public String getServletName() {
    return delegate.getServletName();
  }
}

