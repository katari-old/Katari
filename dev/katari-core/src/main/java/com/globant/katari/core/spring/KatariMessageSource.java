/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.support
  .ReloadableResourceBundleMessageSource;

/** A message source that is designed to make translations modularized and
 * extensible.
 *
 * This class works similarly to ReloadableResourceBundleMessageSource, but
 * with some important differentes:
 *
 * - The locale resolution is extended by looking for files in a directory
 *   based on the locale name.
 *
 * - This message source resolves messages in the parent message source first.
 *   Each katari module may provide messages in the languages the author sees
 *   fit. And integrators have the choice of overriding any message by adding
 *   the code to the parent message source.
 *
 * - When resolving a message in the parent, the code is looked up by first
 *   prefixing it with the module name and then the plain code.
 *
 * - You can 'link' message sources by declaring 'dependencies' between them.
 *   If a message is not found in the current message source, it looks for it
 *   in all its dependencies. This is intended to allow a module to resolve a
 *   message that is declared in another module.
 *
 * - In debug mode, when a client wants a message from the message code, this
 *   message source first looks for it in a file with an url of the form
 *   file:[debugPrefx]/basename. The message source checks the modification
 *   date of this file each time.
 *
 * A typical debug prefix is ../katari-local-login/src/main/resources.
 *
 * This resolution on the file system is not guaranteed to work when
 * fallbackToSystemLocale is true. This class defaults fallbackToSystemLocale
 * to false.
 *
 * To clarify, assume that you have a message source with:
 *
 * - basename = com/globant/lang/msg
 *
 * - debug = true
 *
 * - debugPrefix = src/main/resources.
 *
 * And a parent message source with:
 *
 * - basename = lang/msg
 *
 * - debug = true
 *
 * - debugPrefix = ../src/main/webapp/WEB-INF/
 *
 * To resolve a message code C in the locale es_SP, the process is:
 *
 * - First look in the parent:
 *
 * - Look for C in src/main/webapp/WEB-INF/lang_es_SP/msg.properties
 *
 * - Look for C in src/main/webapp/WEB-INF/lang/msg_es_SP.properties
 *
 * - Look for C in src/main/webapp/WEB-INF/lang_es/msg.properties
 *
 * - Look for C in src/main/webapp/WEB-INF/lang/msg_es.properties
 *
 * - Look for C in lang_es_SP/msg.properties
 *
 * - Look for C in lang/msg_es_SP.properties
 *
 * - Look for C in lang_es/msg.properties
 *
 * - Look for C in lang/msg_es.properties
 *
 * - Look in the message source:
 *
 * - Look for C in src/main/resources/com/globant/lang_es_SP/msg.properties
 *
 * - Look for C in src/main/resources/com/globant/lang/msg_es_SP.properties
 *
 * - Look for C in src/main/resources/com/globant/lang_es/msg.properties
 *
 * - Look for C in src/main/resources/com/globant/lang/msg_es.properties
 *
 * - Look for C in com/globant/lang_es_SP/msg.properties
 *
 * - Look for C in com/globant/lang/msg_es_SP.properties
 *
 * - Look for C in com/globant/lang_es/msg.properties
 *
 * - Look for C in com/globant/lang/msg_es.properties
 *
 * Then, repeat the process for the locale selected as default.
 *
 * Finally, repeat the process in each of the dependencies.
 *
 * This way of resolving messages allows:
 *
 * - Module writers to support a set of locales (with msg_[locale].properties).
 *
 * - 3rd parties to add additional languages to a module (with
 *   lang_[locale]/messages.properties.
 *
 * - 3rd parties to redefine any translation.
 *
 * - Module integrators to support additional languages and override any
 *   translation.
 *
 * NOTE: the dependencies mechanism is not yet fully tested and it has many
 * limitations, the main one being that the 'namespace' of the message names is
 * shared between all message sources. So there is a chance that a message
 * defined in a dependent module may be resolved as a different, non intended
 * value.
 *
 * Another limitation is that the dependency mechanism comes into play only
 * after looking for the message in every relevant locale.
 */
