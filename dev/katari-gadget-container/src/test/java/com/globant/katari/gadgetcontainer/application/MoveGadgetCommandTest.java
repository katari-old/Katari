/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.File;
import java.io.StringWriter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import org.springframework.context.ApplicationContext;

import com.globant.katari.shindig.domain.Application;

import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

import com.globant.katari.gadgetcontainer.domain.SampleUser;

public class MoveGadgetCommandTest {

  private String gadgetXmlUrl = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String groupName = "theGroup";

  private CoreUser user;

  private ContextUserService userService;

  private GadgetGroup gadgetGroup;

  private GadgetGroupRepository repository;

  private ApplicationContext appContext;

  private Session session;

  @Before
  public void setUp() throws Exception {

    gadgetGroup = createMock(GadgetGroup.class);

    appContext = SpringTestUtils.getContext();

    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
      .openSession();

    session.createQuery("delete from GadgetInstance").executeUpdate();
    session.createQuery("delete from GadgetGroup").executeUpdate();
    session.createQuery("delete from CoreUser").executeUpdate();
    session.createQuery("delete from Application").executeUpdate();

    user = new SampleUser("me");
    session.saveOrUpdate(user);
    user = (CoreUser) session.createQuery("from CoreUser").uniqueResult();

    userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUserId()).andReturn(user.getId());
    replay(userService);

    repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(user.getId(), groupName))
      .andReturn(gadgetGroup);
    repository.save(gadgetGroup);
    replay(repository);
  }

  @Test
  public void testExecute_nullGroup() {
    MoveGadgetCommand command = new MoveGadgetCommand(
        createMock(GadgetGroupRepository.class),
        createMock(ContextUserService.class));
    try {
      command.execute();
      fail("should fail because we never set the groupName command property");
    } catch (Exception e) {
    }
  }

  @Test
  public void testExecute_notCustomizable() {

    expect(gadgetGroup.isCustomizable()).andReturn(false);
    replay(gadgetGroup);

    MoveGadgetCommand command;
    command = new MoveGadgetCommand(repository, userService);
    command.setGroupName(groupName);
    command.setGadgetInstanceId(0);
    command.setColumn(3);
    command.setOrder(4);

    try {
      command.execute();
      fail("should fail because the group is not customizable.");
    } catch (Exception e) {
    }

    verify(gadgetGroup);
    verify(userService);
  }

  @Test
  public void testExecute_customizable() {

    expect(gadgetGroup.isCustomizable()).andReturn(true);
    expect(gadgetGroup.getNumberOfColumns()).andReturn(4);
    gadgetGroup.move(0, 3, 4);
    replay(gadgetGroup);

    MoveGadgetCommand command;
    command = new MoveGadgetCommand(repository, userService);
    command.setGroupName(groupName);
    command.setGadgetInstanceId(0);
    command.setColumn(3);
    command.setOrder(4);

    command.execute();

    verify(gadgetGroup);
    verify(userService);
    verify(repository);
  }

  @Test
  public void testExecute_customizableCol0() {

    expect(gadgetGroup.isCustomizable()).andReturn(true);
    expect(gadgetGroup.getNumberOfColumns()).andReturn(4);
    gadgetGroup.move(0, 0, 4);
    replay(gadgetGroup);

    MoveGadgetCommand command;
    command = new MoveGadgetCommand(repository, userService);
    command.setGroupName(groupName);
    command.setGadgetInstanceId(0);
    command.setColumn(0);
    command.setOrder(4);

    command.execute();

    verify(gadgetGroup);
    verify(userService);
    verify(repository);
  }

  // An end-to-end test (bah, from the command) to move a gadget instance.
  @Test
  public void testExecute_endToEnd() throws Exception {

    GadgetGroupRepository repository = (GadgetGroupRepository)
      appContext.getBean("gadgetcontainer.gadgetGroupRepository");

    Application app = new Application(gadgetXmlUrl);
    // Test friendly hack: never use the repository like this.
    repository.getHibernateTemplate().saveOrUpdate(app);

    GadgetGroup group = new GadgetGroup(user, "sample", 2);
    group.add(new GadgetInstance(app, 0, 0));
    group.add(new GadgetInstance(app, 1, 0));
    group.add(new GadgetInstance(app, 1, 1));
    group.add(new GadgetInstance(app, 1, 3));
    repository.save(group);

    // Find the gadget in 1,3
    GadgetInstance gadgetToMove = null;
    for (GadgetInstance gadget: group.getGadgets()) {
      if (gadget.getColumn() == 1 && gadget.getOrder() == 3) {
        gadgetToMove = gadget;
      }
    }

    // Sets the currently logged on user
    SpringTestUtils.setLoggedInUser(user);

    MoveGadgetCommand command;
    command = (MoveGadgetCommand) appContext.getBean("moveGadgetCommand");

    command.setGroupName("sample");
    command.setGadgetInstanceId(gadgetToMove.getId());
    command.setColumn(0);
    command.setOrder(0);
    assertThat(command.execute().write(new StringWriter()).toString(),
        is("{}"));

    // Now we verify. There should be two gadgets per column.
    group = repository.findGadgetGroup(user.getId(), "sample");
    int col0 = 0;
    int col1 = 0;
    for (GadgetInstance gadget: group.getGadgets()) {
      if (gadget.getColumn() == 0) {
        ++ col0;
      } else if (gadget.getColumn() == 1) {
        ++ col1;
      }
    }
    assertThat(col0, is(2));
    assertThat(col1, is(2));
  }

  @After
  public void tearDown() {
    session.close();
  }
}

