/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.globant.katari.hibernate.SpringTestUtils;

/**
 * Tests Role repository with a db datasources for testing purpose.
 * @author gerardo.bercovich
 */
public class RoleRepositoryTest {

  private RoleRepository roleRepository = null;

  private final String ADMIN_ROLE_NAME = "ADMINISTRATOR";

  /** Creates administrator role. */
  @Before
  public void onSetUp() throws Exception {

    roleRepository = (RoleRepository) SpringTestUtils.get().getBean(
        "coreuser.roleRepository");

    SpringTestUtils.get().beginTransaction();
    Role newRole = new Role(ADMIN_ROLE_NAME);
    roleRepository.save(newRole);
    SpringTestUtils.get().endTransaction();
  }

  /**
   * Deletes all the roles.
   */
  @After
  public void onTearDown() throws Exception {
    SpringTestUtils.get().beginTransaction();
    SQLQuery query;
    query = roleRepository.getSession().createSQLQuery(
        "TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
    query.executeUpdate();
    SpringTestUtils.get().endTransaction();
  }

  /** Searches for a known role.
   */
  @Test
  public void testFindRoleByName() throws Exception {
    SpringTestUtils.get().beginTransaction();
    Role adminRole = roleRepository.findRoleByName(ADMIN_ROLE_NAME);
    assertNotNull(adminRole);
    assertEquals(adminRole.getName(), ADMIN_ROLE_NAME);
    SpringTestUtils.get().endTransaction();
  }

  /** Finds roles.
   */
  @Test
  public void testGetRoles() throws Exception {
    SpringTestUtils.get().beginTransaction();
    final List<Role> roles = roleRepository.getRoles();
    assertEquals(1, roles.size());
    SpringTestUtils.get().endTransaction();
  }

  @Test
  public void testGetRolesById() throws Exception {
    SpringTestUtils.get().beginTransaction();
    final long id = roleRepository.findRoleByName(ADMIN_ROLE_NAME).getId();
    final ArrayList<String> ids = new ArrayList<String>();
    ids.add(Long.toString(id));
    final List<Role> roles = roleRepository.getRoles(ids);
    assertEquals(1, roles.size());
    assertEquals(id, roles.get(0).getId());
    SpringTestUtils.get().endTransaction();
  }
}

