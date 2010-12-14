/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public class CoreUserRepositoryTest extends
    AbstractTransactionalDataSourceSpringContextTests {

  /** The core user repository.
   *
   * Injected by Spring.
   */
  private CoreUserRepository userRepository = null;

  /**
   * Defines constructor for disabling rollback only transactions.
   */
  public CoreUserRepositoryTest() {
    this.setDefaultRollback(false);
  }

  /**
   * Configures application context xml file.
   */
  @Override
  protected String[] getConfigLocations() {
    return new String[] {
      "classpath:com/globant/katari/hibernate/coreuser/applicationContext.xml"
    };
  }

  /** Creates a sample user named test.
   */
  @Override
  protected void onSetUp() throws Exception {
    userRepository.getHibernateTemplate().bulkUpdate("delete from CoreUser");
    CoreUser user = new SampleUser("test");
    userRepository.getHibernateTemplate().save(user);
  }

  public void testFindUserByName() throws Exception {
    CoreUser user = userRepository.findUserByName("test");
    assertNotNull(user);
    assertEquals(user.getName(), "test");
  }

  public void testFindUser() throws Exception {
    CoreUser user = userRepository.findUserByName("test");
    long id = user.getId();

    user = userRepository.findUser(id);
    assertEquals(user.getName(), "test");
  }

  public CoreUserRepository getUserRepository() {
    return userRepository;
  }

  public void setUserRepository(final CoreUserRepository repository) {
    userRepository = repository;
  }
}

