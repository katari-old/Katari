/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.Set;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/** A locale resolver that restricts the locale to a list of supported ones.
 */
public final class RestrictedCookieLocaleResolver
  extends CookieLocaleResolver {

  /** The set of supported locales, never null.
   */
  private Set<Locale> supportedLocales;

  /** Constructor, builds a locale resolver.
   */
  public RestrictedCookieLocaleResolver(final Set<Locale> theSupportedLocales,
      final Locale defaultLocale) {
    Validate.notNull(theSupportedLocales,
        "The set of supported locales cannot be null.");
    Validate.notNull(defaultLocale, "The default locale cannot be null.");
    supportedLocales = theSupportedLocales;
    super.setDefaultLocale(defaultLocale);
  }

  /** {@inheritDoc}
   *
   * Resolves the locale from the request, as CookieLocaleResolver does.
   *
   * If the locale resoled by CookieLocaleResolver is not in the list of
   * supported locales, then this operation returns the default one.
   */
  public Locale resolveLocale(final HttpServletRequest request) {
    Locale locale = super.resolveLocale(request);
    return getMatchingLocale(locale);
  }

  /** {@inheritDoc}
   *
   * Sets the current locale to the best mathing the one passed as parameter.
   */
  public void setLocale(final HttpServletRequest request,
      final HttpServletResponse response, final Locale locale) {
    super.setLocale(request, response, getMatchingLocale(locale));
  }

  /** Obtains the best matching locale for the provided one, among the
   * supported locales.
   *
   * @param locale the locale to match. It cannot be null.
   *
   * @return a locale best matching the provided locale. Never null.
   */
  private Locale getMatchingLocale(final Locale locale) {
    if (supportedLocales.contains(locale)) {
      return locale;
    }
    if (!locale.getVariant().equals("")) {
      Locale locale2 = new Locale(locale.getLanguage(), locale.getCountry());
      if (supportedLocales.contains(locale2)) {
        return locale2;
      }
    }
    if (!locale.getCountry().equals("")) {
      Locale locale2 = new Locale(locale.getLanguage());
      if (supportedLocales.contains(locale2)) {
        return locale2;
      }
    }
    return getDefaultLocale();
  }
}

