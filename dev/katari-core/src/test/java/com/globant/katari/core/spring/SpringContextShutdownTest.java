/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import junit.framework.TestCase;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.beans.factory.BeanCreationException;

/** Tests if the spring context shutdown calls the bean destroy methods even if
 * the bean factory could not create a bean because of an exception.
 */
public class SpringContextShutdownTest extends TestCase {

  static boolean initCalled = false;
  static boolean destroyCalled = false;

  protected void init() {
    initCalled = true;
  }

  protected void initFail() {
    fail();
  }

  protected void destroy() {
    destroyCalled = true;
  }

  public void setUp() {
    initCalled = false;
    destroyCalled = false;
  }

  /** Creates two beans, the second throws an exception in its init method.
   *
   * This test validates that the destroy method is called in the first bean.
   * This test is here mainly because I could not find the appropriate
   * documentation in spring. If this changes, this test will fail and we will
   * need to provide another shutdown mechanism for beans.
   */
  public void testDestroy() {

    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<!DOCTYPE beans PUBLIC '-//SPRING//DTD BEAN//EN'"
      + " 'http://www.springframework.org/dtd/spring-beans.dtd'>\n"
      + "<beans>\n"
      + "<bean id='i2'"
      + " class='com.globant.katari.core.spring.SpringContextShutdownTest'\n"
      + " init-method='init' destroy-method='destroy'/>\n"
      + "<bean id='i1'"
      + " class='com.globant.katari.core.spring.SpringContextShutdownTest'\n"
      + " init-method='initFail'/>\n"
      + "</beans>\n";

    AbstractXmlApplicationContext context;
    try {
      context = new AbstractXmlApplicationContext() {
        protected Resource[] getConfigResources() {
          return new Resource[] {new ByteArrayResource(beans.getBytes())};
        }
      };
      context.refresh();
      context.close();
    } catch (BeanCreationException e) {
      // ignored ...
    }
    assertTrue(initCalled);
    assertTrue(destroyCalled);
  }
}

