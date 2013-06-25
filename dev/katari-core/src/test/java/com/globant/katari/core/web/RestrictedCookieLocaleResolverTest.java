/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.HashSet;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

public class RestrictedCookieLocaleResolverTest {

  private Locale defaultLocale = new Locale("es");
  private MockHttpServletRequest request = new MockHttpServletRequest();
  private MockHttpServletResponse response = new MockHttpServletResponse();

  private HashSet<Locale> supportedLocales = new HashSet<Locale>();
  private String cookieName = CookieLocaleResolver.DEFAULT_COOKIE_NAME;

  private RestrictedCookieLocaleResolver resolver
      = new RestrictedCookieLocaleResolver(supportedLocales, defaultLocale);


  @Before public void setUp() {
    supportedLocales.add(defaultLocale);
    supportedLocales.add(new Locale("ja", "JP", "N"));
    supportedLocales.add(new Locale("ja", "JP"));
    supportedLocales.add(new Locale("ja"));
    supportedLocales.add(new Locale("th", "TH"));
    supportedLocales.add(new Locale("th"));
    supportedLocales.add(new Locale("pt"));
  }

  @Test public void resolveLocale_unknown() {
    request.setCookies(new Cookie(cookieName, "ge"));
    Locale locale = resolver.resolveLocale(request);
    assertThat(locale, is(defaultLocale));
  }

  @Test public void resolveLocale_unknownWithCountry() {
    request.setCookies(new Cookie(cookieName, "ge_GE"));
    Locale locale = resolver.resolveLocale(request);
    assertThat(locale, is(defaultLocale));
  }

  @Test public void resolveLocale_unknownWithVariant() {
    request.setCookies(new Cookie(cookieName, "ge_GE_GE"));
    Locale locale = resolver.resolveLocale(request);
    assertThat(locale, is(defaultLocale));
  }

  @Test public void resolveLocale_known() {
    request.setCookies(new Cookie(cookieName, "th"));
    Locale locale = resolver.resolveLocale(request);
    assertThat(locale, is(new Locale("th")));
  }

  @Test public void resolveLocale_variantNotKnown() {
    request.setCookies(new Cookie(cookieName, "th_TH_TH"));
    Locale locale = resolver.resolveLocale(request);
    assertThat(locale, is(new Locale("th", "TH")));
  }

  @Test public void resolveLocale_countryNotKnown() {
    request.setCookies(new Cookie(cookieName, "pt_BR"));
    Locale locale = resolver.resolveLocale(request);
    assertThat(locale, is(new Locale("pt")));
  }

  @Test public void resolveLocale_countryKnown() {
    request.setCookies(new Cookie(cookieName, "th_TH"));
    Locale locale = resolver.resolveLocale(request);
    assertThat(locale, is(new Locale("th", "TH")));
  }

  /** I create a new kind of variant called: "N" because an spring bug.
   * https://jira.springsource.org/browse/SPR-9420
   */
  @Test public void resolveLocale_fullKnown() {
    request.setCookies(new Cookie(cookieName, "ja_JP_N"));
    Locale locale = resolver.resolveLocale(request);
    assertThat(locale, is(new Locale("ja", "JP", "N")));
  }

  @Test public void setLocale() {
    resolver.setLocale(request, response, new Locale("pt", "BR"));
    Cookie cookie = response.getCookie(cookieName);
    assertThat(cookie.getValue(), is("pt"));
  }
}

