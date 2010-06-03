/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.integration;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.Authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.sample.user.domain.User;

/** Security related utilities.
 *
 * This class provides operations to obtain information about the currently
 * logged on user.
 *
 * TODO There is a class named SecurityUtils in katari-core, check if it is ok.
 *
 * @author nicolas.frontini
 */
public final class SecurityUtils {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(SecurityUtils.class);

  /** A private constructor, so nobody can create instances.
   */
  private SecurityUtils() {
  }

  /** Obtains the currently logged in user.
   *
   * @return the currently logged in user. It is a detached User instance. It
   * returns null, if no user has logged in yet.
   */
  public static User getCurrentUser() {
    log.trace("Entering getCurrentUser");

    // Obtains the current user.
    Authentication authentication;
    authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      log.trace("Leaving getCurrentUser with null");
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal == null) {
      log.trace("Leaving getCurrentUser with null");
      return null;
    }

    User user = ((DomainUserDetails) principal).getUser();

    log.trace("Leaving getCurrentUser");
    return user;
  }
}

