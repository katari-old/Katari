package com.globant.katari.hibernate.coreuser.domain;

import java.util.Set;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Role Security related utilities.
 *
 * This class provides operations to obtain information about the currently
 * logged on user roles.
 *
 * @author gerardo.bercovich
 */
public final class RoleSecurityUtils {

  /**
   * Utility class private constructor.
   */
  private RoleSecurityUtils() {
  }

  /**
   * The class logger.
   */
  private static Log log = LogFactory.getLog(RoleSecurityUtils.class);

  /**
   * Obtains the roles of the currently logged on user.
   *
   * @return a set the currently logged on user. It returns null only if the
   * user has not logged in yet.
   */
  public static Set<Role> getCurrentUserRoles() {
    log.trace("Entering getCurrentUserRoles");

    Set<Role> roles = null;

    // Obtains the current user definition.
    Object principal = SecurityContextHolder.getContext().getAuthentication()
       .getPrincipal();
    if (principal != null) {
      if (!(principal instanceof RoleDetails)) {
       throw new RuntimeException("The currently logged on user does not"
           + " implement RoleDetails.");
      }
      roles = ((RoleDetails) principal).getUserRoles();
    }

    log.trace("Leaving getCurrentUserRoles");
    return roles;
  }
}