public class KatariMessageSource
  extends ReloadableResourceBundleMessageSource {

  /** The class logger.
   */
  private Logger log = LoggerFactory.getLogger(KatariMessageSource.class);

  /** The name of the module that this message source belongs to.
   *
   * This is null for a parent, global, message source.
   */
  private String moduleName;

  /** The locale to use when the message is not found in the requested locale.
   */
  private Locale fallbackLocale = null;

  /** Whether debug mode is enabled.
   *
   * In debug mode, the messages files (normally messages.properties) are first
   * search from the file system. This makes it possible to edit messages files
   * and see the result without a redeploy. Defaults to false.
   */
  private boolean debug = false;

  /** A prefix to use to find the resources in the disk as a file.
   *
   * This is used in debug mode. It is never null.
   */
  private String debugPrefix = "file:.";

  /** A list of message sources that this message source may depend on.
   *
   * If a message is neither found in the parent and in this message source,
   * look for messages in each of the dependencies.
   */
  private final List<KatariMessageSource> dependencies =
      new LinkedList<KatariMessageSource>();

  /** Constructor to be used for the global (without parent) message source.
   *
   * @param theFallbackLocale the locale to use when the message is not found
   * in the requested locale. It cannot be null.
   */
  public KatariMessageSource(final Locale theFallbackLocale) {
    Validate.notNull(theFallbackLocale, "The fallback locale cannot be null.");
    setFallbackToSystemLocale(false);
    fallbackLocale = theFallbackLocale;
  }

  /** Constructor.
   *
   * Creates a KatariMessageSource. This constructor is intended to be used in
   * modules. It inherits the fallbackLocale from the parent.
   *
   * @param theModuleName the name of the module. It cannot be null.
   */
  public KatariMessageSource(final String theModuleName,
      final KatariMessageSource theParent) {
    Validate.notNull(theModuleName, "The module name cannot be null.");
    Validate.notNull(theParent, "The parent cannot be null.");

    setFallbackToSystemLocale(false);
    setParentMessageSource(theParent);
    moduleName = theModuleName;
    fallbackLocale = theParent.fallbackLocale;
  }

  /** Constructor.
   *
   * Creates a KatariMessageSource with dependencies. This constructor is
   * intended to be used in modules. It inherits the fallbackLocale from the
   * parent.
   *
   * In addition to the module name and parent, this constructor takes a list
   * of other message sources that are used the message source being
   * constructed cannot resolve the message.
   *
   * @param theModuleName the name of the module. It cannot be null.
   *
   * @param theDependencies the dependencies of this message source. It cannot
   * be null.
   */
  public KatariMessageSource(final String theModuleName,
      final KatariMessageSource theParent,
      final List<KatariMessageSource> theDependencies) {
    Validate.notNull(theModuleName, "The module name cannot be null.");
    Validate.notNull(theParent, "The parent cannot be null.");
    Validate.notNull(theDependencies, "The dependencies cannot be null.");

    setFallbackToSystemLocale(false);
    setParentMessageSource(theParent);
    moduleName = theModuleName;
    fallbackLocale = theParent.fallbackLocale;
    dependencies.addAll(theDependencies);
  }

  /** {@inheritDoc}
   *
   * Calculates the filenames for the given locale and the fallback locale.
   */
  protected List<String> calculateFilenamesForLocale(final String basename,
      final Locale locale) {
    List<String> filenames = filenamesWithoutFallback(basename, locale);

    if (fallbackLocale != null && !locale.equals(fallbackLocale)) {
      List<String> fallbacks;
      fallbacks = calculateFilenamesForLocale(basename, fallbackLocale);
      for (String fallbackFilename : fallbacks) {
        if (!filenames.contains(fallbackFilename)) {
          filenames.add(fallbackFilename);
        }
      }
    }

    return filenames;
  }

  /** {@inheritDoc}
   *
   * Calculate the filenames for the given bundle basename and Locale,
   * appending language code, country code, and variant code to the directory
   * containing the message and the message itself.
   */
  protected List<String> filenamesWithoutFallback(final String basename,
      final Locale locale) {

    log.trace("Entering calculateFilenamesForLocale('{}', '{}')", basename,
        locale);

    Pattern pattern = Pattern.compile("([^:]+:)?(?:(.*)/)?([^/]+)");
    Matcher matcher = pattern.matcher(basename);
    if (!matcher.matches()) {
      throw new RuntimeException(basename + " does not match " + pattern);
    }
    String protocol = matcher.group(1);
    if (protocol == null) {
      protocol = "";
    }
    String directory = matcher.group(2);
    String fileName  = matcher.group(3);
    if (directory == null) {
      directory = "";
    } else {
      fileName = "/" + fileName;
    }
    log.debug("dir: '{}', file: '{}'", directory, fileName);

    // Directory based locales.
    List<String> fileNames = new LinkedList<String>();
    List<String> basicFileNames = super.calculateFilenamesForLocale(
        directory + fileName, locale);
    if (directory.length() != 0) {
      List<String> dirnames;
      dirnames = super.calculateFilenamesForLocale(directory, locale);
      for (String name : dirnames) {
        name = name + fileName;
        fileNames.add(name);
      }
      // Merge the directories and file names.
      int nameCount = fileNames.size();
      for (int i = 0; i < nameCount; ++i) {
        fileNames.add(2 * i + 1, basicFileNames.get(i));
      }
    } else {
      fileNames.addAll(basicFileNames);
    }

    log.debug("File names {}.", fileNames);

    // Now, calculate the file system based messages.
    List<String> result = new LinkedList<String>();
    if (debug) {
      result = new LinkedList<String>();
      for (String name : fileNames) {
        result.add(calculatePrefixedName(name));
      }
    }
    for (String name : fileNames) {
      result.add(protocol + name);
    }

    log.trace("Leaving calculateFilenamesForLocale with {}.", result);
    return result;
  }

  /** Obtains the prefix relative file name of the provided message properties.
   *
   * This operation can only be called in debug mode.
   *
   * @param fileName the original name of the file. It cannot be null.
   *
   * @return a file name that prefixed with the debug prefix, never null.
   */
  private String calculatePrefixedName(final String fileName) {
    log.trace("Entering calculatePrefixedName");
    Validate.isTrue(debug, "Must be in debug mode.");
    String result = fileName;
    if (result.startsWith("/")) {
      result = debugPrefix + result;
    } else {
      result = debugPrefix + "/" + result;
    }
    log.trace("Leaving calculatePrefixedName with {}", result);
    return result;
  }

  /** {@inheritDoc}
   *
   * Overrides the default implementation to first resolve the message in the
   * parent message source.
   */
  protected String getMessageInternal(final String code, final Object[] args,
      final Locale locale) {
    String message = null;
    // First look in the parent, with the module name.
    if (moduleName != null) {
      message = getMessageFromParent(moduleName + "." + code, args, locale);
    }
    // Then look in the parent, withou the module name.
    if (message == null) {
      message = getMessageFromParent(code, args, locale);
    }
    // Look in my own messages.
    if (message == null) {
      message = super.getMessageInternal(code, args, locale);
    }
    // And finally, if I don't have them, look for the dependencies.
    if (message == null) {
      for (KatariMessageSource messageSource : dependencies) {
        message = messageSource.getMessageInternal(code, args, locale);
      }
    }

    return message;
  }

  /** Sets the debug mode.
   *
   * @param debugEnabled true to enable debug mode, false by default.
   */
  public void setDebug(final boolean debugEnabled) {
    debug = debugEnabled;
    if (debug) {
      setCacheSeconds(0);
    }
  }

  /** Sets the debug prefix.
   *
   * @param prefix a prefix to add to the message properties file to look for
   * messages in the file system. It is a dot by default. A trailing / is
   * removed if present. It cannot be null.
   */
  public void setDebugPrefix(final String prefix) {
    Validate.notNull(prefix, "The prefix cannot be null.");
    if (prefix.endsWith("/")) {
      debugPrefix = "file:" + prefix.substring(0, prefix.length() - 1);
    } else {
      debugPrefix = "file:" + prefix;
    }
  }
}

