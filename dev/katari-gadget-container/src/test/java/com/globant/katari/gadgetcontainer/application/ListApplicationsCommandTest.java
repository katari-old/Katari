/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Test;

import java.util.List;
import java.util.LinkedList;
import java.io.File;

import com.globant.katari.shindig.domain.Application;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.gadgetcontainer.domain.SampleUser;

import com.globant.katari.gadgetcontainer.domain.ApplicationRepository;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;

import com.globant.katari.gadgetcontainer.application.ListApplicationsCommand;

public class ListApplicationsCommandTest {

  private String gadgetXmlUrl1 = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String gadgetXmlUrl2 = "file:///" + new File(
      "target/test-classes/SampleGadget2.xml").getAbsolutePath();

  @Test
  public void testExecute() throws Exception {

    List<Application> applications = new LinkedList<Application>();
    Application application1 = new Application(gadgetXmlUrl1);
    Application application2 = new Application(gadgetXmlUrl2);
    applications.add(application1);
    applications.add(application2);

    CoreUser user = new SampleUser("me");
    GadgetGroup group = new GadgetGroup(user, "gadget group", "default", 2);
    group.add(new GadgetInstance(application1, 0, 0));

    ApplicationRepository applicationRepository;
    applicationRepository = createMock(ApplicationRepository.class);
    expect(applicationRepository.findAll()).andReturn(applications);
    replay(applicationRepository);

    GadgetGroupRepository groupRepository;
    groupRepository = createMock(GadgetGroupRepository.class);
    expect(groupRepository.findGadgetGroup(1, "gadget group")).andReturn(group);
    replay(groupRepository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUserId()).andReturn(1L);
    replay(userService);

    ListApplicationsCommand command;
    command = new ListApplicationsCommand(applicationRepository,
        groupRepository, userService);
    command.setGadgetGroupName("gadget group");

    List<Application> result = command.execute();
    assertThat(result.size(), is(1));
    assertThat(result.get(0).getTitle(), is("Test title 2"));

    verify(applicationRepository);
    verify(groupRepository);
  }
}

