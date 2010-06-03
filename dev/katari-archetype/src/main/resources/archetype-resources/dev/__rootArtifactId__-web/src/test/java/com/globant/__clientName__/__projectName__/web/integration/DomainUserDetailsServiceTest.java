#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.${clientName}.${projectName}.web.integration;

import junit.framework.TestCase;

import org.acegisecurity.userdetails.UserDetails;

import com.globant.${clientName}.${projectName}.web.testsupport.SpringTestUtils;
import com.globant.${clientName}.${projectName}.web.user.domain.UserRepository;

/* Tests the user details service implementation that gets the user information
 * from the application domain model.
 */
public class DomainUserDetailsServiceTest extends TestCase {

  /* Test the loadUserByUsername method. Passes a valid username.
  */
  public final void testLoadUserByUsername() {

    UserRepository repository = (UserRepository)
        SpringTestUtils.getBeanFactory().getBean("userRepository");
    DomainUserDetailsService userDetailsService;
    userDetailsService = new DomainUserDetailsService(repository);

    UserDetails  userDetails;
    userDetails = userDetailsService.loadUserByUsername("admin");

    assertTrue(userDetails instanceof DomainUserDetails);
    assertEquals("admin", userDetails.getUsername());
    assertEquals("admin", userDetails.getPassword());

    assertEquals(1, userDetails.getAuthorities().length);
  }
}

