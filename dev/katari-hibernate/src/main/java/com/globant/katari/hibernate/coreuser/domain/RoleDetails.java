/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import java.util.Set;

import org.acegisecurity.userdetails.UserDetails;

/** Maps the acegi granted authorities to katari role domain objects.
 *
 * This is an interface so that implementations can decide how this mapping is
 * done. Normally, after login, the application loads a user from the database,
 * including it's roles. In that case, the implementation can simply return the
 * list of roles. Other alternative is to go to the database with the granted
 * authorities and load the corresponding roles.
 *
 * @author gerardo.bercovich
 */
public interface RoleDetails extends UserDetails {

  /** Returns the roles of the user.
   *
   * @return the roles. Implementations must never return null.
   */
  Set<Role> getUserRoles();
}

