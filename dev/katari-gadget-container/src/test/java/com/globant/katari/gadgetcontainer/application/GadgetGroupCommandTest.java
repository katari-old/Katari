/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;
import com.globant.katari.hibernate.coreuser.domain.CoreUser;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class GadgetGroupCommandTest {

  private CoreUser user;

  @Before
  public void setUp() throws Exception {
    user = createMock(CoreUser.class);
    expect(user.getId()).andReturn(1l);
    replay(user);
  }

  @Test
  public void testExecute_pageNull() {
    GadgetGroupCommand command = new GadgetGroupCommand(
        createMock(GadgetGroupRepository.class),
        createMock(ContextUserService.class));
    try {
      command.execute();
      fail("should fail because we never set the groupName command property");
    } catch (Exception e) {
    }
  }

  @Test
  public void testExecute() {

    String groupName = "1";
    long userId = 1;

    GadgetInstance gi = createMock(GadgetInstance.class);

    GadgetGroup gadgetGroup = new GadgetGroup(user, groupName, 1);
    gadgetGroup.addGadget(gi);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(userId, groupName))
      .andReturn(gadgetGroup);
    replay(repository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUserId()).andReturn(userId);
    replay(userService);

    GadgetGroupCommand command;
    command = new GadgetGroupCommand(repository, userService);
    command.setGroupName(groupName);

    command.execute();

    verify(userService);
    verify(repository);
  }
}

