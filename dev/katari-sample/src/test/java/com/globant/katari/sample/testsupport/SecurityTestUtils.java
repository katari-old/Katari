/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.testsupport;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import com.globant.katari.user.integration.DomainUserDetails;
import com.globant.katari.user.domain.User;

/** Utility class to give support to test cases.
 */
public final class SecurityTestUtils {

  /** The private constructor of an utility class.
   */
  private SecurityTestUtils() {
  }

  /** Sets the user in the acegi holder.
   *
   * This is the user that will perform the operations.
   *
   * @user The user that will be used to perform the operations. It can be
   * null, in which case no user will be seen as the actor.
   */
  public static void setContextUser(final User user) {
    DomainUserDetails domainUserDetails = new DomainUserDetails(user);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
        domainUserDetails, user.getPassword());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}

