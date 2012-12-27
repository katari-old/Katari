package com.globant.katari.core.spring.controller;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Initializable;
import com.globant.katari.core.application.Validatable;

/**
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class SimpleFormControllerTest {

  private SimpleFormController controller = new MockSimpleFormController();

  @Test public void handleGet() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setParameter("dependencyFromInitialRequest", "this");
    request.setMethod("GET");
    ModelAndView mav = controller.handleRequest(request, response);
    MockCommand cmd = (MockCommand) mav.getModel().get("command");

    assertThat(cmd.dependencyFromInitialRequest, is("this"));
    assertThat(mav.getViewName(), is("formView"));
  }

  @Test public void handlePost() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setParameter("name", "emiliano");
    request.setParameter("lastName", "arango");
    request.setMethod("POST");
    ModelAndView mav = controller.handleRequest(request, response);
    MockCommand cmd = (MockCommand) mav.getModel().get("command");

    assertThat(cmd.name, is("emiliano"));
    assertThat(cmd.lastName, is("arango"));
    assertThat(mav.getViewName(), is("successView"));
  }

  @Test public void handlePost_validation() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setMethod("POST");
    ModelAndView mav = controller.handleRequest(request, response);
    BindException errors = (BindException) mav.getModel().get("errors");

    assertThat(errors.getErrorCount(), is(2));

    assertThat(errors.getFieldError("lastName").getCode(),
        is("lastName.not.null"));
    assertThat(errors.getFieldError("name").getCode(),is("name.not.null"));

    assertThat(mav.getViewName(), is("formView"));
  }

  @Test public void handlePost_referenceData() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setParameter("R-person-persons-id", "2");
    request.setParameter("name", "emiliano");
    request.setParameter("lastName", "arango");
    request.setMethod("POST");
    ModelAndView mav = controller.handleRequest(request, response);
    MockCommand cmd = (MockCommand) mav.getModel().get("command");

    assertThat(cmd.name, is("emiliano"));
    assertThat(cmd.lastName, is("arango"));
    assertThat(cmd.person.name, is("Pepe"));

    assertThat(mav.getViewName(), is("successView"));
  }

  @Test public void handleGet_referenceData() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setMethod("GET");
    ModelAndView mav = controller.handleRequest(request, response);

    assertThat((MockReferenceData) mav.getModel().get("referenceData"),
        is(MockSimpleFormController.RD));

    assertThat(mav.getViewName(), is("formView"));
  }

  public static class MockCommand implements Command<Void>, Validatable {

    private String dependencyFromInitialRequest;
    private String name;
    private String lastName;
    private MockPerson person;

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

    public MockPerson getPerson() {
      return person;
    }

    public void setPerson(final MockPerson thePerson) {
      person = thePerson;
    }

    /** {@inheritDoc}.
     */
    public void validate(final Errors errors) {
      ValidationUtils.rejectIfEmpty(errors, "name", "name.not.null");
      ValidationUtils.rejectIfEmpty(errors, "lastName", "lastName.not.null");
    }
  }

  public static class MockSimpleFormController extends SimpleFormController {

    public static MockReferenceData RD = new MockReferenceData();

    public MockSimpleFormController() {
      setBindOnNewForm(true);
      setSuccessView("successView");
      setFormView("formView");
    }

    @Override
    protected Command<?> createCommandBean() {
      return new MockCommand();
    }

    /** {@inheritDoc}. */
    @Override
    public Initializable createReferenceDataBean(HttpServletRequest request) {
      return RD;
    }
  }

  private static class MockReferenceData implements Initializable {

    private List<MockPerson> persons =
        new LinkedList<SimpleFormControllerTest.MockPerson>();

    /** {@inheritDoc}. */
    public void init() {
      persons.add(new MockPerson(1, "Emi"));
      persons.add(new MockPerson(2, "Pepe"));
      persons.add(new MockPerson(3, "Pablo"));
      persons.add(new MockPerson(4, "Juan"));
    }

    /** Retrieves the .
     * @return the persons
     */
    public List<MockPerson> getPersons() {
      return persons;
    }
  }

  private static class MockPerson {
    private int id;
    private String name;

    public MockPerson(int theId, String theName) {
      id = theId;
      name = theName;
    }
    public int getId() {
      return id;
    }
    public String getName() {
      return name;
    }
  }

}
