/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.view;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.net.URL;
import java.util.Properties;

import java.io.IOException;
import java.io.InputStream;

import java.util.Map;
import java.util.HashMap;

/** Holds the resource information for one package.
 */
class ResourceSet {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(ResourceSet.class);

  /** The length of the 'mimeType.' prefix.
   */
  private static final int MIMETYPE_LENGTH = 9;

  /** The base classpath for this resources, never null.
   */
  private String basePath;

  /** The map of resource extensions to the corresponding mime type.
   *
   * It contains entries of the form "gif", "image/gif". If a resource
   * extension for this resource set is not found here, it is not served.
   * This is never null.
   */
  private Map<String, String> mimeTypes = new HashMap<String, String>();

  /** A prefix to use to find the resources in the disk as a file.
   *
   * This is used in debug mode. It is never null.
   */
  private String debugPrefix = ".";

  /** Initializes a ResourceSet from the provided properties file.
   *
   * The property file must contain the keys staticContentPath,
   * mimeType.[file-extension] and debugPrefix:
   *
   * staticContentPath=com/globant/katari/jsmodule/view
   *
   * mimeType.js=text/javascript
   *
   * mimeType.gif=image/gif
   *
   * debugPrefix=../katari-jsmodule/src/test/resources
   *
   * @param propertiesUrl url of the property file. It cannot be null.
   */
  @SuppressWarnings("unchecked")
  ResourceSet(final URL propertiesUrl) {
    Validate.notNull(propertiesUrl, "The base path cannot be null.");

    Properties resourceSetProperties = new Properties();
    InputStream propertiesStream = null;
    try {
      propertiesStream = propertiesUrl.openStream();
      resourceSetProperties.load(propertiesStream);
    } catch (IOException e) {
      throw new RuntimeException("Error reading " + propertiesUrl, e);
    } finally {
       try {
        propertiesStream.close();
      } catch(IOException e) {
        log.error("Error closing stream, ignored.", e);
      }
    }

    basePath = resourceSetProperties.getProperty("staticContentPath");
    debugPrefix = resourceSetProperties.getProperty("debugPrefix");

    Enumeration<String> names;
    names = (Enumeration<String>) resourceSetProperties.propertyNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      if (name.startsWith("mimeType.")) {
        String mimeType = resourceSetProperties.getProperty(name);
        mimeTypes.put(name.substring(MIMETYPE_LENGTH), mimeType);
      }
    }
  }

  /** The base classpath for this resources.
   *
   * @return the classpath. It never returns null.
   */
  public String getBasePath() {
    return basePath;
  }

  /** The map of resource extensions to the corresponding mime type.
   *
   * It contains entries of the form "gif", "image/gif". If a resource
   * extension for this resource set is not found here, it is not served.
   * This is never null.
   *
   * @return the mime types, never null.
   */
  public Map<String, String> getMimeTypes() {
    return mimeTypes;
  }

  /** A prefix to use to find the resources in the disk as a file.
   *
   * This is used in debug mode.
   *
   * @return the debug prefix, never null.
   */
  public String getDebugPrefix() {
    return debugPrefix;
  }
}

