/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.ping;

import static org.easymock.classextension.EasyMock.*;

import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import org.springframework.web.context.WebApplicationContext;

public class PingServletTest {

  HttpServletResponse response = null;

  @Test
  public void testService() throws Exception {

    PingServices pingServices = new PingServices();

    // Mocks the Web Application Context
    WebApplicationContext wac = createMock(WebApplicationContext.class);
    expect(wac.getBean("katari.pingServices")).andReturn(pingServices);
    expectLastCall().anyTimes();
    replay(wac);

    // Mocks the servlet context.
    ServletContext context = createMock(ServletContext.class);
    expect(context.getServletContextName()).andReturn("/module/user");
    expect(context.getAttribute(
          WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)
        ).andReturn(wac);
    expectLastCall().anyTimes();
    // Under some conditions, the init method asks context to log the call.
    context.log(isA(String.class));
    expectLastCall().anyTimes();
    replay(context);
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    replay(request);

    // Mocks the servlet config.
    ServletConfig config = createMock(ServletConfig.class);
    expect(config.getServletContext()).andReturn(context);
    expectLastCall().anyTimes();
    expect(config.getInitParameter("StaticContentPath")).andReturn(
        "com/globant/katari/core/web");
    expectLastCall().anyTimes();
    expect(config.getInitParameter("requestCacheContent")).andReturn("true");
    expectLastCall().anyTimes();
    expect(config.getServletName()).andReturn("StaticContentServlet");
    expectLastCall().anyTimes();
    replay(config);

    PrintWriter out = createMock(PrintWriter.class);
    out.close();
    out.println("Loading spring context: SUCCESS\n"
        + "Application started successfully");
    replay(out);
    HttpServletResponse response = createNiceMock(HttpServletResponse.class);
    response.setContentType("text/plain");
    expect(response.getWriter()).andReturn(out);
    replay(response);

    PingServlet servlet = new PingServlet();
    servlet.init(config);
    servlet.service(request, response);
    verify(out);
  }
}

