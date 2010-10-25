/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.integration;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.acegisecurity.userdetails.UserDetails;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.katari.user.SpringTestUtils;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserFilter;
import com.globant.katari.user.domain.UserRepository;

/* Tests the user details service implementation that gets the user information
 * from the application domain model.
 */
public class DomainUserDetailsServiceTest {

  private UserRepository repository;

  @Before
  public final void setUp() {
    SpringTestUtils.beginTransaction();
    repository = (UserRepository) SpringTestUtils.getBean(
        "user.userRepository");

    RoleRepository roleRepository = (RoleRepository) SpringTestUtils.getBean(
        "coreuser.roleRepository");
    
    Role adminRole = roleRepository.findRoleByName("ADMINISTRATOR");
    if (adminRole == null) {
      roleRepository.save(new Role("ADMINISTRATOR"));
      adminRole = roleRepository.findRoleByName("ADMINISTRATOR");
    }

    while (repository.getUsers(new UserFilter()).size() != 0) {
      for (User user : repository.getUsers(new UserFilter())) {
        repository.remove(user);
      }
    }

    // Add a user.
    User user = new User("admin", "admin@none");
    user.changePassword("admin");
    user.addRole(adminRole);
    repository.save(user);
  }


  /* Test the loadUserByUsername method. Passes a valid username.
  */
  @Test
  public final void testLoadUserByUsername() {

    DomainUserDetailsService userDetailsService;
    userDetailsService = new DomainUserDetailsService(repository);

    UserDetails  userDetails;
    userDetails = userDetailsService.loadUserByUsername("admin");

    assertThat(userDetails, is(DomainUserDetails.class));
    assertThat(userDetails.getUsername(), is("admin"));
    assertThat(userDetails.getPassword(), is("admin"));

    assertThat(userDetails.getAuthorities().length, is(1));
  }
}

