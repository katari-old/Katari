/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.login;

import javax.servlet.Filter;

import org.apache.commons.lang.Validate;

import org.acegisecurity.ui.AuthenticationEntryPoint;

/** Holds the classes that will handle login and logout requests.
 *
 * This class is used to configure a module to be a login provider. The module
 * passes an instance of this class to the ModuleContext, and katari registers
 * the classes in the security chain.
 *
 * There are three main objects here:<br>
 *
 * - the entry point: used when a user wants to access a secured page and has
 *   not been authenticated, for example, redirects the user to the login
 *   page.<br>
 *
 * - The processingFilter: used to handle an authentication request, for
 *   example after the user submitted the login page.<br>
 *
 * - The logoutFilter: used to handle a logount request.<br>
 */
public class LoginProvider {

  /** Handles the initiation of the authentication process.
   *
   * It cannot be null.
   */
  private AuthenticationEntryPoint entryPoint;

  /** The filter responsible for processing authentication requests.
   *
   * It is never null.
   */
  private Filter processingFilter;

  /** The logout filter.
   *
   * It is never null.
   */
  private Filter logoutFilter;

  /** The class constructor.
   *
   * @param theEntryPoint the entry point. It cannot be null.
   *
   * @param theProcessingFilter the processing filter. It cannot be null.
   *
   * @param theLogoutFilter the logout filter. It cannot be null.
   */
  public LoginProvider(final AuthenticationEntryPoint theEntryPoint, final
      Filter theProcessingFilter, final Filter theLogoutFilter) {
    Validate.notNull(theEntryPoint, "The entry point cannot be null");
    Validate.notNull(theProcessingFilter, "The processing filter cannot be"
        + " null");
    Validate.notNull(theLogoutFilter, "The logout filter cannot be null");

    entryPoint = theEntryPoint;
    processingFilter = theProcessingFilter;
    logoutFilter = theLogoutFilter;
  }

  /** Gets the object that initiates the authentication process.
   *
   * @return the serviceUrlFilter. It never returns null.
   */
  public AuthenticationEntryPoint getEntryPoint() {
    return entryPoint;
  }

  /** Gets the proccessing filter, the object responsible for processing
   * authentication requests.
   *
   * @return the processingFilter. It never returns null.
   */
  public Filter getProcessingFilter() {
    return processingFilter;
  }

  /** Gets the filter that handles logout requests.
   *
   * @return the logout filter. It never returns null.
   */
  public Filter getLogoutFilter() {
    return logoutFilter;
  }
}

