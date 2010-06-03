/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.trails;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import junit.framework.TestCase;

public class ModuleBeansTest extends TestCase {

  /* Tests if the module-beans.xml file can be loaded.
  */
  public void testLoad() {
    ApplicationContext beanFactory = new FileSystemXmlApplicationContext(
        new String[] {
          "classpath:com/globant/katari/core/applicationContext.xml",
          "src/main/resources/com/globant/katari/trails/module.xml",
          "src/main/resources/com/globant/katari/trails/module-beans.xml",
          "src/test/resources/com/globant/katari/trails/userApplicationContext.xml"
        });
    String[] beanNames = beanFactory.getBeanDefinitionNames();
    for (int i = 0; i < beanNames.length; ++i) {
      beanFactory.getBean(beanNames[i]);
    }
  }
}

