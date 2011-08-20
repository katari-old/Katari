/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

/** Filter to change the locale.
 *
 * This filter expects a katari-lang request parameter and sets the locale
 * accordingly in the locale resolver and in the LocaleContextHolder.
 *
 * It expects a locale resolver that supports setLocale (ej:
 * CookieLocaleResolver), AcceptHeaderLocaleResolver won't work.
 */
public final class ChangeLocaleFilter implements Filter {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(Utf8EncodingFilter.class);

  /** The locale resolver to save the locale if it changed, never null.
   */
  LocaleResolver localeResolver;

  /** Constructor.
   *
   * @param theLocaleResolver locale resolver to save the locale obtained by
   * this filter. It cannot be null.
   */
  public ChangeLocaleFilter(final LocaleResolver theLocaleResolver) {
    Validate.notNull(theLocaleResolver, "The locale resolver cannot be null.");
    localeResolver = theLocaleResolver;
  }

  /** {@inheritDoc}
   *
   * It currently does nothing.
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    log.trace("Entering init");
    // Do nothing.
    log.trace("Leaving init");
  }

  /** {@inheritDoc}
  */
  public void doFilter(final ServletRequest request, final ServletResponse
      response, final FilterChain chain) throws IOException,
      ServletException {

    log.trace("Entering doFilter.");

    if (!(request instanceof HttpServletRequest)) {
      throw new RuntimeException("Not an http request");
    }
    if (!(response instanceof HttpServletResponse)) {
      throw new RuntimeException("Not an http request");
    }

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String newLocale = request.getParameter("katari-lang");
    if (newLocale != null) {
      Locale locale = StringUtils.parseLocaleString(newLocale.toLowerCase());
      LocaleContextHolder.setLocale(locale);
      localeResolver.setLocale(httpRequest, httpResponse, locale);
    } else {
      Locale locale = localeResolver.resolveLocale(httpRequest);
      LocaleContextHolder.setLocale(locale);
    }
    try {
      chain.doFilter(request, response);
    } finally {
      LocaleContextHolder.resetLocaleContext();
    }

    log.trace("Leaving doFilter.");
  }

  /** Called by the container when the filter is about to be destroyed.
   *
   * This implementation is empty.
   */
  public void destroy() {
    log.trace("Entering destroy");
    // Do nothing.
    log.trace("Leaving destroy");
  }
}

