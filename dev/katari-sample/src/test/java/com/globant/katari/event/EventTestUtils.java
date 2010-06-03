/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/** Utilites for testing the event module.
 */
public class EventTestUtils {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(EventTestUtils.class);

  /** The module Bean fectory.
   */
  private static ApplicationContext beanFactory = null;

  /** This method returns a BeanFactory.
   *
   * @return a BeanFactory, never null.
   */
  public static synchronized ApplicationContext getBeanFactory() {
    if (beanFactory == null) {
      log.info("Creating a beanFactory");
      beanFactory = new FileSystemXmlApplicationContext(
          new String[] {"./src/main/webapp/WEB-INF/applicationContext.xml",
            "classpath:/com/globant/katari/event/testApplicationContext.xml"}
          );
    }
    return beanFactory;
  }
}

