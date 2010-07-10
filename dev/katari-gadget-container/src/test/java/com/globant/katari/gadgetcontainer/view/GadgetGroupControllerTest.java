/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.view;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertNull;

import java.io.PrintWriter;

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
import com.globant.katari.hibernate.coreuser.domain.CoreUser;

import com.google.gson.Gson;

/**
 * Test for the controller {@link GadgetGroupController}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class GadgetGroupControllerTest {

  private MockHttpServletRequest request;
  private GadgetGroupController controller;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest("GET", "socialPage.do");
    controller = (GadgetGroupController) SpringTestUtils.getContext().getBean(
        "/socialPage.do");
  }

  /**
   * This controller writes directly to the response and never use the spring
   * ModelAndView spec, so it's Ok and should always return null.
   */
  @Test
  public void testHandleMvReturnsNullOk() throws Exception {
    request.addParameter("groupName", "thePage");
    ModelAndView mv;
    mv = controller.handleRequest(request, new MockHttpServletResponse());
    assertNull(mv);
  }

  @Test
  public void testHandle() throws Exception {
    String pageName = "thePage";
    CoreUser userId = createMock(CoreUser.class);

    GadgetGroup group = new GadgetGroup(userId, pageName);
    GadgetInstance gi = new GadgetInstance("http://lala", "1");
    group.addGadget(gi);

    GadgetGroupCommand command = createMock(GadgetGroupCommand.class);
    expect(command.execute()).andReturn(group);
    replay(command);

    Gson gson = new Gson();
    String shouldResponse = gson.toJson(group);

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
  }
}

