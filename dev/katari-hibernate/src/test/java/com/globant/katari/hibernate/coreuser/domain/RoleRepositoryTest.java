/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4
    .AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Tests Role repository with a db datasources for testing purpose.
 * @author gerardo.bercovich
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=false)
@ContextConfiguration(locations = {
    "classpath:com/globant/katari/hibernate/coreuser/applicationContext.xml"
  })
public class RoleRepositoryTest
    extends AbstractTransactionalJUnit4SpringContextTests {

  /**
   * This is the implementation of the repository of the role.
   * Injected by Spring.
   */
  @Autowired
  private RoleRepository roleRepository = null;

  /**
   * The name of administrator role.
   */
  private final String ADMIN_ROLE_NAME = "ADMINISTRATOR";

  /** Creates administrator role.
   */
  @Before
  public void onSetUp() throws Exception {
    Role newRole = new Role(ADMIN_ROLE_NAME);
    roleRepository.save(newRole);
  }

  /**
   * Deletes all the roles.
   */
  @After
  public void onTearDown() throws Exception {
    this.deleteFromTables(new String[]{"roles"});
  }

  /** Searches for a known role.
   */
  @Test
  public void testFindRoleByName() throws Exception {
    Role adminRole = roleRepository.findRoleByName(ADMIN_ROLE_NAME);
    assertNotNull(adminRole);
    assertEquals(adminRole.getName(), ADMIN_ROLE_NAME);
  }

  /** Finds roles.
   */
  @Test
  public void testGetRoles() throws Exception {
    final List<Role> roles = roleRepository.getRoles();
    assertEquals(1, roles.size());
  }

  @Test
  public void testGetRolesById() throws Exception {
    final long id = roleRepository.findRoleByName(ADMIN_ROLE_NAME).getId();
    final ArrayList<String> ids = new ArrayList<String>();
    ids.add(Long.toString(id));
    final List<Role> roles = roleRepository.getRoles(ids);
    assertEquals(1, roles.size());
    assertEquals(id, roles.get(0).getId());
  }

  public RoleRepository getRoleRepository() {
    return roleRepository;
  }

  public void setRoleRepository(final RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }
}

