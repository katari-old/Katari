/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.wicket;

import java.util.Locale;

import org.apache.commons.lang.Validate;
import org.apache.wicket.Component;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.springframework.context.MessageSource;

/**
 * A {@link IStringResourceLoader} implementation that resolves message
 * codes using a Spring {@link MessageSource}.
 * @author pablo.saavedra
 */
public class MessageSourceStringResourceLoader
        implements IStringResourceLoader {

  /**
   * The default message. This is used so if the key is not found we can return
   * null and let other {@link IStringResourceLoader}s in the chain resolve the
   * message.
   */
  private static final String DEFAULT = MessageSourceStringResourceLoader.class
      .getName() + "_DEFAULT";
  /**
   * The backing message source.
   */
  private MessageSource delegate;

  /**
   * Creates a new string resource loader backed by the given message source.
   * @param theDelegate The message source to use, cannot be null.
   */
  public MessageSourceStringResourceLoader(final MessageSource theDelegate) {
    Validate.notNull(theDelegate, "The message source cannot be null.");
    this.delegate = theDelegate;
  }

  /**
   * {@inheritDoc}
   */
  public String loadStringResource(final Class<?> clazz, final String key,
      final Locale locale, final String style) {
    //TODO Create a MessageSourceResolvable with the arguments.
    return getMessageInternal(key, locale);
  }

  /**
   * {@inheritDoc}
   */
  public String loadStringResource(final Component component,
      final String key) {
  //TODO Create a MessageSourceResolvable with the arguments.
    return getMessageInternal(key, Locale.getDefault());
  }

  /**
   * Attempts to resolve the message using the underlying message source. In
   * case they key is not found it returns null instead of throwing an
   * exception.
   * @param code
   *          The message code.
   * @param locale
   *          The locale to use.
   * @return The message for that key and locale, or null if none is found.
   */
  private String getMessageInternal(final String code, final Locale locale) {
    String message = delegate.getMessage(code, null, DEFAULT, locale);
    if (DEFAULT.equals(message)) {
      //No message found, carry on.
      return null;
    }
    return message;
  }
}

