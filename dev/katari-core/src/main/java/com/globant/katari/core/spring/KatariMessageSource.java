/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.support
  .ReloadableResourceBundleMessageSource;
import org.springframework.core.io.FileSystemResourceLoader;

/** A message source that, in debug mode, obtains the messages from a file on
 * disk.
 *
 * In debug mode, when a client wants a message from the message code, this
 * message source first looks for it in a file with an url of the form
 * file:[debugPrefx]/basename. The message source checks the modification date
 * of this file each time.
 *
 * A typical debug prefix is ../katari-local-login/src/main/resources.
 *
 * Note: this fallback on the file system is not guaranteed to work when
 * fallbackToSystemLocale is true. This class defaults fallbackToSystemLocale
 * to false.
 */
public class KatariMessageSource
  extends ReloadableResourceBundleMessageSource {

  /** The class logger.
   */
  private Logger log = LoggerFactory.getLogger(KatariMessageSource.class);

  /** The length of the classpath: string.
   */
  private static final int CLASSPATH_PREFIX_LENGTH = 10;

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
  
  /** Constructor.
   */
  public KatariMessageSource() {
    setFallbackToSystemLocale(false);
  }

  /**
   * {@inheritDoc}
   *
   * This implementation adds the paths to the messages in the physical file
   * system, prepending the debugPrefix to the messages.properties file (this
   * is only done in debug mode).
   */
  @Override
  protected List<String> calculateFilenamesForLocale(final String basename,
      final Locale locale) {
    log.trace("Entering calculateFilenamesForLocale");
    List<String> fileNames;
    fileNames = super.calculateFilenamesForLocale(basename, locale);
    List<String> result = fileNames;
    if (debug) {
      log.debug("Debug mode enabled, calculating file system path base on {}",
          debugPrefix);
      // Add the file names for debug mode, ie: file names that point to a
      // location in the file system instead of the classpath.
      result = new LinkedList<String>();
      for (String name : fileNames) {
        result.add(calculatePrefixedName(name));
      }
      result.add(calculatePrefixedName(basename));
      result.addAll(fileNames);
    }
    log.trace("Leaving calculateFilenamesForLocale");
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
    if (result.startsWith("classpath:")) {
      result = result.substring(CLASSPATH_PREFIX_LENGTH);
    }
    if (result.startsWith("/")) {
      result = debugPrefix + result;
    } else {
      result = debugPrefix + "/" + result;
    }
    log.trace("Leaving calculatePrefixedName with {}", result);
    return result;
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

