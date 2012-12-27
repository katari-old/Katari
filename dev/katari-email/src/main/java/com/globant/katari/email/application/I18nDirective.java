package com.globant.katari.email.application;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.springframework.context.i18n.LocaleContextHolder;

import com.globant.katari.core.spring.KatariMessageSource;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/** This freemarker template allows to search bundles within the
 * the katari message source.
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class I18nDirective implements TemplateDirectiveModel {

  /** The katari message source, it's never null.*/
  private final KatariMessageSource messageSource;

  /** The main key for the message.*/
  private static final String KEY = "code";

  /** The default message if the searched key is not found.*/
  private static final String DEFAULT = "text";

  /** The default message if the searched key is not found.*/
  private static final String ARGUMENTS = "args";

  /** The katari message source.
   * @param katariMessageSource the message source, cannot be null.
   */
  public I18nDirective(final KatariMessageSource katariMessageSource) {
    Validate.notNull(katariMessageSource, "The message source cannot be null");
    messageSource = katariMessageSource;
  }

  /** {@inheritDoc}. */
  @SuppressWarnings("rawtypes")
  public void execute(final Environment env, final Map params,
      final TemplateModel[] loopVars, final TemplateDirectiveBody body)
          throws TemplateException, IOException {

    Locale locale = LocaleContextHolder.getLocale();

    String key = getStringValue(KEY, params);
    String defaultMessage = getStringValue(DEFAULT, params);
    String arguments = getStringValue(ARGUMENTS, params);

    Validate.notNull(key, "The key cannot be null");

    Object[] theArguments = null;
    if (arguments != null) {
      theArguments = arguments.split(",");
    }

    String message = null;
    if (defaultMessage == null) {
      message = messageSource.getMessage(key, theArguments, locale);
    } else {
      message = messageSource.getMessage(key, theArguments, defaultMessage,
          locale);
    }

    if (message != null) {
      Writer writer = env.getOut();
      writer.write(message);
      IOUtils.closeQuietly(writer);
    }

  }

  /** Retrieves from the parameters the parameter and transforms it
   * into a string.
   * @param key the key to search and transform.
   * @param parameters the parameteres.
   * @return the string representation or null.
   */
  private String getStringValue(final String key, final Map<?,?> parameters) {
    SimpleScalar scalar = (SimpleScalar) parameters.get(key);
    if (scalar == null) {
      return null;
    }
    return scalar.getAsString();
  }

}
