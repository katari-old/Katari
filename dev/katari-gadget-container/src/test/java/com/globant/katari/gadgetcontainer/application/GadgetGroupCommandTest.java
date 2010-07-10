/**
 *
 */
package com.globant.katari.gadgetcontainer.application;

import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
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

  /** Test method for
   *{@link com.globant.katari.gadgetcontainer.application.GadgetGroupCommand#execute()}.
   */
  @Test
  public void testExecute_pageNull() {
    GadgetGroupCommand command;
    command = new GadgetGroupCommand(createMock(GadgetGroupRepository.class),
        createMock(ContextUserService.class), createMock(TokenService.class));
    try {
      command.execute();
      fail("should fail because we never set the pageName command property");
    } catch (Exception e) {

    }
  }

  /** Test method for
   *{@link com.globant.katari.gadgetcontainer.application.GadgetGroupCommand#execute()}.
   */
  @Test
  public void testExecute() {

    String pageName = "1";
    long userId = 1;

    GadgetGroup gadgetGroup = new GadgetGroup(user, pageName);

    GadgetInstance gi = createMock(GadgetInstance.class);
    gadgetGroup.addGadget(gi);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    ContextUserService userService = createMock(ContextUserService.class);
    TokenService tokenService = createMock(TokenService.class);

    expect(userService.getCurrentUserId()).andReturn(userId);
    expect(repository.findGadgetGroup(userId, pageName)).andReturn(gadgetGroup);
    expect(tokenService.createSecurityToken(userId, gi)).andReturn("token");

    gi.associateToViewer("token", userId);

    replay(userService);
    replay(repository);
    replay(tokenService);

    GadgetGroupCommand command;
    command = new GadgetGroupCommand(repository, userService, tokenService);
    command.setGroupName(pageName);

    command.execute();

    verify(userService);
    verify(repository);
    verify(tokenService);
  }
}

