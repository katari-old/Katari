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

import org.apache.camel.ProducerTemplate;
import org.apache.camel.CamelContext;
import com.globant.katari.hibernate.coreuser.DeleteMessage;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import org.springframework.context.ApplicationContext;

import com.globant.katari.shindig.domain.Application;

import com.globant.katari.gadgetcontainer.domain.CustomizableGadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

import com.globant.katari.gadgetcontainer.domain.SampleUser;

public class DeleteUserListenerTest {

  private String gadgetXmlUrl1= "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String gadgetXmlUrl2 = "file:///" + new File(
      "target/test-classes/SampleGadget2.xml").getAbsolutePath();

  private CoreUser user;

  private ApplicationContext appContext;

  private Session session;

  @Before
  public void setUp() throws Exception {

    SpringTestUtils.get().clearDatabase();
    SpringTestUtils.get().beginTransaction();

    appContext = SpringTestUtils.get().getBeanFactory();

    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
      .openSession();

    user = new SampleUser("me");
    session.saveOrUpdate(user);
    user = (CoreUser) session.createQuery("from CoreUser").uniqueResult();
  }

  @After public void after() {
    SpringTestUtils.get().endTransaction();
  }

  @Test
  public void testExecute() throws Exception {

    GadgetGroupRepository repository = (GadgetGroupRepository)
      appContext.getBean("gadgetcontainer.gadgetGroupRepository");

    Application application1 = new Application(gadgetXmlUrl1);
    repository.getSession().saveOrUpdate(application1);
    Application application2 = new Application(gadgetXmlUrl2);
    repository.getSession().saveOrUpdate(application2);

    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "sample", "default", 2);
    group.add(new GadgetInstance(application1, 0, 0));
    GadgetInstance instanceToRemove = new GadgetInstance(application2, 0, 0);
    group.add(instanceToRemove);
    repository.save(group);

    // Sets the currently logged on user
    SpringTestUtils.setLoggedInUser(user);

    // Send a delete event.
    CamelContext eventBus;
    eventBus = (CamelContext) appContext.getBean("katari.eventBus");
    ProducerTemplate producer = eventBus.createProducerTemplate();
    DeleteMessage message = new DeleteMessage(user.getId());
    producer.requestBody("direct:katari.user.deleteUser", message);

    group = repository.findCustomizableGadgetGroup(user.getId(), "sample");
    assertThat(group, is(nullValue()));
    /*
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
    */
  }

  @After
  public void tearDown() {
    session.close();
  }
}

