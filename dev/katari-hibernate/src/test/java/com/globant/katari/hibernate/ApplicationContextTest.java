/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.junit.Test;
import org.junit.After;

public class ApplicationContextTest {

  private FileSystemXmlApplicationContext beanFactory = null;

  @After
  public void tearDown() {
    if (beanFactory != null) {
      beanFactory.close();
      beanFactory = null;
    }
  }

  /* Tests if the katari application context can be loaded.
  */
  @Test
  public void testLoad() {
    beanFactory = new FileSystemXmlApplicationContext(
        new String[] {
          "classpath:/com/globant/katari/core/beans-core.xml",
          "classpath:/com/globant/katari/hibernate/beans-hibernate.xml",
          "src/test/resources/com/globant/katari/hibernate/userApplicationContext.xml"
        });
    String[] beanNames = beanFactory.getBeanDefinitionNames();
    for (int i = 0; i < beanNames.length; ++i) {
      beanFactory.getBean(beanNames[i]);
    }
  }
}

