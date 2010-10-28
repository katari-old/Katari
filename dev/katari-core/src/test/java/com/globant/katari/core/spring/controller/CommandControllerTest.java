/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring.controller;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.Before;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.Command;

/* This class is a test case of the CommandController class. In this class we
 * will test all the features of the CommandController class.
 */
public class CommandControllerTest {

  private CommandController commandController;

  @Before
  public final void setUp() {
    commandController = new CommandController("viewName") {
      protected Object createCommandBean() {
        return new Command<String>() {
          public String execute() {
            return "the result";
          }
        };
      }
    };
  }

  @SuppressWarnings("unchecked")
  @Test
  public final void testHandle() throws Exception {
    MockHttpServletRequest request 
        = new MockHttpServletRequest("GET", "/command.do");
    request.addParameter("globerId", "1");

    MockHttpServletResponse response = new MockHttpServletResponse();

    ModelAndView mav = commandController.handleRequest(request, response);
    assertThat(mav, notNullValue());
    assertThat(mav.getViewName(), is("viewName"));
    assertThat((String) mav.getModel().get("result"), is("the result"));
    Command<String> command = (Command<String>) mav.getModel().get("command");
    assertThat((String) command.execute(), is("the result"));
  }
}

