/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.shindig.domain.Application;

/** Test for the repository {@link GadgetGroupRepository}
 *
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 */
public class GadgetGroupRepositoryTest {

  private static final String REPOSITORY
    = "gadgetcontainer.gadgetGroupRepository";
  private GadgetGroupRepository repository;
  private ApplicationContext appContext;
  private Session session;

  private CoreUser user;

  private String url = "file://" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  @Before
  public void setUp() throws Exception {
    appContext = SpringTestUtils.getContext();
    repository = (GadgetGroupRepository) appContext.getBean(REPOSITORY);
    user = new SampleUser("me");
    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
        .openSession();
    session.createQuery("delete from GadgetInstance").executeUpdate();
    session.createQuery("delete from GadgetGroup").executeUpdate();
    session.createQuery("delete from CoreUser").executeUpdate();
    session.saveOrUpdate(user);
    user = (CoreUser) session.createQuery("from CoreUser").uniqueResult();
  }

  @After
  public void tearDown() {
    session.close();
  }

  @Test
  public void testFindGadgetGroup() {
    createGadgetGroups(url);

    GadgetGroup group = repository.findGadgetGroup(user.getId(), "for user");

    assertNotNull(group);
    assertFalse(group.getGadgets().isEmpty());
    assertTrue("for user".equals(group.getName()));
    assertTrue(group.getGadgets().iterator().next().getUrl().equals(url));
  }

  @Test
  public void testFindGadgetGroup_forEverybody() {
    createGadgetGroups(url);

    GadgetGroup group;
    group = repository.findGadgetGroup(Long.MAX_VALUE, "for everybody");

    assertNotNull(group);
    assertFalse(group.getGadgets().isEmpty());
    assertTrue("for everybody".equals(group.getName()));
    assertTrue(group.getGadgets().iterator().next().getUrl().equals(url));
  }

  @Test
  public void testFindGadgetGroup_nonExisting() {
    GadgetGroup group = repository.findGadgetGroup(user.getId(), "nonExist");
    assertNull(group);
  }

  /** Creates one gadget group for the user (named 'for user'), and another for
   * everybody (named 'for everybody').
   */
  private void createGadgetGroups(final String gadgetUrl) {
    GadgetGroup group = new GadgetGroup(user, "for user", 2);
    Application app = new Application(gadgetUrl);
    // Test friendly hack: never use the repository like this.
    repository.getHibernateTemplate().saveOrUpdate(app);
    group.add(new GadgetInstance(app, 1, 2));
    repository.save(group);

    group = new GadgetGroup(null, "for everybody", 2);
    group.add(new GadgetInstance(app, 1, 2));
    repository.save(group);
  }
}

