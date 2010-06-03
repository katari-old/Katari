/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import junit.framework.TestCase;

import javax.servlet.ServletContext;

import java.util.Enumeration;

import static org.easymock.EasyMock.*;

/* Tests the request dispatcher servlet.
 */
public class ModuleServletContextTest extends TestCase {

  public final void test() throws Exception {

    ModuleContainerServlet container = new ModuleContainerServlet();

    // Mocks the Enumeration to be returned by theDelegate
    Enumeration<?> theEnum = createMock(Enumeration.class);

    // Mocks the servlet context.
    ServletContext theDelegate = createMock(ServletContext.class);
    theDelegate.setAttribute("MyAttrbute", "MyValue");
    expectLastCall().anyTimes();
    theDelegate.removeAttribute("MyAttrbute");
    expectLastCall().anyTimes();
    expect(theDelegate.getAttribute("MyAttrbute")).andReturn("MyValue");
    expectLastCall().anyTimes();
    expect(theDelegate.getAttributeNames()).andReturn(theEnum);
    expectLastCall().anyTimes();
    expect(theDelegate.getContext("/theuri")).andReturn(theDelegate);
    expectLastCall().anyTimes();
    expect(theDelegate.getInitParameter("name")).andReturn("MyInit");
    expectLastCall().anyTimes();
    expect(theDelegate.getInitParameterNames()).andReturn(theEnum);
    expectLastCall().anyTimes();
    expect(theDelegate.getMimeType("MyFile")).andReturn("HTML/TEXT");
    expectLastCall().anyTimes();
    expect(theDelegate.getRealPath("MyPath")).andReturn("MyRealPath");
    expectLastCall().anyTimes();
    expect(theDelegate.getResource("MyPath")).andReturn(null);
    expectLastCall().anyTimes();
    expect(theDelegate.getServerInfo()).andReturn("MyServer");
    expectLastCall().anyTimes();
    expect(theDelegate.getServletContextName()).andReturn(
        "ModuleServletContext");
    expectLastCall().anyTimes();
    replay(theDelegate);

    String module = "theModule";

    ModuleServletContext moduleServlet;
    moduleServlet = new ModuleServletContext(container, theDelegate, module);

    moduleServlet.setAttribute("MyAttrbute", "MyValue");
    assertEquals(moduleServlet.getAttribute("MyAttrbute"), "MyValue");

    moduleServlet.removeAttribute("MyAttrbute");
    assertEquals(moduleServlet.getAttribute("MyAttrbute"), "MyValue");
    
    assertEquals(moduleServlet.getInitParameter("name"), "MyInit");
    assertEquals(moduleServlet.getMimeType("MyFile"), "HTML/TEXT");
    assertEquals(moduleServlet.getMajorVersion(), 2);
    assertEquals(moduleServlet.getMinorVersion(), 4);
    assertEquals(moduleServlet.getRealPath("MyPath"), "MyRealPath");
    assertEquals(moduleServlet.getServerInfo(), "MyServer");
    assertEquals(moduleServlet.getServletContextName(),
        "ModuleServletContext");
  }
}

