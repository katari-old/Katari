/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.socialserver.socialserver.view;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;

import org.springframework.mock.web.MockServletContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/** Tests the spring-servlet.xml.
 *
 * The test performed is very naive. Just verifies that the application context
 * can be created and the /hello.do bean is of the expected type.
 */
public class SpringServletTest {

  private AbstractXmlApplicationContext parent = null;

  private XmlWebApplicationContext appContext = null;

  @Before
  public void createContexts() throws Exception {

    /* Define a minimal parent context. */
    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<!DOCTYPE beans PUBLIC '-//SPRING//DTD BEAN//EN'"
      + " 'http://www.springframework.org/dtd/spring-beans.dtd'>\n"
      + "<beans>\n"
      + " <bean class='com.globant.katari.core.spring.StringHolder'"
      + "   name='debugMode'>\n"
      + "  <property name='value' value='false'/>\n"
      + " </bean>\n"
      + "</beans>\n";

    parent = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    parent.refresh();

    ServletContext sc;
    sc = new MockServletContext(".", new FileSystemResourceLoader());
    appContext = new XmlWebApplicationContext();
    appContext.setParent(parent);
    appContext.setServletContext(sc);
    appContext.setConfigLocations(new String[] {
      "classpath:com/globant/katari/socialserver/socialserver/view/spring-servlet.xml" });
    appContext.refresh();
  }

  @Test
  public void testHelloType() {
    Object logout = appContext.getBean("/hello.do");
    assertThat(logout, instanceOf(HelloController.class));
  }

  @After
  public void close() {
    appContext.close();
    parent.close();
  }
}

