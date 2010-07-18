/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.view;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.easymock.classextension.EasyMock;
import org.hamcrest.CoreMatchers;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import com.globant.katari.gadgetcontainer.application.GadgetGroupCommand;
import com.globant.katari.gadgetcontainer.domain.Application;
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

  @Test
  public void testHandle_groupNotFound() throws Exception {

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(os);

    HttpServletResponse response = createMock(HttpServletResponse.class);
    response.addHeader("Content-type", "application/json");
    expect(response.getWriter()).andReturn(writer);
    replay(response);

    request.addParameter("groupName", "theGroup");
    ModelAndView mv;
    mv = controller.handleRequest(request, response);

    // piggybacked assertion :).
    assertThat(mv, nullValue());

    writer.flush();
    assertThat(os.toString(), is("{}"));
  }

  @Test
  public void testHandle() throws Exception {
    String groupName = "theGroup";
    CoreUser userId = createMock(CoreUser.class);

    GadgetGroup group = new GadgetGroup(userId, groupName, 1);
    Application app = new Application("http://lala");
    GadgetInstance gi = new GadgetInstance(app, "1#2");
    group.addGadget(gi);

    GadgetGroupCommand command = createMock(GadgetGroupCommand.class);
    expect(command.execute()).andReturn(group);
    replay(command);

    Gson gson = new Gson();
    String shouldResponse = gson.toJson(group);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(os);

    HttpServletResponse response = createMock(HttpServletResponse.class);
    response.addHeader("Content-type", "application/json");
    expect(response.getWriter()).andReturn(writer).times(1);
    replay(response);

    controller.handle(request, response, command, null);

    writer.flush();
    assertThat(os.toString(), is(baselineJson()));
    EasyMock.verify(response);
  }

  /** Creates the baseline json string, a string with a sample json object.
   *
   * @return the json string.
   *
   * @throws JSONException
   */
  private String baselineJson() throws JSONException {
    try {
      JSONObject groupJson = new JSONObject();
      groupJson.put("id", 0);
      groupJson.put("name", "theGroup");
      groupJson.put("ownerId", 0);
      groupJson.put("numberOfColumns", 1);
      groupJson.put("viewerId", 150);

      JSONObject gadgetJson = new JSONObject();
      gadgetJson.put("id", 0);
      gadgetJson.put("appId", 0);
      gadgetJson.put("position", "1#2");
      gadgetJson.put("url", "http://lala");
      groupJson.append("gadgets", gadgetJson);
      return groupJson.toString();
    } catch(JSONException e) {
      throw new RuntimeException("Error generating json", e);
    }
  }
}

