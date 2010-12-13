/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import org.springframework.context.ApplicationContext;

import com.globant.katari.shindig.domain.Application;

import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

import com.globant.katari.gadgetcontainer.domain.SampleUser;

public class RemoveApplicationFromGroupCommandTest {

  private String gadgetXmlUrl1= "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String gadgetXmlUrl2 = "file:///" + new File(
      "target/test-classes/SampleGadget2.xml").getAbsolutePath();

  private CoreUser user;

  private ApplicationContext appContext;

  private Session session;

  @Before
  public void setUp() throws Exception {

    appContext = SpringTestUtils.getContext();

    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
      .openSession();

    session.createQuery("delete from GadgetInstance").executeUpdate();
    session.createQuery("delete from GadgetGroup").executeUpdate();
    session.createQuery("delete from CoreUser").executeUpdate();
    session.createSQLQuery("delete from supported_views").executeUpdate();
    session.createQuery("delete from Application").executeUpdate();

    user = new SampleUser("me");
    session.saveOrUpdate(user);
    user = (CoreUser) session.createQuery("from CoreUser").uniqueResult();
  }

  @Test
  public void testExecute() throws Exception {

    GadgetGroupRepository repository = (GadgetGroupRepository)
      appContext.getBean("gadgetcontainer.gadgetGroupRepository");

    Application application1 = new Application(gadgetXmlUrl1);
    repository.getHibernateTemplate().saveOrUpdate(application1);
    Application application2 = new Application(gadgetXmlUrl2);
    repository.getHibernateTemplate().saveOrUpdate(application2);

    GadgetGroup group = new GadgetGroup(user, "sample", "default", 2);
    group.add(new GadgetInstance(application1, 0, 0));
    GadgetInstance instanceToRemove = new GadgetInstance(application2, 0, 0);
    group.add(instanceToRemove);
    repository.save(group);

    // Sets the currently logged on user
    SpringTestUtils.setLoggedInUser(user);

    RemoveApplicationFromGroupCommand command;
    command = (RemoveApplicationFromGroupCommand) appContext.getBean(
        "removeApplicationFromGroupCommand");

    command.setGroupName("sample");
    command.setGadgetId(instanceToRemove.getId());
    command.execute();

    // Now we verify. There should be one gadget only.
    group = repository.findGadgetGroup(user.getId(), "sample");
    assertThat(group.getGadgets().size(), is(1));
    for (GadgetInstance gadget: group.getGadgets()) {
      assertThat(gadget.getColumn(), is(0));
      if (gadget.getOrder() == 0) {
        assertThat(gadget.getTitle(), is("Test title"));
      }
    }
  }

  @After
  public void tearDown() {
    session.close();
  }
}

