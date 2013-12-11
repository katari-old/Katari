package com.globant.katari.gadgetcontainer;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.hibernate.coreuser.domain.CoreUserDetails;
import com.globant.katari.tools.SpringTestUtilsBase;

/** Container for the spring module application context.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class SpringTestUtils extends SpringTestUtilsBase {

  private static final String MODULE =
    "classpath:com/globant/katari/gadgetcontainer/applicationContext.xml";

  /** The static instance for the singleton.*/
  private static SpringTestUtils instance;

  private static final SpringTestUtils INSTANCE = new SpringTestUtils();

  private SpringTestUtils() {
    super(new String[]{MODULE}, null);
  }

  /** Retrieves the intance.
   * @return the instance, never null.
   */
  public static synchronized SpringTestUtils get() {
    if (instance == null) {
      instance = new SpringTestUtils();
    }
    return instance;
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
