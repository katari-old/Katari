/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.EasyMock.*;

import java.util.Locale;

import javax.servlet.FilterChain;

import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;

public class ChangeLocaleFilterTest {

  MockHttpServletRequest request;
  MockHttpServletResponse response;
  MockFilterConfig filterConfig;
  FilterChain chain;

  @Before
  public void setUp() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();

    // Mocks the Filter.
    chain = createNiceMock(FilterChain.class);
    replay(chain);
  }

  @Test
  public void doFilter_noLangParameter() throws Exception {
    LocaleResolver localeResolver = createMock(LocaleResolver.class);
    expect(localeResolver.resolveLocale(request)).andReturn(null);
    replay(localeResolver);

    // Execute the test.
    ChangeLocaleFilter filter = new ChangeLocaleFilter(localeResolver);
    filter.init(filterConfig);

    filter.doFilter(request, response, chain);
    verify(localeResolver);
    filter.destroy();
  }

  @Test
  public void doFilter_langParameter() throws Exception {
    LocaleResolver localeResolver = createMock(LocaleResolver.class);
    localeResolver.setLocale(eq(request), eq(response), isA(Locale.class));
    replay(localeResolver);

    // Execute the test.
    ChangeLocaleFilter filter = new ChangeLocaleFilter(localeResolver);

    request.setParameter("lang", "es_AR");
    filter.doFilter(request, response, chain);
    verify(localeResolver);
  }
}

