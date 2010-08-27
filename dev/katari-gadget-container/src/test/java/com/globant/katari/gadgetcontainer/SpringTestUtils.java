package com.globant.katari.gadgetcontainer;

import javax.servlet.ServletContext;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.globant.katari.hibernate.coreuser.domain.CoreUserDetails;
import com.globant.katari.hibernate.coreuser.domain.CoreUser;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

/** Container for the spring module application context.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class SpringTestUtils {

  private static final String MODULE =
    "classpath:com/globant/katari/gadgetcontainer/applicationContext.xml";

  private static final SpringTestUtils INSTANCE = new SpringTestUtils();

  private final XmlWebApplicationContext appContext;

  private SpringTestUtils() {
    ServletContext sc;
    sc = new MockServletContext(".", new FileSystemResourceLoader());
    appContext = new XmlWebApplicationContext();
    appContext.setServletContext(sc);
    appContext.setConfigLocations(new String[] { MODULE });
    appContext.refresh();
  }

  /** @return {@link XmlWebApplicationContext} the spring application context.
   */
  public static final XmlWebApplicationContext getContext() {
    return INSTANCE.appContext;
  }

  /** Sets the logged in user, used for testing only.
   *
   * TODO decide if this is the correct place for this.
   */
  public static final void setLoggedInUser(final CoreUser user) {
    CoreUserDetails details = new CoreUserDetails(user) {
      private static final long serialVersionUID = 1L;
      public GrantedAuthority[] getAuthorities() {
        return null;
      }
      public String getPassword() {
        return null;
      }
    };
    details.getCoreUser();
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(details, "");
    authentication.setDetails(details);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
