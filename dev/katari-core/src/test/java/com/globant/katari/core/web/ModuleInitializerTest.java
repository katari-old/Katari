/* vim: set ts=2 et sw=2 cindent fo=qroca: */
package com.globant.katari.core.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

public class ModuleInitializerTest extends TestCase {

  public void testReceiveCustomEvent() {
    ModuleContextRegistrar registrar;
    registrar = EasyMock.createMock(ModuleContextRegistrar.class);
    EasyMock.replay(registrar);

    ApplicationContext parent = EasyMock.createMock(ApplicationContext.class);
    ModuleInitializer initializer = new ModuleInitializer(registrar);
    initializer.setApplicationContext(parent);
    initializer.onApplicationEvent(new ApplicationEvent(new Object()){
      private static final long serialVersionUID = -4647062656144905267L;});
    EasyMock.verify(registrar);
  }

  public void testChildContextRefreshed() {
    ApplicationContext parent = EasyMock.createMock(ApplicationContext.class);
    EasyMock.replay(parent);

    ApplicationContext child = EasyMock.createMock(ApplicationContext.class);
    EasyMock.replay(child);

    ModuleContextRegistrar registrar;
    registrar = EasyMock.createMock(ModuleContextRegistrar.class);
    EasyMock.replay(registrar);

    ModuleInitializer initializer = new ModuleInitializer(registrar);
    initializer.setApplicationContext(parent);

    initializer.onApplicationEvent(new ContextRefreshedEvent(child));

    EasyMock.verify(registrar);
    EasyMock.verify(child);
    EasyMock.verify(parent);
  }

  public void doTestSessionFactory(LocalSessionFactoryBean factoryBean) {
    ModuleContext context = EasyMock.createMock(ModuleContext.class);
    EasyMock.replay(context);

    Module userModule = EasyMock.createMock(Module.class);
    userModule.init(context);
    EasyMock.replay(userModule);

    Module timeModule = EasyMock.createMock(Module.class);
    timeModule.init(context);
    EasyMock.replay(timeModule);

    String[] beanNames = {"user.module", "time.module"};
    ApplicationContext parent = EasyMock.createMock(ApplicationContext.class);
    EasyMock.expect(parent.getBean("user.module")).andReturn(userModule);
    EasyMock.expect(parent.getBean("time.module")).andReturn(timeModule);
    EasyMock.expect(parent.containsBean("katari.sessionFactory"))
      .andReturn(true);
    EasyMock.expect(parent.getBean("&katari.sessionFactory")).andReturn(
        factoryBean);
    EasyMock.replay(parent);

    ModuleContextRegistrar registrar;
    registrar = EasyMock.createMock(ModuleContextRegistrar.class);
    List<String> beans = Arrays.asList(beanNames);
    EasyMock.expect(registrar.getModuleBeanNames()).andReturn(
        new HashSet<String>(beans));
    EasyMock.expect(registrar.getNewModuleContext("user")).andReturn(context);
    EasyMock.expect(registrar.getNewModuleContext("time")).andReturn(context);
    EasyMock.replay(registrar);

    ModuleInitializer initializer = new ModuleInitializer(registrar);
    initializer.setApplicationContext(parent);

    initializer.onApplicationEvent(new ContextRefreshedEvent(parent));

    EasyMock.verify(registrar);
    EasyMock.verify(parent);
    EasyMock.verify(userModule);
    EasyMock.verify(timeModule);
  }
}

