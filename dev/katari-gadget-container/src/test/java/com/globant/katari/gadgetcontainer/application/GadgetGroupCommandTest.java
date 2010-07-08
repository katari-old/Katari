/**
 * 
 */
package com.globant.katari.gadgetcontainer.application;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import static org.easymock.classextension.EasyMock.*;
import org.junit.Test;

import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class GadgetGroupCommandTest {
  
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
    String userName = "1";
    
    
    GadgetGroup gadgetGroup = new GadgetGroup(userName, pageName);

    GadgetInstance gi = createMock(GadgetInstance.class);
    gadgetGroup.addGadget(gi);
    
    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    ContextUserService userService = createMock(ContextUserService.class);
    TokenService tokenService = createMock(TokenService.class);
    
    expect(userService.getCurrentUserId()).andReturn(userName);
    expect(repository.findPage(userName, pageName)).andReturn(gadgetGroup);
    expect(tokenService.createSecurityToken(userName, gi)).andReturn("token");
    
    gi.associateToViewer("token", userName);
    
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

