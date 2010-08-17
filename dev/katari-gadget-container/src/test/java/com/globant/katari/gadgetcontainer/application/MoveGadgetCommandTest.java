/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.gadgetcontainer.application.TokenService;

import com.globant.katari.shindig.domain.Application;

import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

public class MoveGadgetCommandTest {

  private String gadgetXmlUrl = "file://" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String groupName = "theGroup";

  private CoreUser userId;

  private ContextUserService userService;

  private GadgetGroup gadgetGroup;

  private GadgetGroupRepository repository;

  @Before
  public void setUp() throws Exception {

    gadgetGroup = createMock(GadgetGroup.class);

    repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(0, groupName)).andReturn(gadgetGroup);
    replay(repository);

    userId = createMock(CoreUser.class);

    userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUserId()).andReturn(0L);
    replay(userService);

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
    verify(repository);
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
}

