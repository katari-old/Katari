/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring.controller;

import static org.easymock.EasyMock.*;

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

import com.globant.katari.core.application.JsonRepresentation;

/** Test for the controller {@link JsonCommandController}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class JsonCommandControllerTest {

  private MockHttpServletRequest request;
  private JsonCommandController controller;

  private JsonRepresentation commandResult;

  private ByteArrayOutputStream os = new ByteArrayOutputStream();
  private PrintWriter writer = new PrintWriter(os);

  private HttpServletResponse response = createMock(HttpServletResponse.class);

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest("GET", "getGadgetGroup.do");

    controller = new JsonCommandController() {
      protected Command<JsonRepresentation> createCommandBean() {
        return new Command<JsonRepresentation>() {
          public JsonRepresentation execute() {
            return commandResult;
          }
        };
      }
    };

    response.addHeader("Content-type", "application/json; charset=UTF-8");
    expect(response.getWriter()).andReturn(writer);
    replay(response);

  }

  @Test
  public void testHandle() throws Exception {

    // The command will return an empty json object.
    commandResult = new JsonRepresentation(new JSONObject());

    ModelAndView modelAndView = controller.handleRequest(request, response);

    // piggybacked assertion :).
    assertThat(modelAndView, nullValue());

    writer.flush();
    assertThat(os.toString(), is("{}"));
  }

  @Test(expected = RuntimeException.class)
  public void testHandle_nullCommandResult() throws Exception {
    // The command will return null.
    commandResult = null;
    controller.handleRequest(request, response);
  }
}

