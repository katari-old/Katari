/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.List;

import com.globant.katari.tools.SecurityTestUtils;

import com.globant.katari.search.SpringTestUtils;
import com.globant.katari.search.domain.mock.User;
import com.globant.katari.search.domain.mock.Activity;
import com.globant.katari.hibernate.role.domain.Role;

public class IndexRepositoryTest {

  private IndexRepository repository;

  private TestRepository testRepository;

  @Before
  public void setUp() {
    repository = (IndexRepository)
      SpringTestUtils.getBeanFactory().getBean("search.indexRepository");
    testRepository = (TestRepository) 
      SpringTestUtils.getBeanFactory().getBean("repository");

    // Removes users, roles and activities
    testRepository.removeAll(User.class);
    testRepository.removeAll(Activity.class);
    testRepository.removeAll(Role.class);

    SecurityTestUtils.fakeUser("admin", "ROLE_ADMINISTRATOR");
  }

  /* There should be a way to test the reindex :).
  @Test
  public void testReindex() {
    repository.reIndex();
  }
  */

  @Test
  public void testFind_noResults() {
    SearchResult result = repository.find("name 1", 0);
    assertThat(result.getTotalPages(), is(0));
    assertThat(result.getElements().size(), is(0));
  }

  @Test
  public void testFind_emptyQuery() {
    SearchResult result = repository.find("", 0);
    assertThat(result.getTotalPages(), is(0));
    assertThat(result.getElements().size(), is(0));
  }

  @Test
  public void testFind_forAnonymousEmpty() {

    User user = new User("username", "email");
    Activity activity = new Activity("activityname");

    testRepository.save(user);
    testRepository.save(activity);

    SearchResult result;

    SecurityTestUtils.fakeUser("none", "IS_AUTHENTICATED_ANONYMOUSLY");

    result = repository.find("username", 0);
    assertThat(result.getTotalPages(), is(0));
  }

  @Test
  public void testFind_forAnonymousNotEmpty() {

    User user = new User("username", "email");
    Activity activity = new Activity("activityname");

    testRepository.save(user);
    testRepository.save(activity);

    SearchResult result;

    SecurityTestUtils.fakeUser("none", "IS_AUTHENTICATED_ANONYMOUSLY");

    result = repository.find("activityname", 0);
    assertThat(result.getTotalPages(), is(1));
  }

  @Test
  public void testFind_forAdministrator2results() {

    User user = new User("some name", "email");
    Activity activity = new Activity("some name");

    testRepository.save(user);
    testRepository.save(activity);

    SearchResult result;

    result = repository.find("some name", 0);
    assertThat(result.getElements().size(), is(2));
  }

  @Test
  public void testFind_forAnonymous1result() {

    User user = new User("some name", "email");
    Activity activity = new Activity("some name");

    testRepository.save(user);
    testRepository.save(activity);

    SearchResult result;

    SecurityTestUtils.fakeUser("none", "IS_AUTHENTICATED_ANONYMOUSLY");

    result = repository.find("some name", 0);
    assertThat(result.getTotalPages(), is(1));
  }

  @Test
  public void testFind_withRole() {
    User user = new User("name", "email");
    user.addRole(new Role("some role"));

    testRepository.save(user);

    SearchResult result = repository.find("name", 0);

    assertThat(result.getTotalPages(), is(1));
    assertThat(result.getElements().size(), is(1));
    assertThat(result.getElements().get(0).getDescription(),
        is("User - name: name; email: email; roles: some role"));
  }

  @Test
  public void testFind_byRole() {
    User user = new User("name", "email");
    user.addRole(new Role("some role"));
    testRepository.save(user);

    SearchResult result = repository.find("some role", 0);

    assertThat(result.getTotalPages(), is(1));
    assertThat(result.getElements().size(), is(1));
    assertThat(result.getElements().get(0).getDescription(),
        is("User - name: name; email: email; roles: some role"));
  }

  @Test
  public void testFind() {
    // Add some test objects.
    User user1 = new User("name 1", "email 1");
    User user2 = new User("name 2", "email 2");

    testRepository.save(user1);
    testRepository.save(user2);

    SearchResult result = repository.find("name 1", 0);
    assertThat(result.getTotalPages(), is(1));

    List<SearchResultElement> elements = result.getElements();
    assertThat(elements.size(), is(1));
    SearchResultElement element = elements.get(0);
    assertThat(element.getAlias(), is("User"));
    assertThat(element.getTitle(), is("name 1"));
    assertThat(element.getDescription(),
        is("User - name: name 1; email: email 1"));
    assertThat(element.getViewUrl(), containsString("user.do?id="));

    List<Action> actions = element.getActions();
    Action action = actions.get(0);
    assertThat(actions.size(), is(2));

    // We only check the first one ...
    assertThat(action.getName(), is("Edit"));
    assertThat(action.getIcon(), is(nullValue()));
    assertThat(action.getUrl(), containsString("userEdit.do?id="));
  }
}

