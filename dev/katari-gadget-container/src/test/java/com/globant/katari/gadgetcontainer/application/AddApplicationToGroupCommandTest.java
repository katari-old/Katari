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

import com.globant.katari.gadgetcontainer.domain.CustomizableGadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

import com.globant.katari.gadgetcontainer.domain.SampleUser;

public class AddApplicationToGroupCommandTest {

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

    SpringTestUtils.get().endTransaction();
  }

  // An end-to-end test (bah, from the command) to add a gadget instance.
  @Test
  public void testExecute_endToEnd() throws Exception {

    SpringTestUtils.get().beginTransaction();

    GadgetGroupRepository repository = (GadgetGroupRepository)
      appContext.getBean("gadgetcontainer.gadgetGroupRepository");

    Application application1 = new Application(gadgetXmlUrl1);
    repository.getSession().saveOrUpdate(application1);
    Application application2 = new Application(gadgetXmlUrl2);
    repository.getSession().saveOrUpdate(application2);

    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "sample", "default", 2);
    group.add(new GadgetInstance(application1, 0, 0));
    repository.save(group);

    SpringTestUtils.get().endTransaction();

    // Sets the currently logged on user
    SpringTestUtils.setLoggedInUser(user);

    AddApplicationToGroupCommand command;
    command = (AddApplicationToGroupCommand) appContext.getBean(
        "addApplicationToGroupCommand");

    command.setGroupName("sample");
    command.setApplicationId(application2.getId());
    command.execute();

    // Now we verify. There should be two gadgets in one column.
    SpringTestUtils.get().beginTransaction();
    group = repository.findCustomizableGadgetGroup(user.getId(), "sample");
    for (GadgetInstance gadget: group.getGadgets()) {
      assertThat(gadget.getColumn(), is(0));
      if (gadget.getOrder() == 0) {
        assertThat(gadget.getTitle(), is("Test title 2"));
      }
    }
    SpringTestUtils.get().endTransaction();
  }
}

