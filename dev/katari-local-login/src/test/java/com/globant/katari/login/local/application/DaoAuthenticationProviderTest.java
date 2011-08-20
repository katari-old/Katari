/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.application;

import static org.easymock.EasyMock.*;

import java.util.Locale;
import org.junit.Test;

import org.springframework.context.MessageSource;
import org.acegisecurity.Authentication;

public class DaoAuthenticationProviderTest {

  @Test
  public void setMessageSource() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

    Authentication authentication = createMock(Authentication.class);

    MessageSource messageSource1 = createMock(MessageSource.class);
    expect(messageSource1.getMessage(
          find("AbstractUserDetailsAuthenticationProvider.onlySupports"),
          isNull(Object[].class), isA(String.class),
          isA(Locale.class))).andReturn("something");
    replay(messageSource1);

    MessageSource messageSource2 = createMock(MessageSource.class);
    replay(messageSource2);

    provider.setMessageSource(messageSource1);
    provider.setMessageSource(messageSource2);

    try {
      // authenticate calls getMessage on the message source.
      provider.authenticate(authentication);
    } catch(Exception e) {
      // authenticate throws an exception that we must ignore.
    }
    verify(messageSource1);
    // It should only call getMessage on the first message source.
    verify(messageSource2);
  }
}

