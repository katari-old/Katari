package com.globant.katari.gadgetcontainer.domain;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import com.globant.katari.hibernate.coreuser.domain.CoreUser;

/**
 * Test for the repository {@link GadgetGroupRepository}
 *
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class GadgetGroupRepositoryTest {

  private static final String REPOSITORY = "social.gadgetGroupRepository";
  private GadgetGroupRepository repository;
  private ApplicationContext appContext;
  private Session session;

  private CoreUser user;

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

  /** This test, persist a new group, and the search it back in the db.
   *  check that the group has the same attributes.
   */
  @Test
  public void testFindPage() {
    String groupName = randomUUID().toString();
    String url = "http://" + randomUUID().toString();
    createGadgetGroup(user, groupName, url, "1#3");

    GadgetGroup thePage = repository.findGadgetGroup(1, groupName);

    assertNotNull(thePage);
    assertFalse(thePage.getGadgets().isEmpty());
    assertTrue(groupName.equals(thePage.getName()));
    assertTrue(thePage.getGadgets().iterator().next().getUrl().equals(url));
  }

  /** This test, persist a new group, and the search it back in the db.
   *  check that the group has the same attributes.
   */
  @Test
  public void testFindPageNonExist() {
    GadgetGroup thePage = repository.findGadgetGroup(-1, "nonExist");
    assertNull(thePage);
  }

  /** This test creates a new group, then change the attribute of one of his
   *  gadgets instances, then check the changes performed over the
   *  gadget instance.
   */
  @Test
  public void testUpdateGadgetInstance() {
    String groupName = randomUUID().toString();
    String url = "http://" + randomUUID().toString();

    createGadgetGroup(user, groupName, url, "1#2");

    String gadgetNewPosition = "3#3";

    GadgetGroup group = repository.findGadgetGroup(user.getId(), groupName);

    GadgetInstance i = group.getGadgets().iterator().next();
    i.move(gadgetNewPosition);

    repository.save(group);

    group = repository.findGadgetGroup(user.getId(), groupName);

    assertTrue(group.getGadgets().iterator().next().getGadgetPosition().equals(
        gadgetNewPosition));
  }

  /** Creates and persists a new group in the database.
   *
   * @param userId
   * @param groupName
   * @param gadgetUrl
   * @param gadgetPosition
   */
  private void createGadgetGroup(final CoreUser userId, final String groupName,
      final String gadgetUrl, final String gadgetPosition) {
    GadgetGroup group = new GadgetGroup(userId, groupName);
    group.addGadget(new GadgetInstance(gadgetUrl,  gadgetPosition));
    repository.save(group);
  }
}

