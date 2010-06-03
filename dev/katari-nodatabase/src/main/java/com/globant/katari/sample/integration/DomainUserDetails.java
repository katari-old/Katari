/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.integration;

import org.acegisecurity.GrantedAuthority;
import org.apache.commons.lang.Validate;

import com.globant.katari.sample.user.domain.User;

import org.acegisecurity.userdetails.UserDetails;

/** A user details class needed by acegi, that obtains the user information
 * from the application domain.
 *
 * This implementation simply delegates the user details operations to the
 * wrapped user domain object, except for the authorities. All users in this
 * implementation has the ROLE_ADMINISTRATOR role.
 */
public class DomainUserDetails implements UserDetails {

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
  private User user;

  /** Builds the user details.
   *
   * @param theUser The domain user object. It cannot be null.
   */
  public DomainUserDetails(final User theUser) {
    Validate.notNull(theUser, "The user cannot be null");
    user = theUser;
  }

  /** Gets the authorities granted to the user, in this implementation, the
   * name of the roles that the user belongs to.
   *
   * @return Return the authorities granted to the user. It never returns null.
   */
  @SuppressWarnings("serial")
  public GrantedAuthority[] getAuthorities() {
    GrantedAuthority admin = new GrantedAuthority() {
      public String getAuthority() {
        return "ROLE_ADMINISTRATOR";
      }
    };
    GrantedAuthority[] authorities = new GrantedAuthority[1];
    authorities[0] = admin;
    return authorities;
  }

  /** Gets the password used to authenticate the user.
   *
   * @return a string with the password.
   */
  public String getPassword() {
    return user.getPassword();
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
  public User getUser() {
    return user;
  }
}

