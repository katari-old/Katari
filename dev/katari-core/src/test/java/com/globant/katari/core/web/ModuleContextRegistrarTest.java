/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.classextension.EasyMock.createMock;

import org.springframework.beans.DirectFieldAccessor;

import java.util.List;
import java.util.LinkedList;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.core.login.LoginConfigurationSetter;

public class ModuleContextRegistrarTest {

  @Test
  public void testGetNewModuleContext_withMenu() {
    ModuleListenerProxy listener = new ModuleListenerProxy();
    ModuleFilterProxy filter = new ModuleFilterProxy();
    ModuleContainerServlet containerServlet = new ModuleContainerServlet();
    MenuBar menuBar = new MenuBar();
    LoginConfigurationSetter loginConfig;
    loginConfig = createMock(LoginConfigurationSetter.class);

    ModuleContextRegistrar registrar = new ModuleContextRegistrar(listener,
        filter, containerServlet, menuBar, loginConfig);

    ModuleContext context = registrar.getNewModuleContext("test");
    MenuBar contextMenuBar = (MenuBar) (new DirectFieldAccessor(context))
      .getPropertyValue("menuBar");
    assertThat(contextMenuBar, is(menuBar));
  }

  @Test
  public void testGetNewModuleContext_ignoreMenu() {
    ModuleListenerProxy listener = new ModuleListenerProxy();
    ModuleFilterProxy filter = new ModuleFilterProxy();
    ModuleContainerServlet containerServlet = new ModuleContainerServlet();
    MenuBar menuBar = new MenuBar();
    LoginConfigurationSetter loginConfig;
    loginConfig = createMock(LoginConfigurationSetter.class);

    ModuleContextRegistrar registrar = new ModuleContextRegistrar(listener,
        filter, containerServlet, menuBar, loginConfig);
    List<String> moduleNames = new LinkedList<String>();
    moduleNames.add("test");
    registrar.setModuleMenusToIgnore(moduleNames);

    ModuleContext context = registrar.getNewModuleContext("test");
    MenuBar contextMenuBar = (MenuBar) (new DirectFieldAccessor(context))
      .getPropertyValue("menuBar");
    assertThat(contextMenuBar, is(nullValue()));
  }

  @Test
  public void testGetNewModuleContext_ignoreAll() {
    ModuleListenerProxy listener = new ModuleListenerProxy();
    ModuleFilterProxy filter = new ModuleFilterProxy();
    ModuleContainerServlet containerServlet = new ModuleContainerServlet();
    MenuBar menuBar = new MenuBar();
    LoginConfigurationSetter loginConfig;
    loginConfig = createMock(LoginConfigurationSetter.class);

    ModuleContextRegistrar registrar = new ModuleContextRegistrar(listener,
        filter, containerServlet, menuBar, loginConfig);
    List<String> moduleNames = new LinkedList<String>();
    moduleNames.add(".*");
    registrar.setModuleMenusToIgnore(moduleNames);

    ModuleContext context = registrar.getNewModuleContext("test");
    MenuBar contextMenuBar = (MenuBar) (new DirectFieldAccessor(context))
      .getPropertyValue("menuBar");
    assertThat(contextMenuBar, is(nullValue()));
  }
}

