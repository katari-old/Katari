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
    loadAppContext();
    String[] beanNames = beanFactory.getBeanDefinitionNames();
    for (int i = 0; i < beanNames.length; ++i) {
      beanFactory.getBean(beanNames[i]);
    }
  }

  public void loadAppContext() {
    beanFactory = new FileSystemXmlApplicationContext(new String[] {
          "src/main/resources/com/globant/katari/core/beans-core.xml",
          "src/test/resources/com/globant/katari/core/userApplicationContext.xml"
        });
    beanFactory.refresh();
  }

  /* Tests if the app context can be loaded twice. This originated on camel
   * using jmx. We disabled it to make it possible t oload two camel contexts
   * in the same VM.
   */
  @Test
  public void testLoadTwice() {
    loadAppContext();
    FileSystemXmlApplicationContext oldBeanFactory = beanFactory;
    loadAppContext();
    oldBeanFactory.close();
  }

  @After
  public void tearDown() {
    beanFactory.close();
    beanFactory = null;
  }
}

