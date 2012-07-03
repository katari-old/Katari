/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.testsupport;

import com.globant.katari.tools.SpringTestUtilsBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/** Utility class to give support to test cases.
 */
public final class SpringTestUtils extends SpringTestUtilsBase {

  /** A logger.
   */
  private static Logger log = LoggerFactory.getLogger(SpringTestUtils.class);

  /** The single instance, initialized as a singleton.
   */
  private static SpringTestUtils instance = null;

  private static String[] globalConfigurationFiles = new String[] {
    "/WEB-INF/applicationContext.xml",
    "/WEB-INF/applicationContextRuntime.xml"
  };

  private static String[] servletConfigurationFiles = new String[] {
    "classpath:/com/globant/katari/sample/time/view/spring-servlet.xml"
  };

  /** Time entry module Bean factory, as a singleton.
   */
  private static ApplicationContext timeModuleBeanFactory = null;

  /** A private constructor for a singleton.
   */
  private SpringTestUtils() {
    super(globalConfigurationFiles, servletConfigurationFiles);
  }

  /** Gets the single instance of this class.
   */
  public static synchronized SpringTestUtils get() {
    if (instance == null) {
      instance = new SpringTestUtils();
    }
    return instance;
  }

  /** This method returns a BeanFactory.
   *
   * @return a BeanFactory for the time module, never null.
   */
  public static synchronized ApplicationContext getTimeModuleBeanFactory() {
    if (timeModuleBeanFactory == null) {
      log.info("Creating a beanFactory");
      timeModuleBeanFactory = new FileSystemXmlApplicationContext(
        new String[]
        {"classpath:/com/globant/katari/sample/time/view/spring-servlet.xml"},
        get().getBeanFactory());
    }
    return timeModuleBeanFactory;
  }
}

