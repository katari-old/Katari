/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.hibernate.SpringTestUtils;

public class CoreUserRepositoryTest {

  private CoreUserRepository userRepository = null;

  /** Creates a sample user named test.
   */
  @Before
  public void setUp() throws Exception {

    SpringTestUtils.get().beginTransaction();

    userRepository = (CoreUserRepository) SpringTestUtils.get().getBean(
        "coreuser.userRepository");
    userRepository.getSession().createQuery(
        "delete from CoreUser").executeUpdate();
    CoreUser user = new SampleUser("test");
    userRepository.getSession().save(user);

    SpringTestUtils.get().endTransaction();

  }

  @Test
  public void testFindUserByName() throws Exception {
    SpringTestUtils.get().beginTransaction();
    CoreUser user = userRepository.findUserByName("test");
    assertThat(user.getName(), is("test"));

    SpringTestUtils.get().endTransaction();
  }

  @Test
  public void testFindUser() throws Exception {
    SpringTestUtils.get().beginTransaction();
    CoreUser user = userRepository.findUserByName("test");
    long id = user.getId();

    user = userRepository.findUser(id);
    assertThat(user.getName(), is("test"));

    SpringTestUtils.get().endTransaction();
  }

}

