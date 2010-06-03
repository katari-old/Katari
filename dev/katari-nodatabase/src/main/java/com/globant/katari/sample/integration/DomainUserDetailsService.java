/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.lang.Validate;

import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UserDetails;

import com.globant.katari.sample.user.domain.User;

/** A user details service needed by acegi, that that obtains the user
 * information from the application domain.
 */
public class DomainUserDetailsService implements UserDetailsService {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(DomainUserDetailsService.class);

  /** Obtains the user details from a user name.
   *
   * This is a test implementation that considers a user valid if the username
   * and password are the same.
   *
   * @param username The user name to search. This cannot be null.
   *
   * @return Returns the user details needed by acegi. Specifically, an
   * instance of DomainUserDetails.
   */
  public UserDetails loadUserByUsername(final String username) {

    log.trace("Entering loadUserByUsername");

    Validate.notNull(username, "The username cannot be null");

    User user = new User(username, username);
    user.changePassword(username);

    UserDetails userDetails = new DomainUserDetails(user);

    log.trace("Leaving loadUserByUsername");
    return userDetails;
  }
}

