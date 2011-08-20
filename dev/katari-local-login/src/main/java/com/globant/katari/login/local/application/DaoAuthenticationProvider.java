/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.application;

import org.apache.commons.lang.Validate;

import org.springframework.context.MessageSource;

/** An acegi DaoAuthenticationProvider that allows the developer to override
 * the message source.
 *
 * Acegi DaoAuthenticationProvider is a MessageSourceAware bean, so spring
 * injects the application context through setMessageSource. If you inject your
 * own implementation, your message source ends up being ovewriten by spring.
 *
 * This class solves this issue by only accepting just one messageSource and
 * ignoring the rest.
 */
public class DaoAuthenticationProvider
  extends org.acegisecurity.providers.dao.DaoAuthenticationProvider {

  /** Flag to indicate if this class will ignore the call to setMessageSource.
   */
  boolean ignoreSetMessageSource = false;

  /** Sets the message source that will convert message codes to strings.
   *
   * If this operation is called mutiple times, only the first one is
   * considered, all the other ones are ignored.
   *
   * @param messageSource the message source to use to resolve message codes.
   * It cannot be null.
   */
  @Override
  public void setMessageSource(final MessageSource messageSource) {
    if (!ignoreSetMessageSource) {
      Validate.notNull(messageSource);
      super.setMessageSource(messageSource);
      ignoreSetMessageSource = true;
    }
  }
}

