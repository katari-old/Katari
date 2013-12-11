/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.application;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import com.globant.katari.search.SpringTestUtils;
import com.globant.katari.search.domain.mock.User;

import com.globant.katari.tools.SecurityTestUtils;

// import com.globant.katari.search.domain.IndexRepository;
import com.globant.katari.search.domain.SearchResultElement;

import com.globant.katari.search.domain.TestRepository;

public class SearchCommandTest {

  private SearchCommand command;

  private TestRepository testRepository;

  @Before
  public void setUp() {
    command = (SearchCommand)
      SpringTestUtils.getModuleBeanFactory().getBean("searchCommand");

    testRepository = (TestRepository) 
      SpringTestUtils.getBeanFactory().getBean("repository");

    testRepository.removeAll(User.class);

    /*
    IndexRepository repository = (IndexRepository)
      SpringTestUtils.getBeanFactory().getBean("search.indexRepository");
    repository.reIndex();
    */

    SecurityTestUtils.fakeUser("admin", "ROLE_ADMINISTRATOR");
  }

  @Test
  public void testFind() {
    User user1 = new User("name 1", "email 1");
    User user2 = new User("name 2", "email 2");

    testRepository.save(user1);
    testRepository.save(user2);

    command.setQuery("name 1");
    List<SearchResultElement> result = command.execute();
    assertThat(result.size(), is(1));
    SearchResultElement element = result.get(0);
    assertThat(element.getAlias(), is("User"));
    assertThat(element.getTitle(), is("name 1"));

    assertThat(command.getTotalPages(), is(1));
  }
}

