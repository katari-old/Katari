/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.user.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.globant.katari.sample.testsupport.SecurityTestUtils;
import com.globant.katari.hibernate.role.domain.Role;
import com.globant.katari.hibernate.role.domain.RoleRepository;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.user.domain.User;
import com.globant.katari.sample.user.domain.UserRepository;

/** Test the ViewUserCommand class.
 */
public class SaveUserCommandTest extends TestCase {

  /** The command to be tested, initialized to create users (with profile and
   * password).
   */
  private SaveUserCommand saveUserCommand;

  /** The command to be tested (with password only).
   */
  private SaveUserCommand changePasswordCommand;

  /** The user repository.
   */
  private UserRepository userRepository;

  /** The role repository.
   */
  private RoleRepository roleRepository;

  /** This is a set up method of this TestCase.
   */
  protected final void setUp() {
    userRepository = (UserRepository) SpringTestUtils
      .getBeanFactory().getBean("userRepository");
    roleRepository = (RoleRepository) SpringTestUtils
      .getBeanFactory().getBean("role.roleRepository");
    saveUserCommand = (SaveUserCommand) SpringTestUtils
      .getBeanFactory().getBean("editUserCommand");
    changePasswordCommand = (SaveUserCommand) SpringTestUtils.getBeanFactory()
      .getBean("passwordCommand");
    setUpUserRepository();
  }

  /** Removes the unneeded users.
   */
  private void setUpUserRepository() {
    for (User user : userRepository.getUsers()) {
      if (!user.getName().equals("admin")) {
        userRepository.remove(user);
      }
    }

    User admin = userRepository.findUserByName("admin");
    assertTrue(admin.isAdministrator());
    for (Role role : roleRepository.getRoles()) {
      if (!admin.getRoles().contains(role)) {
        roleRepository.remove(role);
      }
    }

    // Sets admin as the user performing the actions.
    SecurityTestUtils.setContextUser(admin);

    // Add new roles in the user repository.
    roleRepository.save(new Role("member"));
    roleRepository.save(new Role("moderator"));
    roleRepository.save(new Role("owner"));
    roleRepository.save(new Role("ADMINISTRATOR"));
    assertEquals(5, roleRepository.getRoles().size());

    // Add a user.
    User user = new User("UserTest", "mail@none");
    user.changePassword("pass");
    user.addRole(roleRepository.getRoles().get(1));
    user.addRole(roleRepository.getRoles().get(2));
    user.addRole(roleRepository.getRoles().get(3));
    userRepository.save(user);
  }

  /** Returns an array of roles ids.
   *
   * @param roles List of roles.
   *
   * @return an array of roles ids.
   */
  private List<String> getRolesId(final Collection<Role> roles) {
    List<String> roleIds = new ArrayList<String>();
    for (Role role : roles) {
      roleIds.add(String.valueOf(role.getId()));
    }
    return roleIds;
  }

  /** Test Execute. Edit user with same roles.
   */
  public final void testExecute_editUserName() {
    //  Add a user.
    User user = new User("newUser", "mail@none");
    userRepository.save(user);

    user = userRepository.findUserByName("newUser");
    String newName = "NewName";
    saveUserCommand.setUserId(user.getId());
    saveUserCommand.getProfile().setName(newName);
    saveUserCommand.getProfile().setEmail(user.getEmail());
    saveUserCommand.getProfile().setRoleIds(getRolesId(user.getRoles()));
    saveUserCommand.execute();

    user = userRepository.findUserByName(newName);
    assertNotNull(user);
  }

  /** Test Execute. Edit user with sames roles.
   */
  public final void testExecute_editUserSameRoles() {
    User user = userRepository.findUserByName("UserTest");
    int roles = user.getRoles().size();
    saveUserCommand.setUserId(user.getId());
    saveUserCommand.getProfile().setName(user.getName());
    saveUserCommand.getProfile().setEmail(user.getEmail());

    // Sets the sames roles.
    saveUserCommand.getProfile().setRoleIds(getRolesId(user.getRoles()));
    saveUserCommand.execute();

    // Verify the results.
    user = userRepository.findUserByName("UserTest");
    assertNotNull(user);
    assertEquals(roles, user.getRoles().size());
  }

  /** Test Execute. Edit user with new roles.
   */
  public final void testExecute_editUserNewRoles() {
    User user = userRepository.findUserByName("UserTest");
    saveUserCommand.setUserId(user.getId());
    saveUserCommand.getProfile().setName(user.getName());
    saveUserCommand.getProfile().setEmail(user.getEmail());

    // Sets new roles.
    saveUserCommand.getProfile().setRoleIds(getRolesId(
          roleRepository.getRoles()));
    saveUserCommand.execute();

    // Verify the results.
    user = userRepository.findUserByName("UserTest");
    assertNotNull(user);
    assertEquals(roleRepository.getRoles().size(), user.getRoles().size());
  }

  /** Test Execute. Edit user with one of existing roles.
   */
  public final void testExecute_editUserOneExistingRole() {
    User user = userRepository.findUserByName("UserTest");
    saveUserCommand.setUserId(user.getId());
    saveUserCommand.getProfile().setName(user.getName());
    saveUserCommand.getProfile().setEmail(user.getEmail());

    // Sets only one of the existing roles.
    List<Role> roles = new ArrayList<Role>();
    roles.add(user.getRoles().iterator().next());
    saveUserCommand.getProfile().setRoleIds(getRolesId(roles));
    saveUserCommand.execute();

    // Verify the results.
    user = userRepository.findUserByName("UserTest");
    assertNotNull(user);
    assertEquals(1, user.getRoles().size());
  }

