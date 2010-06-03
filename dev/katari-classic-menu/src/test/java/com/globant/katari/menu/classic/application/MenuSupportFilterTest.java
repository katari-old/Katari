/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.menu.classic.application;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import com.globant.katari.core.security.MenuAccessFilterer;
import com.globant.katari.core.web.MenuBar;
import com.globant.katari.core.web.MenuNode;
import com.globant.katari.core.web.ModuleContextRegistrar;

public class MenuSupportFilterTest extends TestCase {

  private HttpServletRequest request;

  private HttpServletResponse response;

  private HttpSession session;

  private FilterChain chain;

  private ModuleContextRegistrar registrar;

  private MenuAccessFilterer filterer;

  @Override
  public void setUp() throws Exception {
    // Mocks the context
    ServletContext context = createMock(ServletContext.class);
    replay(context);

    // Mocks the Session
    session = createMock(HttpSession.class);
    session.setAttribute("::selected-module-entry", "/");

    // Mocks the servlet request.
    request = createNiceMock(HttpServletRequest.class);
    expect(request.getSession()).andReturn(session);
    expect(request.getParameter("selected-module-entry")).andReturn("/");
    request.setAttribute(eq("::menu-display-helper"),
        isA(MenuDisplayHelper.class));
    replay(request);

    // Mocks the servlet response.
    response = createNiceMock(HttpServletResponse.class);
    replay(response);

    // Mocks the filter chain.
    chain = createMock(FilterChain.class);
    chain.doFilter(request, response);
    expectLastCall().anyTimes();
    replay(chain);

    // Mocks the ModuleContextRegistrar.
    registrar = createNiceMock(ModuleContextRegistrar.class);
    // Mocks the Menu access filterer
    filterer = createNiceMock(MenuAccessFilterer.class);

  }

  /* Tests the menu support filter.
   */
  public final void testDoFilter() throws Exception {

    expect(session.getAttribute("::selected-module-entry")).andReturn("/");
    replay(session);

    MenuBar menuBar = new MenuBar();
    new MenuNode(menuBar, "Node", "Node", 1, "");
    expect(filterer.filterMenuNodes(menuBar.getChildNodes())).andReturn(
        menuBar.getChildNodes()).anyTimes();
    expect(registrar.getMenuBar()).andReturn(menuBar);
    expectLastCall(). anyTimes();
    replay(filterer);
    replay(registrar);

    // Execute the test.
    MenuSupportFilter filter = new MenuSupportFilter(registrar,filterer);
    filter.init(null);
    filter.doFilter(request, response, chain);
    filter.destroy();

    verify(request);
    verify(session);
  }

  /* Tests the menu support filter simulating the initial user request (no
   * module entry selected).
   */
  public final void testDoFilter_noModuleEntry() throws Exception {

    expect(session.getAttribute("::selected-module-entry")).andReturn(null);
    session.setAttribute("::selected-module-entry", "/root/Node");
    replay(session);

    MenuBar menuBar = new MenuBar();
    MenuNode node = new MenuNode(menuBar, "Node", "Node", 1, "");
    node.getName();
    expect(filterer.filterMenuNodes(menuBar.getChildNodes())).andReturn(
        menuBar.getChildNodes()).anyTimes();

    expect(registrar.getMenuBar()).andReturn(menuBar);
    expectLastCall(). anyTimes();
    replay(filterer);
    replay(registrar);
    // Execute the test.
    MenuSupportFilter filter = new MenuSupportFilter(registrar,filterer);
    filter.init(null);
    filter.doFilter(request, response, chain);
    filter.destroy();

    verify(request);
    verify(session);
  }

  /* Tests the menu support filter simulating the initial user request (no
   * module entry selected).
   */
  public final void testDoFilter_noMenuNodes() throws Exception {

    expect(session.getAttribute("::selected-module-entry")).andReturn(null);
    session.setAttribute("::selected-module-entry", "");
    replay(session);

    MenuBar menuBar = new MenuBar();
    expect(filterer.filterMenuNodes(menuBar.getChildNodes())).andReturn(
        new ArrayList<MenuNode>()).anyTimes();
    expect(registrar.getMenuBar()).andReturn(menuBar).anyTimes();
    replay(filterer);
    replay(registrar);
    // Execute the test.
    MenuSupportFilter filter = new MenuSupportFilter(registrar,filterer);
    filter.init(null);
    filter.doFilter(request, response, chain);
    filter.destroy();

    verify(request);
    verify(session);
  }

  
}

