package com.globant.katari.core.spring.controller;

import static org.easymock.EasyMock.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Validatable;

import org.json.JSONException;
import org.json.JSONObject;

import com.globant.katari.core.application.JsonRepresentation;

/** Test for the controller {@link JsonCommandController}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class SimpleJsonCommandControllerTest {

  private MockHttpServletRequest request;
  private SimpleJsonCommandController controller;
  private ByteArrayOutputStream os = new ByteArrayOutputStream();
  private PrintWriter writer = new PrintWriter(os);
  private HttpServletResponse response = createMock(HttpServletResponse.class);

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest("GET", "getGadgetGroup.do");
    controller = new MockJsonController("name");
    response.addHeader("Content-type", "application/json; charset=UTF-8");
    expect(response.getWriter()).andReturn(writer);
    replay(response);
  }

  @Test
  public void testHandle() throws Exception {
    ModelAndView modelAndView = controller.handleRequest(request, response);
    // piggybacked assertion :).
    assertThat(modelAndView, nullValue());
    writer.flush();
    assertThat(os.toString(),
        is("[{\"defaultMessage\":\"default message\"," +
            "\"code\":\"the error code\"}]"));
  }

  @Test
  public void testHandle_validatable() throws Exception {
    ValidatableCommand command = new ValidatableCommand();
    BindException errors = createMock(BindException.class);

    expect(errors.hasErrors()).andReturn(true);
    List<ObjectError> errorList = new ArrayList<ObjectError>();

    FieldError oneError = createControl().createMock(FieldError.class);

    expect(oneError.getCode()).andReturn("the error code");
    expect(oneError.getDefaultMessage()).andReturn("default message");

    errorList.add(oneError);

    expect(errors.getAllErrors()).andReturn(errorList);

    replay(errors, oneError);

    controller.handleRequestInternal(request, response, command, errors);

    verify(errors, oneError);
  }

  @Test
  public void testHandle_ok() throws Exception {
    ValidatableCommand command = new ValidatableCommand();
    command.setTheField("just a value");
    BindException errors = createMock(BindException.class);
    expect(errors.hasErrors()).andReturn(false);
    replay(errors);
    controller.handleRequestInternal(request, response, command, errors);
    writer.flush();
    assertThat(os.toString(), is("{\"value\":\"just a value\"}"));
    verify(errors);
  }

  private static class MockJsonController extends SimpleJsonCommandController {

    public MockJsonController(String viewName) {
      setViewName(viewName);
    }

    @Override
    public Command<?> createCommandBean() {
      return new ValidatableCommand();
    }

  }

  private static class ValidatableCommand
    implements Command<JsonRepresentation>, Validatable {

    private String theField;

    public void validate(Errors errors) {
      ValidationUtils.rejectIfEmpty(errors, "theField", "the error code",
          "default message");
    }

    public JsonRepresentation execute() {
      JSONObject obj = new JSONObject();
      try {
        obj.put("value", theField);
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
      return new JsonRepresentation(obj);
    }

    @SuppressWarnings("unused")
    public String getTheField() {
      return theField;
    }

    public void setTheField(final String field) {
      theField = field;
    }
  }
}
