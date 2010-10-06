/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.lang.Validate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.mock.web.MockServletContext;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;

import static org.easymock.EasyMock.*;

import java.sql.Connection;
import javax.sql.DataSource;

import com.globant.katari.editablepages.domain.PageRepository;

/** Utilites for testing the editable pages module.
 */
public class TestUtils {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(TestUtils.class);

  /** The global application bean factory.
   */
  private static ApplicationContext beanFactory;

  /** The servlet bean factory.
   */
  private static ApplicationContext servletBeanFactory;

  /** This method returns a BeanFactory.
   *
   * @return a BeanFactory, never null.
   */
  public static ApplicationContext getBeanFactory() {
    if (beanFactory == null) {
      log.info("Creating a beanFactory");
      MockServletContext sc = new MockServletContext("./src/main/webapp",
          new FileSystemResourceLoader());
      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
      appContext.setServletContext(sc);
      appContext.setConfigLocations(new String[] {
        "classpath:/applicationContext.xml",
      });
      appContext.refresh();
      beanFactory = appContext;
    }
    return beanFactory;
  }

  /** Returns the bean factory for the servlet.
   *
   * @return a BeanFactory.
   */
  public static synchronized ApplicationContext getServletBeanFactory() {
    if (servletBeanFactory == null) {
      log.info("Creating a beanFactory");
      servletBeanFactory = new FileSystemXmlApplicationContext(
          new String[]
          {"classpath:/com/globant/katari/editablepages/view/spring-servlet.xml"},
          getBeanFactory());
    }
    return servletBeanFactory;
  }

  /** Removes all test pages.
   */
  public static void deleteTestPages() {
    // Removes all the pages.
    try {
      DataSource dataSource;
      dataSource = (DataSource) getBeanFactory().getBean("dataSource");
      Connection connection = dataSource.getConnection();
      connection.createStatement().execute("delete from pages");
      connection.close();
    } catch(Exception e) {
      throw new RuntimeException("Error removing pages.", e);
    }
  }

  /** Obtains the page repository from the spring application context.
   *
   * @return The page repository, never null.
   */
  public static PageRepository getPageRepository() {
    return (PageRepository) getServletBeanFactory().getBean("pageRepository");
  }
  
  /** Obtains the site name from the spring application context.
  *
  * @return The site Name, never null.
  */
 public static String getSiteName() {
   return (String) getServletBeanFactory().getBean("editable-pages.siteName");
 }

 /** Creates a mock user with the specified role and registers it in acegi
  * context holder.
  *
  * @param role The role to add to the mock user. It cannot be null.
  */
  public static void setRole(final String role) {
    Validate.notNull(role, "The role cannot be null.");
    GrantedAuthority editor = createMock(GrantedAuthority.class);
    expect(editor.getAuthority()).andReturn(role);
    expectLastCall().anyTimes();
    replay(editor);

    GrantedAuthority[] authorities = {editor};

    Authentication authentication = createMock(Authentication.class);
    expect(authentication.getAuthorities()).andReturn(authorities);
    expectLastCall().anyTimes();
    replay(authentication);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}

