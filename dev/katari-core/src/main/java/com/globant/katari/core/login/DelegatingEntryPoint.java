/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.ui.AuthenticationEntryPoint;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*** Used by the <code>ExceptionTranslation filter</code> to commence
 * authentication.
 */
public class DelegatingEntryPoint implements AuthenticationEntryPoint {

  /** The object that does the real work.
   *
   * This must be set before the first authentication request.
   */
  private AuthenticationEntryPoint delegate = null;

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(DelegatingEntryPoint.class);

  /** Commences an authentication scheme.
   *
   * SecurityEnforcementFilter will populate the HttpSession attribute named
   * AuthenticationProcessingFilter.ACEGI_SECURITY_TARGET_URL_KEY with the
   * requested target URL before calling this method.
   *
   * Implementations should modify the headers on the ServletResponse as
   * necessary to commence the authentication process.
   *
   * @param servletRequest The servlet request. It cannot be null.
   *
   * @param servletResponse The servlet response. It cannot be null.
   *
   * @param authenticationException This parameter is not used.
   *
   * @throws IOException in case of an io error.
   *
   * @throws ServletException in case of an unexpected error.
   */
  public void commence(final ServletRequest servletRequest, final
      ServletResponse servletResponse, final AuthenticationException
      authenticationException) throws IOException, ServletException {

    log.trace("Entering commence");

    if (delegate == null) {
      throw new IllegalStateException("You must specify the delegate");
    }

    delegate.commence(servletRequest, servletResponse,
        authenticationException);

    log.trace("Leaving commence");
  }

  /** Sets the delegate that handles the authentication startup.
   *
   * @param theDelegate The object that does the real work. It cannot be null.
   */
  public void setDelegate(final AuthenticationEntryPoint theDelegate) {
    Validate.notNull(theDelegate, "The delegate cannot be null");
    delegate = theDelegate;
  }
}

