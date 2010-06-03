/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.web.context.WebApplicationContext;

public class SpringBootstrapServletTest extends TestCase {

  private boolean destroyedCalled = false;

  HttpServletRequest request = null;

  HttpServletResponse response = null;

  public void setUp() throws Exception {
    destroyedCalled = false;
    // Mocks the servlet request.
    request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn(
        "/module/user/static/test_image.gif");
    expectLastCall().anyTimes();
    expect(request.getRequestURL()).andReturn(new StringBuffer());
    expectLastCall().anyTimes();
    expect(request.getServletPath()).andReturn("/static/test_image.gif");
    expectLastCall().anyTimes();
    expect(request.getDateHeader("If-Modified-Since")).andReturn(0l);
    expect(request.getContextPath()).andReturn("/katari-web");
    expectLastCall().anyTimes();
    expect(request.getMethod()).andReturn("GET");
    expectLastCall().anyTimes();
    expect(request.getProtocol()).andReturn("http");
    expectLastCall().anyTimes();
    expect(request.getPathInfo()).andReturn("/user/welcome.do");
    expectLastCall().anyTimes();
    replay(request);

    // Mocks the Response's output stream
    ServletOutputStream mockOutputStream = createMock(ServletOutputStream.class);
    mockOutputStream.write(isA(byte[].class), eq(0), eq(776));
    mockOutputStream.flush();
    replay(mockOutputStream);

    // Mocks the servlet response.
    response = createMock(HttpServletResponse.class);
    expect(response.getOutputStream()).andReturn(mockOutputStream);
    expectLastCall().anyTimes();
    response.setContentType("image/gif");
    expectLastCall().anyTimes();
    response.setDateHeader(same("Date"), anyLong());
    expectLastCall().anyTimes();
    response.setDateHeader(same("Expires"), anyLong());
    expectLastCall().anyTimes();
    response.setDateHeader(same("Retry-After"), anyLong());
    expectLastCall().anyTimes();
    response.setHeader(same("Cache-Control"), (String) anyObject());
    expectLastCall().anyTimes();
    response.setDateHeader(same("Last-Modified"), anyLong());
    expectLastCall().anyTimes();
    replay(response);
  }

  @SuppressWarnings("serial")
  private final ServletConfig createConfig(final String beanName) {

    // A sample servlet that will be mapped to *.do.
    destroyedCalled = false;
    ServletAndParameters doServlet = new ServletAndParameters(
        new HttpServlet() {
          public void doGet(HttpServletRequest req, HttpServletResponse resp) {
          }

          public void destroy() {
            destroyedCalled = true;
          }
        });

    TreeMap<String, Map<String, ServletAndParameters>> modulesMap;
    modulesMap = new TreeMap<String, Map<String, ServletAndParameters>>();
    TreeMap<String, ServletAndParameters> usersMap;
    usersMap = new TreeMap<String, ServletAndParameters>();
    modulesMap.put("user", usersMap);
    usersMap.put(".*\\.do", doServlet);
    ModuleContainerServlet servlet = new ModuleContainerServlet();
    servlet.addModule("user", usersMap);

    // Mocks the Web Application Context
    WebApplicationContext wac = createMock(WebApplicationContext.class);
    if (beanName == null) {
      expect(wac.getBean("katari.moduleContainer")).andReturn(servlet);
    } else {
      expect(wac.getBean(beanName)).andReturn(servlet);
    }
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

    // Mocks the servlet config.
    ServletConfig config = createMock(ServletConfig.class);
    expect(config.getServletContext()).andReturn(context);
    expectLastCall().anyTimes();
    expect(config.getInitParameter("StaticContentPath")).andReturn(
        "com/globant/katari/core/web");
    expectLastCall().anyTimes();
    expect(config.getInitParameter("requestCacheContent")).andReturn("true");
    expectLastCall().anyTimes();
    expect(config.getInitParameter("servletBeanName")).andReturn(beanName);
    expectLastCall().anyTimes();
    expect(config.getServletName()).andReturn("StaticContentServlet");
    expectLastCall().anyTimes();
    replay(config);

    return config;
  }

  /*
   * Tests if service correctly dispatches the request.
   */
  public final void testServiceDefaulBeanName() throws Exception {

    ServletConfig config = createConfig(null);

    SpringBootstrapServlet servlet = new SpringBootstrapServlet();
    servlet.init(config);
    servlet.service(request, response);
    servlet.destroy();
    assertTrue(destroyedCalled);
  }

  /*
   * Tests if service correctly dispatches the request.
   */
  public final void testServiceSpecificBeanName() throws Exception {

    ServletConfig config = createConfig("SomeBeanName");

    SpringBootstrapServlet servlet = new SpringBootstrapServlet();
    servlet.init(config);
    servlet.service(request, response);
    servlet.destroy();
    assertTrue(destroyedCalled);
  }
}

