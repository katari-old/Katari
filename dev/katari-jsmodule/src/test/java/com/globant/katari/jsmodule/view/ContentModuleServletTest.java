/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.view;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

public class ContentModuleServletTest {

  /* Tests if service correctly dispatches the request.
   */
  @Test
  public final void testService() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockServletConfig config = new MockServletConfig();

    ContentModuleServlet servlet = new ContentModuleServlet();
    servlet.init(config);
    
    request.setServletPath("com/globant/katari/jsmodule/view/image/a.png");
    request.setMethod("GET");
    servlet.service(request, response);

    assertThat(response.getStatus(), is(200));
    assertThat(response.getContentType(), is("image/png"));
  }

  /* Tests if service errors if the path should not be exposed.
   */
  @Test
  public final void testService_pathNotServed() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockServletConfig config = new MockServletConfig();

    ContentModuleServlet servlet = new ContentModuleServlet();
    servlet.init(config);
    
    request.setServletPath("com/globant/katari/jsmodule/view/notserved/a.png");
    request.setMethod("GET");
    servlet.service(request, response);

    assertThat(response.getStatus(), is(404));
  }
}

