/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.TreeMap;

import junit.framework.TestCase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import static org.easymock.EasyMock.*;

/* Tests the request dispatcher servlet. We use jmock here. With jdk-1.5 we
 * should have gone with easy mock instead.
 */
public class ModuleContainerServletTest extends TestCase {

  /* The sample servlets set this variable when the dispatcher calls service on
   * them. This is used to check that the correct servlet was invoked.
   */
  private String servletCalled;

  private String pathInfo;

  private TreeMap<String, ServletAndParameters> usersMap;

  private ServletConfig config;

  @SuppressWarnings("serial")
  protected void setUp() {

    org.apache.log4j.PropertyConfigurator.configure(
        "src/test/resources/log4j.properties");

    servletCalled = "";
    pathInfo = null;

    // Mocks the servlet context.
    ServletContext context = createMock(ServletContext.class);
    expect(context.getServletContextName()).andReturn("/module");
    expectLastCall().anyTimes();

    // Under some conditions, the init method asks context to log the call.
    context.log(isA(String.class));
    expectLastCall().anyTimes();
    replay(context);

    // Mocks the servlet config.
    config = createMock(ServletConfig.class);
    expect(config.getServletContext()).andReturn(context);
    expectLastCall().anyTimes();
    // Under some conditions, the init method asks the servlet for its name.
    expect(config.getServletName()).andReturn("ContainerServlet");
    expectLastCall().anyTimes();
    replay(config);

    // A sample servlet that will be mapped to *.do.
    ServletAndParameters doServlet = new ServletAndParameters(
        new HttpServlet() {
      public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletCalled = ".do";
        pathInfo = req.getPathInfo();
      }
    });

    // A sample servlet that will be mapped to *.test.
    ServletAndParameters testServlet = new ServletAndParameters(
        new HttpServlet() {
      public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletCalled = "test";
        pathInfo = req.getPathInfo();
      }
    });

    // A sample configuration mapping.
    usersMap = new TreeMap<String, ServletAndParameters>();
    usersMap.put(".*\\.do", doServlet);
    usersMap.put(".*/test", testServlet);
  }

  /* Tests if service correctly dispatches a request to *.do.
  */
  public final void testService_dotdo() throws Exception {

    // Mocks the servlet request.
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn(
        "/katari-i/module/user/welcome.do");
    expectLastCall().anyTimes();
    expect(request.getRequestURL()).andReturn(new StringBuffer());
    expectLastCall().anyTimes();
    expect(request.getServletPath()).andReturn("/module");
    expectLastCall().anyTimes();
    expect(request.getContextPath()).andReturn("/katari-i");
    expectLastCall().anyTimes();
    expect(request.getPathInfo()).andReturn("/user/welcome.do");
    expectLastCall().anyTimes();
    expect(request.getMethod()).andReturn("GET");
    expectLastCall().anyTimes();
    expect(request.getProtocol()).andReturn("http");
    expectLastCall().anyTimes();
    replay(request);

    ModuleContainerServlet servlet = new ModuleContainerServlet();
    servlet.addModule("user", usersMap);

    servlet.init(config);
    servlet.service(request, null);

    assertEquals(".do", servletCalled);
    assertNull(pathInfo);
  }

  /* Tests if service correctly dispatches a request to 'test' and the pathinfo
   * is correct.
   */
  public final void testService_testPathInfo() throws Exception {

    // Mocks the servlet request.
    HttpServletRequest request = createNiceMock(HttpServletRequest.class);
    expect(request.getRequestURI()).andReturn(
        "/katari-i/module/user/welcome.do");
    expectLastCall().anyTimes();
    expect(request.getRequestURL()).andReturn(new StringBuffer());
    expectLastCall().anyTimes();
    expect(request.getServletPath()).andReturn("/module");
    expectLastCall().anyTimes();
    expect(request.getContextPath()).andReturn("/katari-i");
    expectLastCall().anyTimes();
    expect(request.getPathInfo()).andReturn("/user/test/user/21?action=remove");
    expectLastCall().anyTimes();
    expect(request.getMethod()).andReturn("GET");
    expectLastCall().anyTimes();
    expect(request.getProtocol()).andReturn("http");
    expectLastCall().anyTimes();
    replay(request);

    ModuleContainerServlet servlet = new ModuleContainerServlet();
    servlet.addModule("user", usersMap);

    servlet.init(config);
    servlet.service(request, null);

    assertEquals("test", servletCalled);
    assertEquals("/user/21?action=remove", pathInfo);
  }
}

