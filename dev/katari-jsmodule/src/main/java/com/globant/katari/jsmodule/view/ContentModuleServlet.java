/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.view;

import javax.servlet.ServletConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletException;
import java.util.Enumeration;
import java.net.URL;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.globant.katari.core.web.BaseStaticContentServlet;
import com.globant.katari.jsmodule.domain.BundleCache;

/** Servlet that serves static content (gif, png, css, js) from the classpath.
 *
 * Static content is packaged in jar files with an additional metadata file
 * that defines which classpath entries are served from that jar. The metadata
 * file is META-INF/katari-resource-set. This resource is a property file of
 * the form:
 *
 * staticContentPath=com/globant/katari/jsmodule/view
 *
 * mimeType.js=text/javascript
 *
 * mimeType.gif=image/gif
 *
 * debugPrefix=../katari-jsmodule/src/test/resources
 *
 * This servlet serves resources of the form
 * [context-path]/[servlet-path]/[package]/file.ext.
 *
 * The [package] part must start with one of the paths that are being served
 * from this servlet, as specified by the property staticContentPath in some
 * katari-resource-set file.
 *
 * If the requested file is in package /com/globant/katari/jsmodule/bundle,
 * this servlet assumes it is a bundle. See ResolveDependenciesCommand.
 *
 * As a future enhacement, this servlet will be able to bundle and compress css
 * files.
 */
public class ContentModuleServlet extends BaseStaticContentServlet {

  /** The serialization version number.
   *
   * This number must change every time a new serialization incompatible change
   * is introduced in the class.
   */
  private static final long serialVersionUID = 1;

  /** Default path for the cached bundled files.
   *
   * The default value is "/com/globant/katari/jsmodule/bundle/".
   */
  public static final String BUNDLE_PATH =
    "/com/globant/katari/jsmodule/bundle/";

  /** Content type for ".js" files.
   *
   * The value is "text/javascript".
   */
  private static final String JS_CONTENT_TYPE = "text/javascript";

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      ContentModuleServlet.class);

  /** The resource sets, keyed by base path.
   */
  private SortedMap<String, ResourceSet> resourceSets
    = new TreeMap<String, ResourceSet>();

  /** Contains the cache of the bundled files, never null.
   */
  private BundleCache cache;

  /** Makes a new instance of the {@link ContentModuleServlet} with the given
   * {@link BundleCache}.
   *
   * @param theCache The {@link BundleCache} contains the cache of the bundled
   * files. Cannot be null.
   */
  public ContentModuleServlet(final BundleCache theCache) {
    Validate.notNull(theCache, "The Bundle Cache cannot be null.");
    cache = theCache;
  }

  /** Initializes the servlet.
   *
   * It sets the default packages for static resources.
   *
   * @param config The servlet configuration, ignored in this implementation.
   *
   * @throws ServletException in case of error.
   */
  @Override
  public void init(final ServletConfig config) throws ServletException {
    log.trace("Entering init");
    super.init(config);
    String name = "META-INF/katari-resource-set";
    Enumeration<URL> resourceUrls;
    try {
      resourceUrls = getClass().getClassLoader().getResources(name);
    } catch (IOException e) {
      throw new ServletException("Error loading " + name, e);
    }

    while(resourceUrls.hasMoreElements()) {
      URL url = resourceUrls.nextElement();
      log.debug("Found resource set {}.", url);
      ResourceSet resourceSet = new ResourceSet(url);
      resourceSets.put(resourceSet.getBasePath(), resourceSet);
    }

    log.trace("Leaving init");
  }

  /** {@inheritDoc}
   */
  @Override
  protected String getContentType(final String path) {
    log.trace("Entering getContentType('{}')", path);

    if (path.contains(BUNDLE_PATH)) {
      // We only support javascript.
      String bundleName = path.substring(path.lastIndexOf("/") + 1);
      if (StringUtils.isNotEmpty(bundleName)) {
        log.trace("Leaving getContentType with {}", JS_CONTENT_TYPE);
        return JS_CONTENT_TYPE;
      }
    } else {
      ResourceSet set = findResourceSet(path);
      if (set != null) {
        String basePath = set.getBasePath();
        Map<String, String> mimeTypes;
        mimeTypes = resourceSets.get(basePath).getMimeTypes();
        int dotPosition = path.lastIndexOf('.');
        if (dotPosition != -1) {
          String contentType = mimeTypes.get(path.substring(dotPosition + 1));
          log.trace("Leaving getContentType with {}", contentType);
          return contentType;
        }
      }
    }
    log.trace("Leaving getContentType with null");
    return null;
  }

  /** {@inheritDoc}
   */
  @Override
  protected InputStream findInputStream(final String path) throws IOException {
    log.trace("Entering findInputStream('{}')", path);

    Validate.notNull(path, "The resource path cannot be null");

    if (path.contains(BUNDLE_PATH)) {
      String bundleName = path.substring(path.lastIndexOf("/") + 1);
      String bundleContent = cache.findContent(bundleName);
      if (StringUtils.isNotEmpty(bundleName)) {
        log.trace("Leaving findInputStream with a file resource");
        return new ByteArrayInputStream(bundleContent.getBytes("UTF-8"));
      }
    } else {
      ResourceSet set = findResourceSet(path);
      if (set != null) {
        if (isInDebugMode()) {
          String filePath = buildPath(set.getDebugPrefix(), path);
          log.debug("In debug mode, looking for file {}", filePath);
          File file = new File(filePath);
          if (file.exists()) {
            log.trace("Leaving findInputStream with a file resource");
            return new FileInputStream(file);
          }
        }
        // The resource path must not start with '/', or it will fail under
        // certain containers.
        String resource;
        if (path.startsWith("/")) {
          resource = path.substring(1);
        } else {
          resource = path;
        }

        log.debug("Looking for resource {}", resource);
        return getResourceAsStream(resource);
      }
    }

    log.trace("Leaving findInputStream");
    return null;
  }

  /** Obtains the resourceSet that matches the provided path.
   *
   * @param path The path that we are searching for. It cannot be null.
   *
   * @return the resource set, or null if the path cannot be served by any
   * resource set.
   */
  private ResourceSet findResourceSet(final String path) {
    log.trace("Entering findResourceSet {}", path);
    Validate.notNull(path, "the path cannot be null");
    SortedMap<String, ResourceSet> subset = resourceSets.headMap(path);
    if (subset.size() != 0) {
      String basePath = subset.lastKey();
      if (path.startsWith(basePath)) {
        log.trace("Leaving findResourceSet with {}", basePath);
        return resourceSets.get(basePath);
      }
    }
    log.trace("Leaving findResourceSet with null");
    return null;
  }
}

