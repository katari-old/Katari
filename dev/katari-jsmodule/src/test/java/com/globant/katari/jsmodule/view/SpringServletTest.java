/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.view;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import javax.servlet.ServletContext;

import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;

import com.globant.katari.core.spring.controller.JsonCommandController;

import com.globant.katari.jsmodule.application.ResolveDependenciesCommand;

/** Tests the spring-servlet.xml.
 *
 * The test performed is very naive. Just verifies that the application context
 * can be created and the /hello.do bean is of the expected type.
 */
public class SpringServletTest {

  private XmlWebApplicationContext parent = null;

  private XmlWebApplicationContext appContext = null;

  @Before
  public void createContexts() throws Exception {

    ServletContext sc;
    sc = new MockServletContext(".", new FileSystemResourceLoader());

    parent = new XmlWebApplicationContext();
    parent.setServletContext(sc);
    parent.setConfigLocations(new String[] {
      "classpath:applicationContext.xml" });
    parent.refresh();

    appContext = new XmlWebApplicationContext();
    appContext.setParent(parent);
    appContext.setServletContext(sc);
    appContext.setConfigLocations(new String[] {
      "classpath:com/globant/katari/jsmodule/view/spring-servlet.xml" });
    appContext.refresh();
  }

  @Test
  public void testController() {
    Object logout = appContext.getBean(
        "/com/globant/katari/jsmodule/action/resolveDependencies.do");
    assertThat(logout, instanceOf(JsonCommandController.class));
  }

  @Test
  public void testCommand() {
    Object logout = appContext.getBean("jsmodule.resolveDependenciesCommand");
    assertThat(logout, instanceOf(ResolveDependenciesCommand.class));
  }

  @After
  public void close() {
    appContext.close();
    parent.close();
  }
}

