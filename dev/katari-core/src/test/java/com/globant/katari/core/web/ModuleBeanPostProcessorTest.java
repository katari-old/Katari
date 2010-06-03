/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.classextension.EasyMock.createMock;

import java.util.Set;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.globant.katari.core.login.LoginConfigurationSetter;

/* Tests the ModuleBeanPostProcessor.
 */
public class ModuleBeanPostProcessorTest extends TestCase {

  /* The postProcessor under test.
   */
  ModuleBeanPostProcessor postProcessor;

  private ModuleContextRegistrar registrar;

  public void setUp() {
    ModuleListenerProxy listeners = new ModuleListenerProxy();
    ModuleFilterProxy filters = new ModuleFilterProxy();
    ModuleContainerServlet container = new ModuleContainerServlet();
    MenuBar menuBar = new MenuBar();
    LoginConfigurationSetter conf = createMock(LoginConfigurationSetter.class);
    registrar = new ModuleContextRegistrar(listeners,
        filters, container, menuBar, conf);
    postProcessor = new ModuleBeanPostProcessor(registrar);
  }

  public void testPostProcessBeanFactory() {
    String[] modules = {"user.module", "time.module"};
    ConfigurableListableBeanFactory factory = EasyMock
        .createMock(ConfigurableListableBeanFactory.class);
    EasyMock.expect(factory.getBeanNamesForType(Module.class)).andReturn(
        modules);
    EasyMock.replay(factory);
    postProcessor.postProcessBeanFactory(factory);
    Set<String> registeredModules = registrar.getModuleBeanNames();
    assertEquals(2, registeredModules.size());
    assertTrue(registeredModules.contains("user.module"));
    assertTrue(registeredModules.contains("time.module"));
  }
}
