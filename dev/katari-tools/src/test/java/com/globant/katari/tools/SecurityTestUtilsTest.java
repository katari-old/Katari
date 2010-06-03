/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import org.acegisecurity.Authentication;

import org.acegisecurity.context.SecurityContextHolder;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/* Test the HibernateUtils.
 */
public class SecurityTestUtilsTest {

  @Test
  public void testFakeUser() throws Exception {

    SecurityTestUtils.fakeUser("admin", "ADMINISTRATOR");
    Authentication authentication;
    authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication.getPrincipal(), is((Object) "admin"));
    assertThat(authentication.getAuthorities().length, is(1));
    assertThat(authentication.getAuthorities()[0].getAuthority(),
        is("ADMINISTRATOR"));
  }
}

