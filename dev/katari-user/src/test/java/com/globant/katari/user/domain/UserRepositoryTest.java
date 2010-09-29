/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.domain;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.Before;
import org.junit.internal.matchers.TypeSafeMatcher;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;

import com.globant.katari.user.SpringTestUtils;

import com.globant.katari.user.domain.filter.ContainsFilter;
import com.globant.katari.user.domain.filter.Paging;
import com.globant.katari.user.domain.filter.Sorting;

public class UserRepositoryTest {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(UserRepositoryTest.class);

  /** This is the implementation of the repository of the user.
   */
  private UserRepository userRepository;

  /** This is the implementation of the repository of the role.
   */
  private RoleRepository roleRepository;

  /** This is a set up method of this TestCase.
   */
  @Before
  public final void setUp() {
    userRepository = (UserRepository) SpringTestUtils.getBeanFactory().getBean(
        "user.userRepository");
    roleRepository = (RoleRepository) SpringTestUtils.getBeanFactory().getBean(
        "coreuser.roleRepository");
    addUsersAndRoles();
    SpringTestUtils.beginTransaction();
  }

  /** Adds a pair of users to be used in the tests.
   */
  private void addUsersAndRoles() {
    log.trace("Entering addUsersAndRoles");

    //  Removes the unneeded users.
    for (User user : userRepository.getUsers(new UserFilter())) {
      log.debug("Found user " + user.getName());
      userRepository.remove(user);
    }
    //  Removes the unneeded roles.
    for (Role role : roleRepository.getRoles()) {
      log.debug("Found role " + role.getName());
      roleRepository.remove(role);
    }

    Role sampleRole = new Role("test-1");
    roleRepository.save(sampleRole);
    sampleRole = new Role("ADMINISTRATOR");
    roleRepository.save(sampleRole);

    // Add users.
    User user = new User("admin", "admin@none");
    user.changePassword("admin");
    user.addRole(sampleRole);
    user.addRole(roleRepository.findRoleByName("ADMINISTRATOR"));
    userRepository.save(user);

    user = new User("nico", "mail@none");
    user.changePassword("pass");
    user.addRole(sampleRole);
    user.addRole(roleRepository.findRoleByName("ADMINISTRATOR"));
    userRepository.save(user);

    user = new User("nicanor", "mail@none");
    user.changePassword("pass");
    user.addRole(sampleRole);
    user.addRole(roleRepository.findRoleByName("ADMINISTRATOR"));
    userRepository.save(user);
    log.trace("Leaving addUsersAndRoles");
  }

  /* Searches a user by an existing email and also search by a non-existing
   * email.
   */
  public final void testFindByEmail() {
    assertThat(userRepository.findUserByEmail("admin-2@none"), notNullValue());
    assertThat(userRepository.findUserByEmail("XXX"), nullValue());
  }

  /* Searches a user by an existing name and also search by a non-existing
   * name.
   */
  public final void testFindByName() {
    assertThat(userRepository.findUserByName("admin-2"), notNullValue());
    assertThat(userRepository.findUserByName("XXX"), nullValue());
  }

  /* Gets a user, modifies its name, saves it and gets it back.
  */
  public final void testModify() {
    User user = userRepository.findUserByName("admin-2");
    user.modify(user.getName(), "modified email");
    userRepository.save(user);
    user = userRepository.findUserByEmail("modified email");
    assertThat(user, notNullValue());
  }

  /* Gets a user, removes it and verifies that it no longer exists.
  */
  public final void testRemove() {
    User user = userRepository.findUserByName("admin-2");
    userRepository.remove(user);
    assertThat(userRepository.getUsers(new UserFilter()).size(), is(1));
  }

  /* Finds a user and checks the name and email
  */
  public final void testFindUser() {
    User user1 = (User) userRepository.findUserByName("admin-2");
    User user2 = (User) userRepository.findUser(user1.getId());
    assertThat(user2.getName(), is("admin-2"));
    assertThat(user2.getEmail(), is("admin-2@none"));
  }

