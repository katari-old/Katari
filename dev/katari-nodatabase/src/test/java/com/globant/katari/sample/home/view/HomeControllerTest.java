/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.home.view;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.web.servlet.ModelAndView;

/** Tests the home controller.
 *
 * This is the simplest test for a controller. It simply checks that it selects
 * the correct view.
 */
public class HomeControllerTest extends TestCase {

  /** Test that the requests returns the correct view (home).
   */
  public final void testhandleRequest() throws Exception {

    HttpServletRequest request;
    request = createMock(HttpServletRequest.class);
    expect(request.getMethod()).andReturn("GET");
    replay(request);

    HttpServletResponse response;
    response = createMock(HttpServletResponse.class);

    HomeController homeController = new HomeController();
    ModelAndView mav = homeController.handleRequest(request, response);
    assertNotNull(mav);
    assertEquals(mav.getViewName(), "home");
  }
}

