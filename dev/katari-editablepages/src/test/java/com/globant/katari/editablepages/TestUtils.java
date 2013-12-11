/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages;

import org.apache.commons.lang.Validate;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;

import static org.easymock.EasyMock.*;

import java.sql.Connection;
import javax.sql.DataSource;

import com.globant.katari.editablepages.domain.PageRepository;
import com.globant.katari.tools.SpringTestUtilsBase;

/** Utilites for testing the editable pages module.
 */
public class TestUtils extends SpringTestUtilsBase {

  /** The static instance for the singleton.*/
  private static TestUtils instance;

  /**
   * @param theGlobalConfigurationFiles
   * @param theServletConfigurationFiles
   */
  protected TestUtils(final String[] theGlobalConfigurationFiles,
      final String[] theServletConfigurationFiles) {
    super(theGlobalConfigurationFiles, theServletConfigurationFiles);
  }

  /** Retrieves the intance.
   * @return the instance, never null.
   */
  public static synchronized TestUtils get() {
    if (instance == null) {
      instance = new TestUtils(
        new String[] {"classpath:/applicationContext.xml"},
        new String[] {"classpath:/com/globant/katari/editablepages/view/"
            + "spring-servlet.xml"});
    }
    return instance;
  }

  /** Removes all test pages.
   */
  public static void deleteTestPages() {
    // Removes all the pages.
    try {
      DataSource dataSource;
      dataSource = (DataSource) get().getBeanFactory().getBean("dataSource");
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
    return (PageRepository) get().getServletBeanFactory().getBean(
        "pageRepository");
  }

  /** Obtains the site name from the spring application context.
  *
  * @return The site Name, never null.
  */
 public static String getSiteName() {
   return (String) get().getServletBeanFactory().getBean(
       "editable-pages.siteName");
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

