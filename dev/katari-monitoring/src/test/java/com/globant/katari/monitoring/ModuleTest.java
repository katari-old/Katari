/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.monitoring;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;

import org.springframework.mock.web.MockServletContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import com.globant.katari.core.web.ConfigurableModule;

/** Tests the module.xml.
 */
public class ModuleTest {

  private XmlWebApplicationContext appContext = null;

  @Before
  public void createContexts() throws Exception {

    /*
    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<!DOCTYPE beans PUBLIC '-//SPRING//DTD BEAN//EN'"
      + " 'http://www.springframework.org/dtd/spring-beans.dtd'>\n"
      + "<beans>\n"
      + "</beans>\n";

    parent = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    parent.refresh();
    */

    ServletContext sc;
    sc = new MockServletContext(".", new FileSystemResourceLoader());
    appContext = new XmlWebApplicationContext();
    // appContext.setParent(parent);
    appContext.setServletContext(sc);
    appContext.setConfigLocations(new String[] {
      "classpath:/applicationContext.xml" });
    appContext.refresh();
  }

  @Test
  public void testModuleType() {
    Object module = appContext.getBean("monitoring.module");
    assertTrue(module.getClass().equals(ConfigurableModule.class));
  }

  @After
  public void close() {
    appContext.close();
    // parent.close();
  }
}

