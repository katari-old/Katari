/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.view;

import static org.easymock.classextension.EasyMock.*;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.Command;

import org.json.JSONObject;
import com.globant.katari.gadgetcontainer.application.JsonRepresentation;

/** Test for the controller {@link JsonCommandController}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class JsonCommandControllerTest {

  private MockHttpServletRequest request;
  private JsonCommandController controller;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest("GET", "socialPage.do");
  }

  @Test
  public void testHandle() throws Exception {

    JsonCommandController controller = new JsonCommandController() {
      protected Command<JsonRepresentation> createCommandBean() {
        return new Command<JsonRepresentation>() {
          public JsonRepresentation execute() {
            return new JsonRepresentation(new JSONObject());
          }
        };
      }
    };

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(os);

    HttpServletResponse response = createMock(HttpServletResponse.class);
    response.addHeader("Content-type", "application/json");
    expect(response.getWriter()).andReturn(writer);
    replay(response);

    ModelAndView mv;
    mv = controller.handleRequest(request, response);

    // piggybacked assertion :).
    assertThat(mv, nullValue());

    writer.flush();
    assertThat(os.toString(), is("{}"));
  }
}

