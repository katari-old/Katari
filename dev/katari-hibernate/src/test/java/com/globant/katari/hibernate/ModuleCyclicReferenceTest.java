/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.Validate;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author rcunci
 */
public class ModuleCyclicReferenceTest {

  /**
   * Cyclic dependency test.
   *
   * The loading order is as follows:
   * <pre>
   *  ConfigurableModule
   *   |
   *   |---> FilterMapping
   *   |        \--> FilterAndParameters
   *   |                \--> SampleFilter
   *   |                        \--> katari.sessionFactory (1)
   *   |
   *   \---> ModuleBeanPostProcessor
   *            \--> katari.sessionFactory
   *                    \--> FAIL because (1) is on creation
   * </pre>
   */
  @Test
  public void testLoad() {
    new FileSystemXmlApplicationContext( new String[] {
      "classpath:/com/globant/katari/core/applicationContext.xml",
      "src/test/resources/com/globant/katari/hibernate/userApplicationContext.xml",
      "src/test/resources/com/globant/katari/hibernate/spring/cyclicApplicationContext.xml"
    });
  }

  /**
   * Test filter, does nothing.
   *
   * @author rcunci
   */
  public static class SampleFilter implements Filter {

    private SessionFactory sessionFactory;

    public SampleFilter(final SessionFactory sessionFactory) {
      Validate.notNull(sessionFactory);
      this.sessionFactory = sessionFactory;
    }

    public void destroy() {}

    public void doFilter(final ServletRequest request,
        final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {}

    public void init(final FilterConfig filterConfig) throws ServletException {}

    public SessionFactory getSessionFactory() {
      return sessionFactory;
    }
  }
}

