/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.socialserver.socialserver.view;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

public class HelloControllerTest {

  @Test
  public void testHandleRequestInternal() throws Exception {
    HelloController controller = new HelloController();
    ModelAndView mav = controller.handleRequestInternal(
        new MockHttpServletRequest(), new MockHttpServletResponse());
    assertThat(mav.getViewName(), is("hello"));
  }
}

