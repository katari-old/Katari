package com.globant.katari.core.spring.controller;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Validatable;

/**
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class SimpleFormControllerTest {

  private SimpleFormController controller = new MockSimpleFormController();

  @Test public void testHandleGet() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setParameter("dependencyFromInitialRequest", "this");
    request.setMethod("GET");
    ModelAndView getMAV = controller.handleRequest(request, response);
    MockCommand cmd = (MockCommand) getMAV.getModel().get("command");

    assertThat(cmd.dependencyFromInitialRequest, is("this"));
    assertThat(getMAV.getViewName(), is("formView"));
  }

  @Test public void testHandlePost() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setParameter("name", "emiliano");
    request.setParameter("lastName", "arango");
    request.setMethod("POST");
    ModelAndView getMAV = controller.handleRequest(request, response);
    MockCommand cmd = (MockCommand) getMAV.getModel().get("command");

    assertThat(cmd.name, is("emiliano"));
    assertThat(cmd.lastName, is("arango"));
    assertThat(getMAV.getViewName(), is("successView"));
  }

  @Test public void testHandlePost_validation() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setMethod("POST");
    ModelAndView getMAV = controller.handleRequest(request, response);
    BindException errors = (BindException) getMAV.getModel().get("errors");

    assertThat(errors.getErrorCount(), is(2));

    assertThat(errors.getFieldError("lastName").getCode(),
        is("lastName.not.null"));
    assertThat(errors.getFieldError("name").getCode(),is("name.not.null"));

    assertThat(getMAV.getViewName(), is("formView"));
  }

  public static class MockCommand implements Command<Void>, Validatable {

    private String dependencyFromInitialRequest;
    private String name;
    private String lastName;

    public Void execute() {
      return null;
    }

    /** Retrieves the .
     * @return the name
     */
    public String getName() {
      return name;
    }

    public void setName(final String theName) {
      name = theName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(final String theLastName) {
      lastName = theLastName;
    }

    public void setDependencyFromInitialRequest(final String param) {
      dependencyFromInitialRequest = param;
    }

    /** {@inheritDoc}.
     */
    public void validate(final Errors errors) {
      ValidationUtils.rejectIfEmpty(errors, "name", "name.not.null");
      ValidationUtils.rejectIfEmpty(errors, "lastName", "lastName.not.null");
    }
  }

  public static class MockSimpleFormController extends SimpleFormController {

    public MockSimpleFormController() {
      setBindOnNewForm(true);
      setSuccessView("successView");
      setFormView("formView");
    }

    @Override
    protected Command<?> createCommandBean() {
      return new MockCommand();
    }
  }

}
