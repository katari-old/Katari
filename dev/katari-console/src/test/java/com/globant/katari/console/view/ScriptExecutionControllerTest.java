package com.globant.katari.console.view;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.OutputStream;
import java.io.PrintStream;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.junit.Test;

import com.globant.katari.console.application.ScriptingEngine;
import com.globant.katari.console.view.ScriptExecutionController;

public class ScriptExecutionControllerTest {

  @Test
  public void testHandleRequest() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST",
        "execute.do");
    MockHttpServletResponse response = new MockHttpServletResponse();

    ScriptingEngine scriptingEngine = new ScriptingEngine() {
      @Override
      public void execute(final String code, final OutputStream output,
          final OutputStream error) {
        new PrintStream(output, true).print("Groovy rocks your socks!\n");
      }
    };

    ScriptExecutionController scriptExecutionController =
        new ScriptExecutionController(scriptingEngine);

    String code = "println \"Groovy rocks your socks!\"";

    request.addParameter("script", code);

    scriptExecutionController.handleRequest(request, response);

    String contentType = response.getContentType();
    String content = response.getContentAsString();

    assertThat(contentType, equalTo("application/json"));
    assertThat(content, equalTo("{\"output\":\"Groovy rocks your socks!\\n\""
        + ",\"error\":\"\"}"));
  }
}
