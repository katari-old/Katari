/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.menu.classic.application;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.core.security.MenuAccessFilterer;
import com.globant.katari.core.web.MenuBar;
import com.globant.katari.core.web.MenuNode;
import com.globant.katari.core.web.ModuleContextRegistrar;

public class MenuSupportFilterTest {

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  private FilterChain chain;

  private ModuleContextRegistrar registrar;

  private MenuAccessFilterer filterer;

  @Before
  public void setUp() throws Exception {

    request = new MockHttpServletRequest(null, null, null);
    response = new MockHttpServletResponse();
    chain = new MockFilterChain();

    // Mocks the ModuleContextRegistrar.
    registrar = createNiceMock(ModuleContextRegistrar.class);
    // Mocks the Menu access filterer
    filterer = createNiceMock(MenuAccessFilterer.class);
  }

  /* Tests the menu support filter.
   */
  @Test
  public final void testDoFilter() throws Exception {

    request.setCookies(new Cookie("selected-module-entry", "/"));
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

    assertThat(request.getAttribute("::menu-display-helper"),
        is(instanceOf(MenuDisplayHelper.class)));
    // We do send the cookie again.
    Cookie cookie = response.getCookie("selected-module-entry");
    assertThat(cookie, is(nullValue()));
  }

  /* Tests the menu support filter simulating the initial user request (no
   * module entry selected).
   */
  @Test
  public final void testDoFilter_noModuleEntry() throws Exception {
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

    Cookie cookie = response.getCookie("selected-module-entry");
    assertThat(cookie, is(nullValue()));
  }

  /* Tests the menu support filter simulating the initial user request (no
   * module entry selected).
   */
  @Test
  public final void testDoFilter_noMenuNodes() throws Exception {
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

    Cookie cookie = response.getCookie("selected-module-entry");
    assertThat(cookie, is(nullValue()));
  }
}

