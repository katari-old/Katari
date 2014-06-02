/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.view;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.net.URL;

import java.io.IOException;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/** Manages the result sets for all available packages.
 */
public class ResourceSets {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(ResourceSets.class);

  /** The resource sets, keyed by base path.
   */
  private SortedMap<String, ResourceSet> resourceSets
    = new TreeMap<String, ResourceSet>();

  /** Constructor, builds a new ResourceSets instance.
   */
  public ResourceSets() {
    String name = "META-INF/katari-resource-set";
    Enumeration<URL> resourceUrls;
    try {
      resourceUrls = getClass().getClassLoader().getResources(name);
    } catch (IOException e) {
      throw new RuntimeException("Error loading " + name, e);
    }

    while(resourceUrls.hasMoreElements()) {
      URL url = resourceUrls.nextElement();
      log.debug("Found resource set {}.", url);
      ResourceSet resourceSet = new ResourceSet(url);
      resourceSets.put(resourceSet.getBasePath(), resourceSet);
    }
  }

  /** Obtains the resourceSet that matches the provided path.
   *
   * @param path The path that we are searching for. It cannot be null.
   *
   * @return the resource set, or null if the path cannot be served by any
   * resource set.
   */
  public ResourceSet find(final String path) {
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

  public String getContentType(final String path) {
    ResourceSet set = find(path);
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
    return null;
  }
}

