/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import java.util.HashSet;
import java.util.Set;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.lang.Validate;
import org.easymock.EasyMock;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.hibernate.SpringTestUtils;

/**
 * RoleSecurityUtils Test Case.
 * @author gerardo.bercovich
 */
public class RoleSecurityUtilsTest {

  /**
   * This is the implementation of the repository of the role.
   */
  private RoleRepository roleRepository = null;

  @Before
  public void setUp() throws Exception {

    roleRepository = (RoleRepository) SpringTestUtils.get().getBean(
        "coreuser.roleRepository");
    String roleName = "ADMINISTRATOR";
    Set<Role> roles = new HashSet<Role>();
    roleRepository.save(new Role(roleName));
    roles.add(roleRepository.findRoleByName(roleName));

    RoleDetails roleDetailsMock = EasyMock.createMock(RoleDetails.class);
    EasyMock.expect(roleDetailsMock.getUserRoles()).andReturn(roles).times(2);
    EasyMock.replay(roleDetailsMock);
    UsernamePasswordAuthenticationToken authentication;
    authentication = new UsernamePasswordAuthenticationToken(
        roleDetailsMock, "admin");
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test public void testGetCurrentUserRoles() {
    final Set<Role> currentUserRoles = RoleSecurityUtils.getCurrentUserRoles();
    assertThat(currentUserRoles.size(), is(1));
    assertThat(currentUserRoles.iterator().next().getName(),
        is("ADMINISTRATOR"));
  }

  @Test public void test_Exception_Wrong_userDetails() throws Exception {
    UserDetails userDetails = EasyMock.createMock(UserDetails.class);
    EasyMock.replay(userDetails);
    UsernamePasswordAuthenticationToken authentication;
    authentication = new UsernamePasswordAuthenticationToken(userDetails,
        "admin");
    SecurityContextHolder.getContext().setAuthentication(authentication);
    try {
      RoleSecurityUtils.getCurrentUserRoles();
      fail("The principal object type must be RoleUserDetail");
    } catch (Exception e) {
    }
  }
}

