/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.login;

import javax.servlet.Filter;

import junit.framework.TestCase;

import org.acegisecurity.ui.AuthenticationEntryPoint;

import static org.easymock.classextension.EasyMock.*;

public class LoginConfigurationSetterTest extends TestCase {

  private DelegatingEntryPoint entryPoint;

  /** The delegating logout filter. */
  private ConfigurableFilterProxy logoutFilter;

  /** The delegating processing filter. */
  private ConfigurableFilterProxy authFilter;

  private LoginProvider provider;

  @Override
  public void setUp() {
    AuthenticationEntryPoint delegateEntryPoint;
    delegateEntryPoint = createMock(AuthenticationEntryPoint.class);

    Filter delegateLogoutFilter = createMock(Filter.class);

    Filter delegateAuthFilter = createMock(Filter.class);

    provider = new LoginProvider(delegateEntryPoint, delegateAuthFilter,
        delegateLogoutFilter);

    entryPoint = createMock(DelegatingEntryPoint.class);
    entryPoint.setDelegate(delegateEntryPoint);
    replay(entryPoint);

    logoutFilter = createMock(ConfigurableFilterProxy.class);
    logoutFilter.setDelegate(delegateLogoutFilter);
    replay(logoutFilter);

    authFilter = createMock(ConfigurableFilterProxy.class);
    authFilter.setDelegate(delegateAuthFilter);
    replay(authFilter);
  }

  public void testSetLoginConfiguration_once() {
    LoginConfigurationSetter loginConf = new
      LoginConfigurationSetter(entryPoint, logoutFilter, authFilter);
    loginConf.setLoginConfiguration(provider);
    verify(entryPoint);
    verify(logoutFilter);
    verify(authFilter);
  }

  public void testSetLoginConfiguration_twice() {

    LoginConfigurationSetter loginConf = new
      LoginConfigurationSetter(entryPoint, logoutFilter, authFilter);
    loginConf.setLoginConfiguration(provider);
    // now validate
    try {
      loginConf.setLoginConfiguration(provider);
      fail();
    } catch (IllegalStateException e){
      // Test passed
    }
  }
}

