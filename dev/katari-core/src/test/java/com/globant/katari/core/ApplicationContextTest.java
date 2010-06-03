/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import junit.framework.TestCase;

public class ApplicationContextTest extends TestCase {

  /* Tests if the katari application context can be loaded.
  */
  public void testLoad() {
    ApplicationContext beanFactory = new FileSystemXmlApplicationContext(
        new String[] {
          "src/main/resources/com/globant/katari/core/beans-core.xml",
          "src/test/resources/com/globant/katari/core/userApplicationContext.xml"
        });
    String[] beanNames = beanFactory.getBeanDefinitionNames();
    for (int i = 0; i < beanNames.length; ++i) {
      beanFactory.getBean(beanNames[i]);
    }
  }
}

