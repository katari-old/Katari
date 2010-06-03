package com.globant.katari.login.local.view;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class LogoutAndRedirectControllerTest extends TestCase {

  /**
   * Test if the requests logout succeds to the propper url using the parameter
   * forwarding.
   */
  public final void testHandleRequestInternal_forward() throws Exception {

    HttpServletRequest request;
    request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("GET");
    expect(request.getCharacterEncoding()).andReturn("UTF-8").anyTimes();
    expect(request.getParameter("service")).andReturn("forward_path.do");
    replay(request);

    HttpServletResponse response;
    response = createMock(HttpServletResponse.class);

    LogoutAndRedirectController controller = new LogoutAndRedirectController();
    ModelAndView mav = controller.handleRequest(request, response);
    assertNotNull(mav);
    assertTrue(mav.getView() instanceof RedirectView);
    assertEquals(((RedirectView) mav.getView()).getUrl(), "forward_path.do");
  }

  /**
   * Test if the requests logout succeds to the propper url.
   */
  public final void testHandleRequestInternal_noForward() throws Exception {

    HttpServletRequest request;
    request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("GET");
    expect(request.getCharacterEncoding()).andReturn("UTF-8").anyTimes();
    expect(request.getParameter("service")).andReturn(null);
    expect(request.getAttribute("baseweb")).andReturn("base_web");
    replay(request);

    HttpServletResponse response;
    response = createMock(HttpServletResponse.class);

    LogoutAndRedirectController controller = new LogoutAndRedirectController();
    ModelAndView mav = controller.handleRequest(request, response);
    assertNotNull(mav);
    assertTrue(mav.getView() instanceof RedirectView);
    assertEquals(((RedirectView) mav.getView()).getUrl(), "base_web/");
  }
}

