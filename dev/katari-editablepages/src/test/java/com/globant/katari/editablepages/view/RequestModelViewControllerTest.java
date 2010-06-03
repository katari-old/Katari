/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

public class RequestModelViewControllerTest {

  @Test
  public final void testhandleRequestInternal() throws Exception {
    RequestModelViewController controller;
    controller = new RequestModelViewController();

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("baseweb", "/some/path");
    MockHttpServletResponse response = new MockHttpServletResponse();

    ModelAndView mav = controller.handleRequestInternal(request, response);
    assertNotNull(mav);
    assertNotNull(mav.getModel());
    assertNotNull(mav.getModel().get("request"));
    assertEquals("/some/path", mav.getModel().get("baseweb"));
  }
}

