package com.globant.katari.core.spring.controller;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.application.Command;

/** Test for the generic command controller.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class SimpleFormCommandControllerTest {

  /** Test method for the onSubmit happy path. */
  @SuppressWarnings("unchecked")
  @Test
  public void testOnSubmitObject() throws Exception {

    BindException bindException = createMock(BindException.class);
    expect(bindException.getModel()).andReturn(new HashMap<String, String>());
    replay(bindException);

    SimpleFormCommandController controller = new SimpleFormCommandController() {
      @Override
      protected Object createCommandBean() {
        return new Command<String>() {
          public String execute() {
            return "testing";
          }
        };
      }
    };

    controller.setFormView("theView");
    controller.setSuccessView("theSuccessView");
    controller.formBackingObject(new MockHttpServletRequest());

    Command<String> cmd = (Command<String>) controller.createCommandBean();
    ModelAndView mav = controller.onSubmit(cmd, bindException);

    assertThat("testing", equalTo(mav.getModel().get("result")));
    assertThat(cmd, equalTo(mav.getModel().get("command")));

    verify(bindException);

  }

}