  /** Test getUsers using sorting filter.
   */
  @Test
  public final void testGetUsers_Ordering() {
    UserFilter userFilter = new UserFilter();
    Sorting sorting = new Sorting();
    sorting.setAscendingOrder(true);
    sorting.setColumnName("name");
    userFilter.setSorting(sorting);
    List<User> userList = userRepository.getUsers(userFilter);

    // Verify ascending order.
    assertThat(userList, notNullValue());
    assertThat(userList.size(), is(3));
    assertThat(userList, IsOrdered.isInAscendingOrder());

    // Set the descending order.
    sorting.setAscendingOrder(false);
    userFilter.setSorting(sorting);
    userList = userRepository.getUsers(userFilter);

    // Verify descending order.
    assertThat(userList, notNullValue());
    assertThat(userList.size(), is(3));
    assertThat(userList, IsOrdered.isInDescendingOrder());
  }

  /** Test getUsers using paging filter.
   */
  @Test
  public final void testGetUsers_Paging() {
    UserFilter userFilter = new UserFilter();
    Paging paging = new Paging();
    paging.setPageNumber(1);
    paging.setPageSize(2);
    userFilter.setPaging(paging);
    List<User> userList = userRepository.getUsers(userFilter);

    // Verify the results.
    assertThat(userList, notNullValue());
    assertThat(userList.size(), is(1));

    // Setting another page number.
    paging.setPageNumber(0);
    userFilter.setPaging(paging);
    userList = userRepository.getUsers(userFilter);

    // Verify the results
    assertThat(userList, notNullValue());
    assertThat(userList.size(), is(2));
  }

  /** Test the contains value filter.
   */
  @Test
  public final void testGetUsers_ContainsValue() {
    UserFilter userFilter = new UserFilter();
    ContainsFilter containsFilter = new ContainsFilter();
    containsFilter.setColumnName("name");
    containsFilter.setValue("nic");
    userFilter.setContainsFilter(containsFilter);
    List<User> userList = userRepository.getUsers(userFilter);

    //Verify the results.
    for (Object object : userList) {
      User user = (User) object;
      assertThat(user.getName(), containsString("nic"));
    }
  }

  /** Test the contains value filter, when the search does not match any
   * result.
   */
  @Test
  public final void testGetUsers_ContainsValueNoRecords() {
    UserFilter userFilter = new UserFilter();
    ContainsFilter containsFilter = new ContainsFilter();
    containsFilter.setColumnName("name");
    containsFilter.setValue("nicXXXXXXXXXX");
    userFilter.setContainsFilter(containsFilter);
    List<User> userList = userRepository.getUsers(userFilter);

    assertThat(userList.size(), is(0));
  }

  /* Saves a Role
  */
  public final void testSaveRole() {
    Role role = new Role("MyRole");
    assertThat(role.getId(), is(0L));
    roleRepository.save(role);
    assertThat(role.getId(), is(not(0L)));
  }

  /* Removes a Role
  */
  @Test
  public final void testRemoveRole() {
    Role role = new Role("MyRole");
    roleRepository.remove(role);
  }

  /* Test the findRole method.
   */
  @Test
  public final void testfindRole() {
    Role role = roleRepository.findRoleByName("ADMINISTRATOR");
    assertThat(role.getName(), is("ADMINISTRATOR"));
    role = roleRepository.findRole(role.getId());
    assertThat(role.getName(), is("ADMINISTRATOR"));
  }

  private static class IsOrdered<T extends List<User>>
    extends TypeSafeMatcher<List<User>> {
    
    boolean ascending = false;

    public IsOrdered(final boolean isAscending) {
      ascending = isAscending;
    }

    @Override
    public boolean matchesSafely(List<User> list) {
      User previous = list.get(0);
      for (User current: list) {
        int result = previous.getName().compareToIgnoreCase(current.getName());
        if (ascending && result > 0) {
            return false;
        } else {
          if (!ascending && result < 0) {
            return false;
          }
        }
        previous = current;
      }
      return true;
    }

    public void describeTo(Description description) {
      description.appendText("not in descendent order");
    }

    @Factory
    public static <T extends List<User>> Matcher<List<User>>
        isInDescendingOrder() {
      return new IsOrdered<T>(false);
    }
    @Factory
    public static <T extends List<User>> Matcher<List<User>>
        isInAscendingOrder() {
      return new IsOrdered<T>(true);
    }
  }
}

