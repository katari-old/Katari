/**
 * 
 */
package com.globant.katari.gadgetcontainer.view;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import com.globant.katari.gadgetcontainer.application.GadgetGroupCommand;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.google.gson.Gson;

/**
 * Test for the controller {@link GadgetGroupController}
 * 
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 * 
 */
public class GadgetGroupControllerTest {
  
  MockHttpServletRequest request;
  GadgetGroupController controller;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest("GET", "socialPage.do");
    controller = (GadgetGroupController) SpringTestUtils.getContext().getBean("/socialPage.do");
  }

  /**
   * This controller writes directly to the response and never use the spring
   * ModelAndView spec, so it's Ok and should always return null.
   */
  @Test
  public void testHandleMvReturnsNullOk() {
    request.addParameter("groupName", "thePage");
    try {
      ModelAndView mv = controller.handleRequest(request,
          new MockHttpServletResponse());
      assertNull(mv);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testHandle() {
    String pageName = "thePage";
    String userId = "idUser";
    
    GadgetInstance gi = new GadgetInstance(userId, "http://lala", "1");
    Set<GadgetInstance> ins = new HashSet<GadgetInstance>();
    ins.add(gi);
    GadgetGroup page = new GadgetGroup(userId, pageName, ins);
    
    GadgetGroupCommand command = createMock(GadgetGroupCommand.class);
    expect(command.execute()).andReturn(page);
    replay(command);
    
    Gson gson = new Gson();
    String shouldResponse = gson.toJson(page);
    
    try {
      HttpServletResponse response = createMock(HttpServletResponse.class);
      PrintWriter writer = createMock(PrintWriter.class);
      
      response.addHeader("Content-type", "application/json");
      expect(response.getWriter()).andReturn(writer).times(2);
      
      writer.write(shouldResponse);
      writer.close();
      
      replay(response);
      replay(writer);
      
      controller.handle(request, response, command, null);
      
      EasyMock.verify(response);
      EasyMock.verify(writer);
    
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
}