  /** Test Execute. Edit user with one of existing roles.
   */
  public final void testExecute_editUserOneExistingAndNewRole() {
    User user = userRepository.findUserByName("UserTest");
    saveUserCommand.setUserId(user.getId());
    saveUserCommand.getProfile().setName(user.getName());
    saveUserCommand.getProfile().setEmail(user.getEmail());

    // Sets a new role one of the existing roles.
    List<Role> roles = new ArrayList<Role>();
    roles.add(user.getRoles().iterator().next());
    roles.add(roleRepository.getRoles().get(0));
    saveUserCommand.getProfile().setRoleIds(getRolesId(roles));
    saveUserCommand.execute();

    // Verify the results.
    user = userRepository.findUserByName("UserTest");
    assertNotNull(user);
    assertEquals(2, user.getRoles().size());
  }

  /** Tests execute for a new user.
   */
  public final void testExecute_newUser() {
    saveUserCommand.getProfile().setName("TestUserName");
    saveUserCommand.getProfile().setEmail("mail@mail.com");
    saveUserCommand.getProfile().setRoleIds(getRolesId(
          roleRepository.getRoles()));
    saveUserCommand.execute();

    User user = userRepository.findUserByName("TestUserName");
    assertNotNull(user);
    assertEquals(roleRepository.getRoles().size(), user.getRoles().size());
  }

  /** Test change password.
   */
  public final void testExecute_changePassword() {

    // changePasswordCommand

    User user = userRepository.findUserByName("UserTest");

    changePasswordCommand.setUserId(user.getId());
    changePasswordCommand.getPassword().setOldPassword("pass");
    changePasswordCommand.getPassword().setNewPassword("newPassword");
    changePasswordCommand.getPassword().setConfirmedPassword("newPassword");
    changePasswordCommand.execute();

    user = userRepository.findUserByName("UserTest");
    assertNotNull(user);
    assertTrue("Password not correctly changed",
        user.validatePassword("newPassword"));
  }

  /** Tests that the NewUserValidator correctly validates a valid user.
   */
  public final void testValidate_success() throws Exception {

    SaveUserCommand user = (SaveUserCommand) SpringTestUtils.getBeanFactory()
        .getBean("createUserCommand");
    user.getProfile().setName("user01");
    user.getProfile().setEmail("user01@hotmail.com");
    user.getPassword().setNewPassword("123456");
    user.getPassword().setConfirmedPassword("123456");
    List<String> roleIds = new ArrayList<String>();
    roleIds.add("1");
    user.getProfile().setRoleIds(roleIds);

    Errors errors = new BindException(user, user.getClass().getName());
    user.validate(errors);
    assertFalse(errors.hasErrors());
  }

  /** Tests that the NewUserValidator correctly fails an invalid user.
   */
  public final void testValidate_error() throws Exception {

    //Fails because it has an empty name.
    SaveUserCommand user = (SaveUserCommand) SpringTestUtils.getBeanFactory()
        .getBean("createUserCommand");
    user.getProfile().setName("");
    user.getProfile().setEmail("user02@hotmail.com");
    user.getPassword().setNewPassword("123456");
    user.getPassword().setConfirmedPassword("123456");
    List<String> roleIds = new ArrayList<String>();
    roleIds.add("1");
    user.getProfile().setRoleIds(roleIds);

    Errors errors = new BindException(user, user.getClass().getName());
    user.validate(errors);
    assertEquals(1, errors.getAllErrors().size());

    /* Fails because it has an empty email and the password lenght is
     * less than 6.
     */
    user.getProfile().setName("User01");
    user.getProfile().setEmail("");
    user.getPassword().setNewPassword("12345");
    user.getPassword().setConfirmedPassword("12345");

    errors = new BindException(user, user.getClass().getName());
    user.validate(errors);
    assertEquals(2, errors.getAllErrors().size());
  }

  /** Test change password validation.
   */
  public final void testValidate_changePassword() {

    User user = userRepository.findUserByName("UserTest");

    changePasswordCommand.setUserId(user.getId());
    changePasswordCommand.getPassword().setOldPassword("passWrong");
    changePasswordCommand.getPassword().setNewPassword("newPasswordWrong");
    changePasswordCommand.getPassword().setConfirmedPassword("newPassword");
    changePasswordCommand.init();

    Errors errors = new BindException(changePasswordCommand,
        changePasswordCommand.getClass().getName());
    changePasswordCommand.validate(errors);

    assertEquals(2, errors.getAllErrors().size());
  }

  /* Tests if non admin users cannot create other users.
   */
  public final void testExecute_createNoPrivileges() throws Exception {

    User userTest = userRepository.findUserByName("UserTest");
    SecurityTestUtils.setContextUser(userTest);

    try {
      saveUserCommand.execute();
      fail("Non admin user can create new users");
    } catch (Exception e) {
    }
  }

  /* Tests if non admin users cannot modify other users.
   */
  public final void testExecute_modifyNoPrivileges() throws Exception {

    User userTest = userRepository.findUserByName("UserTest");
    SecurityTestUtils.setContextUser(userTest);

    User admin = userRepository.findUserByName("admin");
    saveUserCommand.setUserId(admin.getId());

    try {
      saveUserCommand.execute();
      fail("Non admin user can modify other users");
    } catch (Exception e) {
    }
  }

  /* Tests if non admin users can modify themselves.
   */
  public final void testExecute_modifyMyself() throws Exception {

    User userTest = userRepository.findUserByName("UserTest");
    SecurityTestUtils.setContextUser(userTest);

    saveUserCommand.setUserId(userTest.getId());
    saveUserCommand.init();
    saveUserCommand.getProfile().setName("Changed name");

    saveUserCommand.execute();

    userTest = userRepository.findUserByName("Changed name");
    assertNotNull(userTest);
  }
}

