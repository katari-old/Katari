package com.globant.katari.hibernate.coreuser.domain;

import java.util.HashSet;
import java.util.Set;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.lang.Validate;
import org.easymock.classextension.EasyMock;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * RoleSecurityUtils Test Case.
 * @author gerardo.bercovich
 */
public class RoleSecurityUtilsTest extends
    AbstractTransactionalDataSourceSpringContextTests {

  /**
   * This is the implementation of the repository of the role.
   * Injected by Spring.
   */
  private RoleRepository roleRepository = null;

  /**
   * Configures application context xml file.
   */
  @Override
  protected String[] getConfigLocations() {
    return new String[] {
        "classpath:com/globant/katari/hibernate/coreuser/applicationContext.xml" };
  }

  @Override
  protected void onSetUp() throws Exception {
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

  public void testGetCurrentUserRoles() {
    final Set<Role> currentUserRoles = RoleSecurityUtils.getCurrentUserRoles();
    assertEquals(1, currentUserRoles.size());
    assertEquals("ADMINISTRATOR", currentUserRoles.iterator().next().getName());
  }

  public void test_Exception_Wrong_userDetails() throws Exception {
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

  // Spring Accessors

  public RoleRepository getRoleRepository() {
    return roleRepository;
  }

  public void setRoleRepository(final RoleRepository theRoleRepository) {
    Validate.notNull(theRoleRepository, "The roleRepository cannot be null.");
    roleRepository = theRoleRepository;
  }
}

