#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.${clientName}.${projectName}.web.user.domain;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.${clientName}.${projectName}.web.testsupport.SpringTestUtils;

/* This class represents a TestCase of the user. In this class we will test all
 * the features of the user.
 */
public class UserTest extends TestCase {

  /* This is the implementation of the repository of the user.
  */
  private UserRepository userRepository;

  /* This is the implementation of the repository of the role.
   */
  private RoleRepository roleRepository;

  /* This is a set up method of this TestCase.
  */
  protected final void setUp() {
    userRepository = (UserRepository) SpringTestUtils.getBeanFactory().getBean(
        "userRepository");
    roleRepository = (RoleRepository) SpringTestUtils.getBeanFactory().getBean(
        "coreuser.roleRepository");
    addUsers();
  }

  /* Adds a pair of users to be used in the tests.
  */
  private void addUsers() {
    // Removes the unneeded users.
    for (Object element : userRepository.getUsers()) {
      User user = (User) element;
      if (!user.getName().equals("admin")) {
        userRepository.remove(user);
      }
    }

    // Creates an additional user.
    User user = new User("admin-2", "admin-2@none");
    user.changePassword("admin-2");
    user.addRole(roleRepository.findRoleByName("ADMINISTRATOR"));
    userRepository.save(user);
  }

  /* Tests the getUsers operation.
  */
  public final void testGetUsers() {
    List users = userRepository.getUsers();
    assertEquals(users.size(), 2);
  }

  /* Searches a user by an existing email and also search by a non-existing
   * email.
   */
  public final void testFindByEmail() {
    assertNotNull(userRepository.findUserByEmail("admin-2@none"));
    assertNull(userRepository.findUserByEmail("XXX"));
  }

  /* Searches a user by an existing name and also search by a non-existing
   * name.
   */
  public final void testFindByName() {
    assertNotNull(userRepository.findUserByName("admin-2"));
    assertNull(userRepository.findUserByName("XXX"));
  }

  /* Gets a user, modifies its name, saves it and gets it back.
  */
  public final void testModify() {
    User user = userRepository.findUserByName("admin-2");
    user.modify(user.getName(), "modified email");
    userRepository.save(user);
    assertNotNull(userRepository.findUserByEmail("modified email"));
  }

  /* Gets a user, removes it and verifies that it no longer exists.
  */
  public final void testRemove() {
    User user = userRepository.findUserByName("admin-2");
    userRepository.remove(user);
    assertEquals(userRepository.getUsers().size(), 1);
  }

  /* Finds a user and checks the name and email
  */
  public final void testFindUser() {
    User user1 = (User) userRepository.findUserByName("admin-2");
    User user2 = (User) userRepository.findUser(user1.getId());
    assertEquals("admin-2", user2.getName());
    assertEquals("admin-2@none", user2.getEmail());
  }

  /* Tests the modification of a Role.
   */
  public final void testModifyRole() {
    Role role = new Role("Old name");
    role.modify("MyRole");
    assertEquals(role.getName(), "MyRole");
  }

  /* Saves a Role
  */
  public final void testSaveRole() {
    Role role = new Role("MyRole");
    assertEquals(role.getId(), 0);
    roleRepository.save(role);
    assertTrue(role.getId() != 0);
  }

  /* Removes a Role
  */
  public final void testRemoveRole() {
    Role role = new Role("MyRole");
    roleRepository.remove(role);
  }

  /* Test the findRole method.
   */
  public final void testfindRole() {
    Role role = roleRepository.findRoleByName("ADMINISTRATOR");
    assertEquals("ADMINISTRATOR", role.getName());
    role = roleRepository.findRole(role.getId());
    assertEquals("ADMINISTRATOR", role.getName());
  }

  /* Test the user Add Role method.
   */
  public final void testAddRemoveRole() {
    Role role = new Role("MyRole");
    User user = new User("pablo", "admin@mail");
    assertTrue(user.addRole(role));
    Set<Role> roles = user.getRoles();
    assertEquals(roles.iterator().next().getName(), "MyRole");
    assertTrue(user.removeRole(role));
  }

  /* Test user validate password.
  */
  public final void testValidatePassword() {
    User user = new User("pablo", "admin@gmail");
    user.changePassword("MyPassword");
    assertTrue(user.validatePassword("MyPassword"));
    assertEquals(user.getPassword(), "MyPassword");
  }

  /* Tests if the user is correctly marked as an administrator.
   */
  public final void testIsAdministrator() {
    User user = new User("pablo", "admin@gmail");
    Role role = new Role("ADMINISTRATOR");
    user.addRole(role);
    assertTrue(user.isAdministrator());
  }
}

