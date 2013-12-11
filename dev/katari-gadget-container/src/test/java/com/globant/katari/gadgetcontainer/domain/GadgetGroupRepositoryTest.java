/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContext;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.shindig.domain.Application;

/** Test for the repository {@link GadgetGroupRepository}
 */
public class GadgetGroupRepositoryTest {

  private static final String REPOSITORY
    = "gadgetcontainer.gadgetGroupRepository";
  private GadgetGroupRepository repository;
  private ApplicationContext appContext;
  private Session session;

  private CoreUser user;

  private String url = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  @Before
  public void setUp() throws Exception {

    SpringTestUtils.get().clearDatabase();
    SpringTestUtils.get().beginTransaction();

    appContext = SpringTestUtils.get().getBeanFactory();
    repository = (GadgetGroupRepository) appContext.getBean(REPOSITORY);
    user = new SampleUser("me");
    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
        .openSession();
    session.saveOrUpdate(user);
    user = (CoreUser) session.createQuery("from CoreUser").uniqueResult();
  }

  @After public void after() {
    SpringTestUtils.get().endTransaction();
  }


  @Test
  public void testFindGadgetGroup() {
    createGadgetGroups(url);

    GadgetGroup group = repository.findGadgetGroup(user.getId(), "for me");

    assertThat(group, notNullValue());
    assertThat(group.getName(), is("for me"));
    assertThat(group.getGadgets().isEmpty(), is(false));
    assertThat(group.getGadgets().iterator().next().getUrl(), is(url));
  }

  @Test
  public void testFindGadgetGroup_forEverybody() {
    createGadgetGroups(url);

    GadgetGroup group;
    group = repository.findGadgetGroup(Long.MAX_VALUE, "for everybody");

    assertThat(group, notNullValue());
    assertThat(group.getName(), is("for everybody"));
    assertThat(group.getGadgets().isEmpty(), is(false));
    assertThat(group.getGadgets().iterator().next().getUrl(), is(url));
  }

  @Test
  public void testFindGadgetGroup_nonExisting() {
    GadgetGroup group = repository.findGadgetGroup(user.getId(), "nonExist");
    assertThat(group, nullValue());
  }

  @Test
  public void testFindGadgetGroupTemplate() {
    createGadgetGroups(url);

    GadgetGroup group = repository.findGadgetGroupTemplate("for user");

    assertThat(group, notNullValue());
    assertThat(group.getGadgets().isEmpty(), is(false));
    assertThat(group.getName(), is("for user"));
    assertThat(group.getGadgets().iterator().next().getUrl(), is(url));
  }

  @Test
  public void testRemoveGroupsFromUser() {
    createGadgetGroups(url);
    repository.removeGroupsFromUser(user.getId());
    GadgetGroup group = repository.findGadgetGroup(user.getId(), "for me");
    assertThat(group, is(nullValue()));
  }

  @Test
  public void testRemoveGroupsFromUser_noGroups() {
    // This simply tests that removeGroupsFromUser does not throw an exception.
    repository.removeGroupsFromUser(user.getId());
  }

  /** Creates one gadget group for the user (named 'for me'), and another for
   * everybody (named 'for everybody') and a gadget group template (named 'for
   * user').
   */
  @SuppressWarnings("unchecked")
  private void createGadgetGroups(final String gadgetUrl) {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "for me", "default", 2);
    Application app = new Application(gadgetUrl);
    // Test friendly hack: never use the repository like this.
    repository.getSession().saveOrUpdate(app);
    group.add(new GadgetInstance(app, 1, 2));
    repository.save(group);

    SharedGadgetGroup shared;
    shared = new SharedGadgetGroup("for everybody", "default", 2);
    ((Set<GadgetInstance>) new DirectFieldAccessor(shared)
      .getPropertyValue("gadgets")).add(new GadgetInstance(app, 1, 2));
    repository.save(shared);

    GadgetGroupTemplate templ;
    templ = new GadgetGroupTemplate("for user", "default", 2);
    ((Set<GadgetInstance>) new DirectFieldAccessor(templ)
      .getPropertyValue("gadgets")).add(new GadgetInstance(app, 1, 2));
    repository.save(templ);
  }
}

