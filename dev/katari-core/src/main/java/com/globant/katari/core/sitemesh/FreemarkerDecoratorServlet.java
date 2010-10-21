/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.sitemesh;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.lang.Validate;
import com.globant.katari.core.web.ServletConfigWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.WebappTemplateLoader;

/** A reimplementation of the freemarker decorator servlet for sitemesh that
 * can specify multiple loading paths.
 *
 * This servlet adds the following parameters:
 *
 * - AdditionalTemplatePaths: The AdditionalTemplatePaths parameter is a comma
 *   separated list of paths following the same syntax as the original
 *   FreemarkerServlet TemplatePath parameter
 *
 * - DebugPrefix: a prefix to prepend to the path component of the TemplatePath
 *   to look for ftl templates in the file system.
 *
 * - AdditionalDebugPrefixes: This is a comma separated list of prefixes to
 *   prepend to the path component of each of the items in
 *   AdditionalTemplatePaths.
 *
 * - debug: The debug parameter enables hot reloading of templates from the
 *   file system. If this is false, DebugPrefix and AdditionalDebugPrefixes are
 *   ignored.
 */
public class FreemarkerDecoratorServlet extends
    com.opensymphony.module.sitemesh.freemarker.FreemarkerDecoratorServlet {

  /** The serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      FreemarkerDecoratorServlet.class);

  /** The position in the url where the resource starts.
   *
   * For example, file:///a/b.c: the prefix is 'file://', the resource is
   * /a/b.c, (starts at position 7).
   *
   * class://a/b.c: the prefix is 'class:/', the resource is /a/b.c, (starts at
   * position 7).
   */
  private static final int RESOURCE_OFFSET = 7;

  /** The value of the TemplatePath initialization parameter.
   *
   * It is used only to add the prefix for debug. It is null in case this
   * parameter was not specified.
   */
  private String templatePath = null;

  /** The value of the AdditionalTemplatePaths initialization parameter.
   *
   * It is null in case this parameter was not specified.
   */
  private String additionalPaths = null;

  /** The value of the DebugPrefix initialization parameter.
   */
  private String debugPrefix = null;

  /** The value of the AdditionalDebugPrefixes initialization parameter.
   *
   * It is null in case this parameter was not specified.
   */
  private String additionalPrefixes = null;

  /** The value of the debug initialization parameter.
   */
  private boolean debug = false;

  /** A servlet config that strips the DebugPrefix, AdditionalTemplatePaths,
   * AdditionalDebugPrefixes and debug initialization parameter from the list
   * of parameters.
   *
   * This is due to the fact that adding unrecognized parameters to sitemesh
   * FreemarkerDecoratorServlet throws an exception.
   */
  private static final class ConfigWithoutAdditionlPaths extends
    ServletConfigWrapper {

    /** Init parameters to strip, never null.
     */
    private static Set<String> strippedParameters = new HashSet<String>();

    static {
      strippedParameters.add("AdditionalTemplatePaths");
      strippedParameters.add("DebugPrefix");
      strippedParameters.add("AdditionalDebugPrefixes");
      strippedParameters.add("debug");
    }

    /** Builds a new ConfigWithoutAdditionlPaths.
     *
     * @param delegate The original servlet config.
     */
    private ConfigWithoutAdditionlPaths(final ServletConfig delegate) {
      super(delegate);
    }

    /** Returns the names of the servlet's initialization parameters as an
     * Enumeration of String objects, or an empty Enumeration if the servlet
     * has no initialization parameters.
     *
     * This enumeration does not include AdditionalTemplatePaths, DebugPrefix,
     * AdditionalDebugPrefixes nor debug.
     *
     * @return an enumeration with the names of the parameters.
     */
    @SuppressWarnings("unchecked")
    public Enumeration getInitParameterNames() {
      List<String> original = Collections.list(
          (Enumeration<String>) super.getInitParameterNames());
      List<String> modified = new LinkedList<String>();
      for (String name : original){
        if (!strippedParameters.contains(name)) {
          modified.add(name);
        }
      }
      return Collections.enumeration(modified);
    }

    /*
      Enumeration original = super.getInitParameterNames();
      List modified = new Vector();
      Vector modified = new Vector();
      while (original.hasMoreElements()) {
        String name = (String) original.nextElement();
        if (!strippedParameters.contains(name)) {
          modified.add(name);
        }
      }
      return modified.elements();
    }
    */
  }

  /** Initializes the servlet.
   *
   * @param config The container provided servlet config.
   *
   * @throws ServletException in case of error.
   */
  public void init(final ServletConfig config) throws ServletException {
    log.trace("Entering init");
    // Save the additional paths to be used in init();
    templatePath = config.getInitParameter("TemplatePath");
    log.debug("Initialized templatePath to {}", templatePath);
    additionalPaths = config.getInitParameter("AdditionalTemplatePaths");
    log.debug("Initialized additionalPaths to {}", additionalPaths);
    debugPrefix = config.getInitParameter("DebugPrefix");
    log.debug("Initialized debugPrefix to {}", debugPrefix);
    additionalPrefixes = config.getInitParameter("AdditionalDebugPrefixes");
    log.debug("Initialized additionalPrefixes to {}", additionalPrefixes);
    String debugParameter = config.getInitParameter("debug");
    if (debugParameter != null) {
      debug = Boolean.valueOf(debugParameter);
    }
    log.debug("Initialized debug to {}", debug);
    super.init(new ConfigWithoutAdditionlPaths(config));
    log.trace("Leaving init");
  }

  /** Initializes the freemarker configuration.
   *
   * Adds the additional template paths to the freemarker configuration.
   *
   * @throws ServletException in case of error.
   */
  public void init() throws ServletException {
    super.init();
    getLoaders();
  }

  /** Testing helper.
   *
   * This is the init implementation, but returns the list of loaders. This
   * list is only used in the unit test.
   *
   * @return the list of loaders, used only for unit testing.
   *
   * @throws ServletException if initialization failed.
   */
  protected TemplateLoader[] getLoaders() throws ServletException {
    // Use the saved additional paths to modify the freemarker configuration.
    List<TemplateLoader> loaders = new LinkedList<TemplateLoader>();

    // Add the loader for the file-system based TemplatePath.
    if (debug && debugPrefix != null && templatePath != null) {
      TemplateLoader loader = createLoader(debugPrefix, templatePath);
      if (loader != null) {
        loaders.add(loader);
      }
    }
    // Adds the default loader.
    loaders.add(getConfiguration().getTemplateLoader());

    if (additionalPaths != null) {
      List<TemplateLoader> additionalLoaders = getAdditionalLoaders(
          additionalPaths, additionalPrefixes);
      loaders.addAll(additionalLoaders);
    }

    TemplateLoader[] loaderArray;
    loaderArray = loaders.toArray(new TemplateLoader[loaders.size()]);
    if (loaders.size() != 0) {
      MultiTemplateLoader loader = new MultiTemplateLoader(loaderArray);
      getConfiguration().setTemplateLoader(loader);
    }
    return loaderArray;
  }

  /** Creates a template loader for a path and a prefix.
   *
   * See addPrefix for parameter information.
   *
   * @param prefix The prefix to add. it cannot be null.
   *
   * @param path The path to add the prefix to. It cannot be null.
   *
   * @return a file system relative template loader. If can be null if the
   * generated path is invalid. An invalid path must be ignored.
   */
  private TemplateLoader createLoader(final String prefix, final String path) {
    FileTemplateLoader loader = null;
    String filePath = addPrefix(prefix, path);
    try {
      loader = new FileTemplateLoader(new File(filePath));
      log.debug("Added '{}' to template loader path.", filePath);
    } catch (IOException e) {
      // We ignore this exception.
      log.warn("Error loading {}.", filePath, e);
    }
    return loader;
  }

  /** Creates the loaders for the additional template paths.
   *
   * @param additionalTemplatePaths a ; separated list of paths of the form
   * [file|class]://[something], or /[something]. It cannot be null.
   *
   * @param additionalDebugPrefixes a ; separated list of prefixes to be
   * prepended to each of the additionalTemplatePaths. Each new path is added
   * to the list before the original one, so it takes priority over it.
   *
   * @return a list of template loaders, one for each path. Never returns null.
   *
   * @throws ServletException if initialization failed.
   */
  private List<TemplateLoader> getAdditionalLoaders(final String
      additionalTemplatePaths, final String additionalDebugPrefixes)
      throws ServletException {
    Validate.notNull(additionalTemplatePaths);

    String[] paths = additionalPaths.split(";");
    String[] prefixes = null;

    if (debug && additionalDebugPrefixes != null) {
      prefixes = additionalDebugPrefixes.split(";");

      Validate.isTrue(paths.length == prefixes.length && debug,
          "AdditionalTemplatePaths and AdditionalDebugPrefixes must have the"
          + " same number of elements.");
    }

    List<TemplateLoader> loaders = new LinkedList<TemplateLoader>();
    for (int i = 0; i < paths.length; ++i) {
      String path = paths[i].trim();
      Validate.notEmpty(path, "Empty template path. Perhaps a trailing ;?");
      log.debug("Analyzing '{}'.", path);
      if (path.startsWith("class://")) {
        // Adds the debug loader.
        if (prefixes != null) {
          String prefix = prefixes[i].trim();
          TemplateLoader loader = createLoader(prefix, path);
          if (loader != null) {
            loaders.add(loader);
          }
        }
        // Strips the class:/
        String classPath = path.substring(RESOURCE_OFFSET);
        loaders.add(new ClassTemplateLoader(getClass(), classPath));
        log.debug("Added '{}' to template loader path.", classPath);
      } else if (path.startsWith("file://")) {
        String filePath = path.substring(RESOURCE_OFFSET);
        try {
          loaders.add(new FileTemplateLoader(new File(filePath)));
          log.debug("Added '{}' to template loader path.", filePath);
        } catch (IOException e) {
          throw new ServletException("Loading file " + filePath, e);
        }
      } else {
        loaders.add(new WebappTemplateLoader(getServletContext(), path));
        log.debug("Added '{}' to template loader path.", path);
      }
    }
    return loaders;
  }

  /** Adds a prefix to a path.
   *
   * @param prefix The prefix to add. it cannot be null.
   *
   * @param path The path to add the prefix to. This path may be of the form
   * class://[something], file://[something] or has no :. The prefix is only
   * added when path starts with class:. It cannot be null.
   *
   * @return a file system relative path that will represent the same resource
   * as the path parameter. The prefix is only applied to a classpath located
   * resource (class://). Never returns null.
   */
  private String addPrefix(final String prefix, final String path) {
    Validate.notNull(prefix, "The prefix cannot be null.");
    Validate.notNull(path, "The path cannot be null.");
    if (path.trim().startsWith("class://")) {
      String classPath = path.trim().substring(RESOURCE_OFFSET);
      return prefix.trim() + classPath;
    } else {
      return path;
    }
  }
}

