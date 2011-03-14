/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.junit.Test;
import org.junit.After;

public class ApplicationContextTest {

  FileSystemXmlApplicationContext beanFactory;

  /* Tests if the katari application context can be loaded. Fails with a
   * runtime exception if not.
  */
  @Test
  public void testLoad() {
    beanFactory = new FileSystemXmlApplicationContext(new String[] {
          "src/main/resources/com/globant/katari/core/beans-core.xml",
          "src/test/resources/com/globant/katari/core/userApplicationContext.xml"
        });
    String[] beanNames = beanFactory.getBeanDefinitionNames();
    for (int i = 0; i < beanNames.length; ++i) {
      beanFactory.getBean(beanNames[i]);
    }
  }

  @After
  public void tearDown() {
    beanFactory.close();
    beanFactory = null;
  }
}

