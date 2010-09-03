#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.user.application;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import ${package}.web.testsupport.SpringTestUtils;
import ${package}.web.testsupport.VerifyHelper;
import ${package}.web.user.domain.User;
import ${package}.web.user.domain.UserRepository;
import ${package}.web.user.domain.filter.ContainsFilter;
import ${package}.web.user.domain.filter.Paging;
import ${package}.web.user.domain.filter.Sorting;

/* This class represents a TestCase of the user filter command. In this class
 * we will test all the features of the user filter command.
 */
public class UserFilterCommandTest extends TestCase {

  /* The command to be tested.
   */
  private UserFilterCommand userFilterCommnad;

  /* The user repository.
   */
  private UserRepository userRepository;

  /* The verify helper.
   */
  private VerifyHelper verify = new VerifyHelper();

  /* This is a set up method of this TestCase.
   */
  protected final void setUp() {
    userRepository = (UserRepository) SpringTestUtils.getBeanFactory()
        .getBean("userRepository");
    addUsers();
    userFilterCommnad = new UserFilterCommand(userRepository);
  }

  /* Adds a pair of users to be used in the tests.
   */
  private void addUsers() {

    //  Removes the unneded users.
    for (User user : userRepository.getUsers()) {
      if (!user.getName().equals("admin")) {
        userRepository.remove(user);
      }
    }

    // Add users.
    User user = new User("nico", "mail@none");
    user.changePassword("pass");
    userRepository.save(user);

    user = new User("nicanor", "mail@none");
    user.changePassword("pass");
    userRepository.save(user);

    user = new User("juan", "mail@none");
    user.changePassword("pass");
    userRepository.save(user);

    user = new User("ramon", "mail@none");
    user.changePassword("pass");
    userRepository.save(user);
  }

  private List<String> getUserNameList(List<User> users) {
    List<String> usersName = new ArrayList<String>();
    for (User user : users) {
      usersName.add(user.getName());
    }
    return usersName;
  }

  /* Test Execute.
   */
  public final void testExecute() {
    List<User> users = userFilterCommnad.execute();
    assertNotNull(users);
    assertEquals(5, users.size());
  }

  /* Test Execute Sorting.
   */
  public final void testExecute_Sorting() {

    // Verify the ascending order.
    Sorting sorting = new Sorting();
    sorting.setAscendingOrder(true);
    sorting.setColumnName("name");
    userFilterCommnad.setSorting(sorting);
    List<User> users = userFilterCommnad.execute();
    assertNotNull(users);
    assertEquals(5, users.size());
    assertTrue(verify.ascendingOrder(getUserNameList(users)));

    // Verify the descending order.
    sorting.setAscendingOrder(false);
    userFilterCommnad.setSorting(sorting);
    users = userFilterCommnad.execute();
    assertNotNull(users);
    assertEquals(5, users.size());
    assertTrue(verify.descendingOrder(getUserNameList(users)));
  }

  /* Test Execute Contains.
   */
  public final void testExecute_Contains() {
    ContainsFilter containsFilter = new ContainsFilter();
    containsFilter.setColumnName("name");
    String value = "nic";
    containsFilter.setValue(value);
    userFilterCommnad.setContainsFilter(containsFilter);
    List<User> users = userFilterCommnad.execute();
    assertNotNull(users);
    assertEquals(2, users.size());
    assertTrue(verify.containsAll(getUserNameList(users), value));
  }

  /* Test Execute Paging.
   */
  public final void testExecute_Paging() {

    // First Page.
    Paging paging = new Paging();
    paging.setPageNumber(0);
    paging.setPageSize(4);
    userFilterCommnad.setPaging(paging);
    List<User> users = userFilterCommnad.execute();
    assertNotNull(users);
    assertEquals(4, users.size());

    // Second Page.
    paging.setPageNumber(1);
    paging.setPageSize(4);
    userFilterCommnad.setPaging(paging);
    users = userFilterCommnad.execute();
    assertNotNull(users);
    assertEquals(1, users.size());

    // Sets the default pagination.
    userFilterCommnad.setPaging(new Paging());
  }
}
