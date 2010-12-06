package com.globant.katari.core.spring;


import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * Test for the NullPropertyParser.
 * 
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class NullPropertyParserTest {

  @Test
  public void testCreateNullBean() throws Exception {
    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<beans xmlns='http://www.springframework.org/schema/beans'\n"
      + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
      + "  xmlns:katari='http://www.globant.com/schema/katari'\n"
      + "  xsi:schemaLocation='http://www.springframework.org/schema/beans\n"
      + "    http://www.springframework.org/schema/beans/spring-beans-2.0.xsd\n"
      + "    http://www.globant.com/schema/katari\n"
      + "    http://www.globant.com/schema/katari/katari.xsd'>\n"
      + "  <katari:null id='just_a_null_bean'/>"
      + "</beans>\n";
      System.setProperty("authentication.mode", "CAS");
      AbstractXmlApplicationContext context;
      context = new AbstractXmlApplicationContext() {
        protected Resource[] getConfigResources() {
          return new Resource[] {new ByteArrayResource(beans.getBytes())};
        }
      };
      context.refresh();
      Assert.assertNull(context.getBean("just_a_null_bean"));
  }

}
