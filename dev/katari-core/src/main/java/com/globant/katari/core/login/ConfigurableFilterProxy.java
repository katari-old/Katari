package com.globant.katari.core.login;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.Validate;

/** A filter that simply delegates all operations to a delegate.
 *
 * The delegate must be set through the setDelegate operation.
 */
public class ConfigurableFilterProxy implements Filter {

 /** The delegate filter.
  *
  * This is initially null, but it must be set to a non null value with
  * setDelegate before invoking the filter.
  */
  private Filter delegate = null;

  /** Sets the delegate wrapped by this proxy.
   *
   * @param theDelegate the delegate to set. It cannot be null.
   */
  public void setDelegate(final Filter theDelegate) {
    Validate.notNull(theDelegate, "The delegate cannot be null");
    delegate = theDelegate;
  }

  /** Initializes the delegate.
   *
   * This operation can only be called after setDelegate.
   *
   * {@inheritDoc}
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    if (delegate == null) {
      throw new RuntimeException("You must initialize the delegate with"
          + " setDelegate.");
    }
    delegate.init(filterConfig);
  }

  /** This method delegates the doFilter method to the delegate.
   *
   * This operation can only be called after setDelegate.
   *
   * {@inheritDoc}
   */
  public void doFilter(final ServletRequest request,
      final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {
    Validate.notNull(request, "The ServletRequest cannot be null");
    Validate.notNull(response, "The ServletResponse cannot be null");
    Validate.notNull(chain, "The FilterChain cannot be null");
    if (delegate == null) {
      throw new IllegalStateException("You must initialize the delegate with"
          + " setDelegate.");
    }
    delegate.doFilter(request, response, chain);
  }

  /** Destroys the delegate.
   *
   * This operation can only be called after setDelegate.
   */
  public void destroy() {
    if (delegate == null) {
      throw new RuntimeException("You must initialize the delegate with"
          + " setDelegate.");
    }
    delegate.destroy();
  }
}

