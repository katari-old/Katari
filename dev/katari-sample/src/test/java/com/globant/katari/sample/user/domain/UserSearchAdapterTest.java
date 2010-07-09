/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.user.domain;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import com.globant.katari.hibernate.coreuser.domain.Role;

import com.globant.katari.search.domain.SearchResultElement;
import com.globant.katari.search.domain.Action;

public class UserSearchAdapterTest {

  @Test
  public void testCanConvert() {
    UserSearchAdapter adapter = new UserSearchAdapter("/module/user");
    // This works due to the getAdaptedClass covariance, otherwise it fails
    // with a compilation error.
    assertThat(adapter.getAdaptedClass(), equalTo(User.class));
  }

  @Test
  public void testConvert() {
    User user = new User("name", "email");
    user.addRole(new Role("some role"));

    UserSearchAdapter adapter = new UserSearchAdapter("/module/user");

    SearchResultElement result = adapter.convert(user, 10);

    assertThat(result.getDescription(),
        is("User - name: name; email: email; roles: some role"));

    List<Action> actions = result.getActions();
    Action action = actions.get(0);
    assertThat(actions.size(), is(1));

    assertThat(action.getName(), is("Edit"));
    assertThat(action.getIcon(), is(nullValue()));
    assertThat(action.getUrl(), is("/module/user/userEdit.do?userId=0"));
  }
}

