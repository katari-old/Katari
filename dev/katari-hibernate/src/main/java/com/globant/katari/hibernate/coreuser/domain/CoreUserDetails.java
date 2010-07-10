/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.lang.Validate;

/** A user details service needed by acegi, that obtains the user information
 * from the application domain.
 *
 * This implementation simply delegates the user details operations to the
 * wrapped user domain object.
 */
public abstract class CoreUserDetails implements UserDetails {

  /** The serialization version number.
   *
   * This number must change every time a new serialization incompatible change
   * is introduced in the class.
   */
  private static final long serialVersionUID = 20071005;

  /** The domain user object.
   *
   * This is never null.
   */
  private CoreUser user;

  /** Builds the user details.
   *
   * @param theUser The domain user object. It cannot be null.
   */
  public CoreUserDetails(final CoreUser theUser) {
    Validate.notNull(theUser, "The user cannot be null");
    user = theUser;
  }

  /** Obtains the wrapped core user.
   *
   * @return the core user, never returns null.
   */
  public CoreUser getCoreUser() {
    return user;
  }

  /** Gets the username used to authenticate the user.
   *
   * @return a string with the username.
   */
  public String getUsername() {
    return user.getName();
  }

  /** Indicates whether the user's account has expired.
   *
   * TODO Implement this.
   *
   * @return true if the account has not expired, false if the account has
   * expired. This implementation always returns true.
   */
  public boolean isAccountNonExpired() {
    return true;
  }

  /** Indicates whether the user is locked or unlocked.
   *
   * TODO Implement this.
   *
   * @return true if the account is not locked, false if the account is locked.
   * This implementation always returns true.
   */
  public boolean isAccountNonLocked() {
    return true;
  }

  /** Indicates whether the user's credentials (password) has expired.
   *
   * TODO Implement this.
   *
   * @return true if the password has not expired, false if the password has
   * expired. This implementation always returns true.
   */
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /** Indicates whether the user is enabled or disabled.
   *
   * TODO Implement this.
   *
   * @return true if the account is enabled, false if the account is disabled.
   * This implementation always returns true.
   */
  public boolean isEnabled() {
    return true;
  }

  /** Returns the user domain.
   *
   * @return The user domain. Never returns null.
   */
  public CoreUser getUser() {
    return user;
  }
}

