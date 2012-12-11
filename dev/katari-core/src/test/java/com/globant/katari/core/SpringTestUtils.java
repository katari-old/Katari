package com.globant.katari.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**  Utility class to give support to test cases.
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class SpringTestUtils {

  /** Bean factory, as a singleton. */
  private static ApplicationContext beanFactory = null;

  /** private constructor.*/
  private SpringTestUtils() {
  }

  /** This method returns a BeanFactory.
  *
  * @return the global BeanFactory, never null.
  */
  public static synchronized ApplicationContext getBeanFactory() {
    FileSystemXmlApplicationContext appContext;
    appContext = new FileSystemXmlApplicationContext();
    appContext.setConfigLocations(new String[] {
        "src/test/resources/com/globant/katari/core/userApplicationContext.xml"
    });
    appContext.refresh();
    beanFactory = appContext;
    return beanFactory;
  }

  /** This method returns the given bean instance by name.
   * @param name the name of the bean to retrieve.
   * @return the instance or null.
   */
  public static Object getBean(final String name) {
    return getBeanFactory().getBean(name);
  }

}
