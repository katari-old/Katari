#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.${clientName}.${projectName}.web.integration;

import java.util.ArrayList;
import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.apache.commons.lang.Validate;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleDetails;
import com.globant.${clientName}.${projectName}.web.user.domain.User;

/** A user details service needed by acegi, that obtains the user information
 * from the application domain.
 *
 * This implementation simply delegates the user details operations to the
 * wrapped user domain object.
 */
public class DomainUserDetails implements RoleDetails {

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

  /** An implementation of GrantedAuthority that wraps the name of the role the
   * user is related to.
   */
  private static final class RoleAuthority implements GrantedAuthority {

    /** The serialization version number.
     *
     * This number must change every time a new serialization incompatible
     * change is introduced in the class.
     */
    private static final long serialVersionUID = 20071018;

    /** The role name.
     *
     * This is never null.
     */
    private String name;

    /** Builds an instance of RoleAuthority.
     *
     * @param theName The role name. It cannot be null.
     */
    private RoleAuthority(final String theName) {
      name = theName;
    }

    /** Returns the authority name.
     *
     * @return the authtority name, in this implementation, this is the role
     * name.
     */
    public String getAuthority() {
      return name;
    }
  };

  /** Gets the authorities granted to the user, in this implementation, the
   * name of the roles that the user belongs to.
   *
   * @return Return the authorities granted to the user. It never returns null.
   */
  public GrantedAuthority[] getAuthorities() {
    ArrayList<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
    for (Role role : user.getRoles()) {
      result.add(new RoleAuthority("ROLE_" + role.getName()));
    }
    return result.toArray(new GrantedAuthority[result.size()]);
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

  /** Returns the list of roles of the logged in user.
   *
   * @return the set of roles, never returns null.
   */
  public Set<Role> getUserRoles() {
    return user.getRoles();
  }
}

