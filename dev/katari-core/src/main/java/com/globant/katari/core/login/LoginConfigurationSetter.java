/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.login;

import org.apache.commons.lang.Validate;

/** This class configures the login related delegates.
 *
 * There are three filters in acegi related to login and logout. These fiters
 * are configured with simple 'do-nothing' proxies that simply forward the work
 * to their respective delegates. This object is responsible for configuring
 * the delegates for those proxies.
 */
public class LoginConfigurationSetter {

  /** Tells if the login has been configured.
   */
  private boolean alreadyConfigured = false;

  /** The delegating entry point.
   *
   * This is never null.
   */
  private DelegatingEntryPoint entryPoint;

  /** The delegating authentication filter.
   *
   * This is never null.
   */
  private ConfigurableFilterProxy authFilter;

  /** The delegating logout filter.
   *
   * This is never null.
   */
  private ConfigurableFilterProxy logoutFilter;

  /** The class constructor.
   *
   * @param theEntryPoint the entry point. It cannot be null.
   *
   * @param theLogoutFilter the logout filter. It cannot be null.
   *
   * @param theAuthFilter the processing filter. It cannot be null.
   */
  public LoginConfigurationSetter(final DelegatingEntryPoint theEntryPoint,
      final ConfigurableFilterProxy theLogoutFilter,
      final ConfigurableFilterProxy theAuthFilter) {

    Validate.notNull(theEntryPoint, "The entry point cannot be null.");
    Validate.notNull(theAuthFilter, "The processing filter cannot be null.");
    Validate.notNull(theLogoutFilter, "The logouth filter cannot be null.");

    entryPoint = theEntryPoint;
    authFilter = theAuthFilter;
    logoutFilter = theLogoutFilter;
  }

  /** This method configures the login.
   *
   * @param provider the login provider. It is has the filters to be set in the
   * delegators. It cannot be null.
   *
   * TODO Why is this synchronized?
   */
  public void setLoginConfiguration(final LoginProvider provider) {
    synchronized (this) {
      if (alreadyConfigured) {
        throw new IllegalStateException(
            "A login provider has already been defined");
      }
      Validate.notNull(provider, "The login provider cannot be null.");
      entryPoint.setDelegate(provider.getEntryPoint());
      authFilter.setDelegate(provider.getProcessingFilter());
      logoutFilter.setDelegate(provider.getLogoutFilter());
      alreadyConfigured = true;
    }
  }
}

