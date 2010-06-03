/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.integration;

import junit.framework.TestCase;

import org.acegisecurity.userdetails.UserDetails;

/* Tests the user details service implementation that gets the user information
 * from the application domain model.
 */
public class DomainUserDetailsServiceTest extends TestCase {

  /* Test the loadUserByUsername method. Passes a valid username.
  */
  public final void testLoadUserByUsername() {

    DomainUserDetailsService userDetailsService;
    userDetailsService = new DomainUserDetailsService();

    UserDetails  userDetails;
    userDetails = userDetailsService.loadUserByUsername("admin");

    assertTrue(userDetails instanceof DomainUserDetails);
    assertEquals("admin", userDetails.getUsername());

    assertEquals(1, userDetails.getAuthorities().length);
  }
}

