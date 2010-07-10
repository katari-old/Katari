/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.user.domain;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.katari.sample.testsupport.DataHelper;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.testsupport.VerifyHelper;
import com.globant.katari.sample.user.domain.filter.ContainsFilter;
import com.globant.katari.sample.user.domain.filter.Paging;
import com.globant.katari.sample.user.domain.filter.Sorting;

public class UserRepositoryTest extends TestCase {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(UserRepositoryTest.class);

  /** This is the implementation of the repository of the user.
   */
  private UserRepository userRepository;

  /** This is the implementation of the repository of the role.
   */
  private RoleRepository roleRepository;

  /** The verify helper.
   */
  private VerifyHelper verify;

  /** This is a set up method of this TestCase.
   */
  protected final void setUp() {
    userRepository = (UserRepository) SpringTestUtils.getBeanFactory().getBean(
        "userRepository");
    roleRepository = (RoleRepository) SpringTestUtils.getBeanFactory().getBean(
    "coreuser.roleRepository");
    verify = new VerifyHelper();
    addUsersAndRoles();
  }

  /** Adds a pair of users to be used in the tests.
   */
  private void addUsersAndRoles() {
    log.trace("Entering addUsersAndRoles");

    //  Removes the unneeded users.
    DataHelper.removeExtraUsers(userRepository);
    DataHelper.removeExtraRoles(roleRepository);

    Role sampleRole = new Role("test-1");
    roleRepository.save(sampleRole);

    // Add users.
    User user = new User("nico", "mail@none");
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

  /** Test getUsers using sorting filter.
   */
  public final void testGetUsers_Ordering() {
    UserFilter userFilter = new UserFilter();
    Sorting sorting = new Sorting();
    sorting.setAscendingOrder(true);
    sorting.setColumnName("name");
    userFilter.setSorting(sorting);
    List<User> userList = userRepository.getUsers(userFilter);

    // Verify ascending order.
    assertNotNull(userList);
    assertTrue(userList.size() > 1);
    assertTrue(verify.ascendingOrder(getUserNameList(userList)));

    // Set the descending order.
    sorting.setAscendingOrder(false);
    userFilter.setSorting(sorting);
    userList = userRepository.getUsers(userFilter);

    // Verify descending order.
    assertNotNull(userList);
    assertTrue(userList.size() > 1);
    assertTrue(verify.descendingOrder(getUserNameList(userList)));
  }

  /** Test getUsers using paging filter.
   */
  public final void testGetUsers_Paging() {
    UserFilter userFilter = new UserFilter();
    Paging paging = new Paging();
    paging.setPageNumber(1);
    paging.setPageSize(2);
    userFilter.setPaging(paging);
    List<User> userList = userRepository.getUsers(userFilter);

    // Verify the results.
    assertNotNull(userList);
    assertEquals(1, userList.size());

    // Setting another page number.
    paging.setPageNumber(0);
    userFilter.setPaging(paging);
    userList = userRepository.getUsers(userFilter);

    // Verify the results
    assertNotNull(userList);
    assertEquals(2, userList.size());
  }

  /** Test the contains value filter.
   */
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
      assertTrue(user.getName().contains("nic"));
    }
  }

  /** Test the contains value filter, when the search does not match any
   * result.
   */
  public final void testGetUsers_ContainsValueNoRecords() {
    UserFilter userFilter = new UserFilter();
    ContainsFilter containsFilter = new ContainsFilter();
    containsFilter.setColumnName("name");
    containsFilter.setValue("nicXXXXXXXXXX");
    userFilter.setContainsFilter(containsFilter);
    List<User> userList = userRepository.getUsers(userFilter);

    assertEquals(0, userList.size());
  }

  private List<String> getUserNameList(List<User> users) {
    List<String> usersName = new ArrayList<String>();
    for (User user : users) {
      usersName.add(user.getName());
    }
    return usersName;
  }
}

