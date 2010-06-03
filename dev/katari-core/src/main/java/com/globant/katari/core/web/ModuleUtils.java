/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import org.apache.commons.lang.Validate;

/** Utility class for dealing with module related urls and names.
 *
 * @author ulises.bocchio
 */
public final class ModuleUtils {

  /** Module suffix. */
  private static final String MODULE_SUFFIX = ".module";

  /** The string after the context path where all modules are mapped.
   */
  private static final String MODULE_URL_PREFIX = "/module/";

  /** A cache for the MODULE_URL_PREFIX length in characters.
   */
  private static final int PREFIX_LENGTH = MODULE_URL_PREFIX.length();

  /**
   * Private Constructor for a utility class.
   */
  private ModuleUtils() {
  }

  /** Obtains the module name from an url.
   *
   * If the url is of the form /module/[module-name]/[something], this method
   * returns [module-name]. Otherwise, it returns null. This method does not
   * verify if the module is effectively registered in katari.
   *
   * @param url the url to parse. It cannot be null.
   *
   * @return the module name, or null if the url does not start with /module/.
   */
  public static String getModuleNameFromUrl(final String url) {
    Validate.notNull(url, "The url cannot be null");
    if (url.startsWith(MODULE_URL_PREFIX)) {
      int endIdx = url.indexOf('/', PREFIX_LENGTH);
      if (endIdx == -1) {
        endIdx = url.length();
      }
      return url.substring(PREFIX_LENGTH, endIdx);
    }
    return null;
  }

  /**
   * Returns a url fragment relative to the module.
   *
   * If the url is of the form [context-path]/module/[module-name]/[something],
   * this method returns [something].
   *
   * @param url the url to strip. It cannot be null.
   *
   * @return the stripped url. This url always begins with '/'. Never returns
   * null.
   */
  public static String stripModuleNameFromUrl(final String url) {
    String moduleName = getModuleNameFromUrl(url);
    if (moduleName == null) {
      throw new IllegalArgumentException("The Url does not contain a module"
          + " name.");
    }
    String beginning = MODULE_URL_PREFIX + moduleName;
    return url.substring(beginning.length());
  }

  /**
   * Returns the real context path ignoring the module path.
   *
   * The module container servlet tricks the module into believing that the
   * context path consists of the war context path followed by '/module/'
   * followed by the module name. This operation returns the original war
   * context path.
   *
   * If the url is of the form [context-path]/module/[module-name]/[something],
   * this method returns [context-path].
   *
   * @param moduleUri the module uri with the context path. It cannot be null.
   *
   * @return the real context path.
   */
  public static String getGlobalContextPath(final String moduleUri) {
    Validate.notNull(moduleUri, "The context path cannot be null");
    Validate.isTrue(moduleUri.matches(".*/module/.*"),
        "the given context path does not contain any module");
    int moduleIdx = moduleUri.indexOf(MODULE_URL_PREFIX);
    return moduleUri.substring(0, moduleIdx);
  }

  /**
   * Obtains the module name based on the bean name. This method strips the
   * trailing '.module' if present, and returns the result.
   * @param beanName
   *          The name of the spring bean. It cannot be null.
   * @return the module name. Never returns null.
   */
  public static String getModuleNameFromBeanName(final String beanName) {
    Validate.notNull(beanName, "The bean name cannot be null");
    String name = beanName;
    if (beanName.endsWith(MODULE_SUFFIX)) {
      name = beanName.substring(0, beanName.length() - MODULE_SUFFIX.length());
    }
    return name;
  }
}

