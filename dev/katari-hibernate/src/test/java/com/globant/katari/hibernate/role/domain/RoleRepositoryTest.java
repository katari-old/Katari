package com.globant.katari.hibernate.role.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * Tests Role repository with a db datasources for testing purpose.
 * @author gerardo.bercovich
 */
public class RoleRepositoryTest extends
    AbstractTransactionalDataSourceSpringContextTests {

  /**
   * This is the implementation of the repository of the role.
   * Injected by Spring.
   */
  private RoleRepository roleRepository = null;

  /**
   * The name of administrator role.
   */
  private final String ADMIN_ROLE_NAME = "ADMINISTRATOR";

  /**
   * Defines constructor for disabling rollback only transactions.
   */
  public RoleRepositoryTest() {
    this.setDefaultRollback(false);
  }

  /**
   * Configures application context xml file.
   */
  @Override
  protected String[] getConfigLocations() {
    return new String[] {
        "classpath:com/globant/katari/hibernate/role/applicationContext.xml" };
  }

  /**
   * Creates administrator role.
   */
  @Override
  protected void onSetUp() throws Exception {
    Role newRole = new Role(ADMIN_ROLE_NAME);
    roleRepository.save(newRole);
  }

  /**
   * Deletes all the roles.
   */
  @Override
  protected void onTearDown() throws Exception {
    this.deleteFromTables(new String[]{"roles"});
  }

  /** Searches for a known role.
   */
  public void testFindRoleByName() throws Exception {
    Role adminRole = roleRepository.findRoleByName(ADMIN_ROLE_NAME);
    assertNotNull(adminRole);
    assertEquals(adminRole.getName(), ADMIN_ROLE_NAME);
  }

  /** Finds roles.
   */
  public void testGetRoles() throws Exception {
    final List<Role> roles = roleRepository.getRoles();
    assertEquals(1, roles.size());
  }

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

