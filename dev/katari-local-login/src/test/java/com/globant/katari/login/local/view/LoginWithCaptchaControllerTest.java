package com.globant.katari.login.local.view;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.web.servlet.ModelAndView;

public class LoginWithCaptchaControllerTest extends TestCase {

  public final void testHandleRequestInternal() throws Exception {

    HttpServletRequest request;
    request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("GET");
    expect(request.getCharacterEncoding()).andReturn("UTF-8").anyTimes();
    replay(request);

    HttpServletResponse response;
    response = createMock(HttpServletResponse.class);

    LoginWithCaptchaController controller = new LoginWithCaptchaController();
    ModelAndView mav = controller.handleRequestInternal(request, response);
    assertNotNull(mav);
    assertEquals("localLoginView", mav.getViewName());
    assertEquals(mav.getModel().get("showCaptcha"), true);
  }
}

