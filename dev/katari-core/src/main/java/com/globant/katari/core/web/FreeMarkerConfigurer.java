/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.List;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.Validate;

import freemarker.cache.TemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.ClassTemplateLoader;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A freemarker configurer that registers a template loader to load the
 * "katari.ftl" macro library.
 *
 * It also supports loading the templates from the file system if debug mode is
 * enabled, so that modifications of the template are picked up without having
 * to redeploy the application.
 */
public class FreeMarkerConfigurer extends
  org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer {

  /** The class logger.
   */
  private Logger log = LoggerFactory.getLogger(FreeMarkerConfigurer.class);

  /** The length of the classpath: string.
   */
  private static final int CLASSPATH_PREFIX_LENGTH = 10;

  /** Whether debug mode is enabled.
   *
   * In debug mode, the templates are first search from the file system. This
   * makes it possible to edit ftl files and see the result without a redeploy.
   */
  private boolean debug = false;

  /** A prefix to use to find the resources in the disk as a file.
   *
   * Only used in debug mode.
   */
  private String debugPrefix = ".";

  /** Constructor, builds a FreeMarkerConfigurer.
   *
   * This constructor changes the default preferFileSystemAccess to false.
   *
   * Spring tries to convert the templateLoaderPath to a file system directory.
   * Maven puts the test-classes directory before the classes directory in the
   * classpath, so spring resolves templateLoaderPath to the test-classes
   * directory if the view directory is found there. But the templates are
   * found in the classes directory, so spring is unable to resolve the views.
   * preferFileSystemAccess=false forces spring to load templates from the
   * classpath, avoiding this problem.
   */
  public FreeMarkerConfigurer() {
    setPreferFileSystemAccess(false);
  }

  /** Creates a TemplateLoader that searches for templates in the file system
   * and classloader.
   *
   * @param templateLoaderPath the path to load templates from
   *
   * @return In debug mode, it returns a MultiTemplateLoader wrapping a file
   * system loader and a classpath loader. Otherwise, it simply returns the
   * classpath loader.
   */
  @Override
  protected TemplateLoader getTemplateLoaderForPath(final String
      templateLoaderPath) {
    log.trace("Entering getTemplateLoaderForPath({})", templateLoaderPath);

    TemplateLoader classpathLoader = new
      SpringTemplateLoader(getResourceLoader(), templateLoaderPath);

    TemplateLoader loader = null;

    if (debug) {
      String path = templateLoaderPath;
      if (path.startsWith("classpath:")) {
        path = path.substring(CLASSPATH_PREFIX_LENGTH);
      }
      if (!path.startsWith("/")) {
        // Force a / at the begining.
        path = "/" + path;
      }

      String fileTemplatePath = debugPrefix + path;
      log.debug("Debug mode enabled, attempt to load templates from {}",
          fileTemplatePath);

      try {
        TemplateLoader[] loaders = new TemplateLoader[2];
        loaders[0] = new FileTemplateLoader(new File(fileTemplatePath));
        loaders[1] = classpathLoader;
        loader = new MultiTemplateLoader(loaders);
      } catch (IOException e) {
        // We fall back to the standard loader.
        log.debug("Could not find {}. Using normal classpath loader.",
            fileTemplatePath);
        loader = classpathLoader;
      }

    } else {
      log.debug("Debug mode not enabled, using SpringTemplateLoader");
      loader = classpathLoader;
    }
    Validate.notNull(loader, "Error setting the loader.");
    log.trace("Leaving getTemplateLoaderForPath");
    return loader;
  }

  /** Registers an additional ClassTemplateLoader for the katari-provided
   * macros.
   *
   * The new ClassTemplateLoader is added to the end of the list.
   *
   * @param templateLoaders The current list of template loaders. It cannot be
   * null.
   */
  @SuppressWarnings("unchecked")
  protected void postProcessTemplateLoaders(final List templateLoaders) {
    super.postProcessTemplateLoaders(templateLoaders);
    templateLoaders.add(new ClassTemplateLoader(
          FreeMarkerConfigurer.class, ""));
  }

  /** Sets the debug mode.
   *
   * @param debugEnabled true to enable debug mode, false by default.
   */
  public void setDebug(final boolean debugEnabled) {
    debug = debugEnabled;
  }

  /** Sets the debug prefix.
   *
   * @param prefix a prefix to add to the template path to look for it in the
   * file system. It is a dot by default. A trailing / is removed if present.
   * It cannot be null.
   */
  public void setDebugPrefix(final String prefix) {
    Validate.notNull(prefix, "The prefix cannot be null.");
    if (prefix.endsWith("/")) {
      debugPrefix = prefix.substring(0, prefix.length() - 1);
    } else {
      debugPrefix = prefix;
    }
  }
}

