/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

public class PropertiesParserTest {

  @Test
  public void testClasspathProperties() {

    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
      + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
      + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
      + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
      + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
      + "    http://www.globant.com/schema/katari\n"
      + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
      + "  <katari:properties name='katari.props'"
      + "   location='classpath:/com/globant/katari/core/spring/test.properties'/>\n" 
      + "</beans>\n";

    AbstractXmlApplicationContext context;
    context = new AbstractXmlApplicationContext() {
      protected Resource[] getConfigResources() {
        return new Resource[] {new ByteArrayResource(beans.getBytes())};
      }
    };
    context.refresh();
    Object properties = context.getBean("katari.props");
    assertThat(properties, is(Properties.class));
    assertEquals("CAS", ((Properties)properties).getProperty("authentication.mode"));
    context.close();
  }
}

