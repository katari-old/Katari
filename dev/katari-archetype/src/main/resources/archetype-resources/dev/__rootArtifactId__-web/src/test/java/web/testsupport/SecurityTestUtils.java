#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package ${package}.web.testsupport;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import ${package}.web.integration.DomainUserDetails;
import ${package}.web.user.domain.User;

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

