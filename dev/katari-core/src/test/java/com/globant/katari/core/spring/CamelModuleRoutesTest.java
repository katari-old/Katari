/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class CamelModuleRoutesTest {

  private SpringCamelContext context;

  private ClassPathXmlApplicationContext beanFactory;

  @Before
  public void setUp() throws Exception {
    beanFactory = new ClassPathXmlApplicationContext(
        "com/globant/katari/core/spring/camelModuleRoutesContext.xml");
    context = (SpringCamelContext) beanFactory.getBean("katari.eventBus");
  }

  @After
  public void tearDown() {
    beanFactory.close();
    beanFactory = null;
  }

  @Test
  public void testSend_withListeners() throws Exception {
    ProducerTemplate template = context.createProducerTemplate();
    String response = (String) template.requestBody("direct:toListener",
        "message");
    assertThat(response, is("Response 1"));
  }

  // Some sample listeners
  public static class Listener1 {
    public String a(final String message) {
      assertThat(message, is("message"));
      return "Response 1";
    }
  }
}

