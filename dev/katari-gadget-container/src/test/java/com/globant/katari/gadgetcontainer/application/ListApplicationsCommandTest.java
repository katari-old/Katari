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
import com.globant.katari.gadgetcontainer.domain.CustomizableGadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;

import com.globant.katari.gadgetcontainer.application.ListApplicationsCommand;

public class ListApplicationsCommandTest {

  private String gadgetXmlUrl1 = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String gadgetXmlUrl2 = "file:///" + new File(
      "target/test-classes/SampleGadget2.xml").getAbsolutePath();

  private String gadgetXmlUrlProfile = "file:///" + new File(
      "target/test-classes/SampleProfileGadget.xml").getAbsolutePath();

  @Test
  public void testExecute() throws Exception {

    List<Application> applications = new LinkedList<Application>();

    // We add 3 applications: 2 supports the default view, the other does not.
    Application application1 = new Application(gadgetXmlUrl1);
    applications.add(application1);

    Application application2 = new Application(gadgetXmlUrl2);
    applications.add(application2);

    Application application3 = new Application(gadgetXmlUrlProfile);
    applications.add(application3);

    // Add 1 application to the gadget group that supports the default view.
    CoreUser user = new SampleUser("me");
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "gadget group", "default", 2);
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

    // This should return just one application. Of the original 3, one is
    // already included and the other does not support the gadget container
    // view.
    List<Application> result = command.execute();
    assertThat(result.size(), is(1));
    assertThat(result.get(0).getTitle(), is("Test title 2"));

    verify(applicationRepository);
    verify(groupRepository);
  }
}

