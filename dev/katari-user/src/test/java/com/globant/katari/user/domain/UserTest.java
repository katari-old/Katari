/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.domain;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;

import com.globant.katari.user.SpringTestUtils;

/* This class represents a TestCase of the user. In this class we will test all
 * the features of the user.
 */
public class UserTest extends TestCase {

  /* Tests the modification of a Role.
   */
  public final void testModifyRole() {
    Role role = new Role("Old name");
    role.modify("MyRole");
    assertEquals(role.getName(), "MyRole");
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

