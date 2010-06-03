/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import org.apache.commons.lang.Validate;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import static org.easymock.EasyMock.*;

/** Utility class to help test stuff that is dependent on the logged on user.
 */
public final class SecurityTestUtils {

  /** Private contructor, this is a utility class.
   */
  private SecurityTestUtils() {
  }

  /** Sets the user that will be seen as performing an action.
   *
   * Sets in the acegi security context holder, a user with the specified role.
   *
   * @param userName The name of the fake user. It cannot be null.
   *
   * @param roleName The name of the role that will be assigned to the fake
   * user. It cannot be null.
   */
  public static void fakeUser(final String userName, final String roleName) {

    Validate.notNull(userName, "The user name cannot be null");
    Validate.notNull(roleName, "The role name cannot be null");

    GrantedAuthority authority  = createMock(GrantedAuthority.class);
    expect(authority.getAuthority()).andReturn(roleName).anyTimes();
    replay(authority);

    GrantedAuthority[] authorities = new GrantedAuthority[] {authority};
    UsernamePasswordAuthenticationToken authentication;
    authentication = new UsernamePasswordAuthenticationToken(userName, "-----",
        authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}

