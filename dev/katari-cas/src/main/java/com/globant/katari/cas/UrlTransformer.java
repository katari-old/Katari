/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import org.apache.commons.lang.Validate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Transforms urls according to some specification.
 *
 * A source url can be transformed to a target url by changing the original
 * port number or the path. Port tranformation can be specified directly or in
 * a system property.
 */
public class UrlTransformer {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(UrlTransformer.class);

  /** The replacement server name.
   *
   * If null, the server fragment of the original url is kept unmodified.
   */
  private String replacementServerName = null;

  /** The regular expression to match the path with.
   *
   * This can include grouping operations that can be referenced in the
   * replacement. If null, the path is not matched to perform the replacement.
   */
  private String pathRegex = null;

  /** The path replacement string.
   *
   * If pathRegex has been specified, this can include references to the groups
   * in the regex. If null, no replacement takes place. If pathRegex is null
   * and this is not null, then the path takes this value.
   */
  private String pathReplacement = null;

  /** The port number tranformation specification.
   *
   * If it is null the port number is not transformed. If it starts with '+' or
   * '-', then the number is added or substracted from the original port
   * number. Otherwise, it must be a number specifying the port number.
   */
  private String portSpecification = null;

  /** Builds an identity url transformer.
   */
  public UrlTransformer() {
  }

  /** Builds a new url transformer.
   *
   * @param theReplacementServerName The new server name. If null, the
   * transformation leaves the server name as is.
   *
   * @param thePortSpecification The port specification used to contruct the
   * port number of the target url based on the source url.
   *
   * The posible options are:
   *
   * -n: the target port is obtained substracting n from the source port.
   *
   * +n: the target port is obtained adding n to the source port.
   *
   * n: the target port is n.
   *
   * null: the target port is the same as the source port.
   *
   * Otherwise, it is the name of a system property that contains the port
   * specification.
   *
   * @param thePathRegex The regular expression to match the path with. This
   * can include grouping operations that can be referenced in the replacement.
   * If null, the path is not matched to perform the replacement.
   *
   * @param thePathReplacement The path replacement string. The path
   * replacement string. If pathRegex has been specified, this can include
   * references to the groups in the regex. If null, no replacement takes
   * place. If pathRegex is null and this is not null, then the path takes this
   * value.
   */
  public UrlTransformer(final String theReplacementServerName, final String
      thePortSpecification, final String thePathRegex, final String
      thePathReplacement) {

    if (thePathRegex != null && thePathReplacement == null) {
      throw new RuntimeException("The path replacemnet cannot be null if the"
          + " path regex is not.");
    }
    boolean isProperty = thePortSpecification != null
      && (!Character.isDigit(thePortSpecification.charAt(0))
          && !isPortRelative(thePortSpecification));
    if (isProperty) {
      portSpecification = System.getProperty(thePortSpecification);
    } else {
      portSpecification = thePortSpecification;
    }
    pathRegex = thePathRegex;
    pathReplacement = thePathReplacement;
    replacementServerName = theReplacementServerName;
  }

  /** Tranforms the url according to the specification.
   *
   * @param scheme The url protocol scheme of the source url, for example,
   * http.
   *
   * @param serverName The server name of the source url.
   *
   * @param port The port number of the source url.
   *
   * @param path The url path of the source url.
   *
   * @return Returns the transformed url.
   */
  public String transform(final String scheme, final String serverName,
      final int port, final String path) {
    if (log.isTraceEnabled()) {
      log.trace("Entering transform('" + scheme + "', '" + serverName + "', "
          + port + ", '" + path + "'");
    }
    StringBuffer url = new StringBuffer();
    url.append(scheme).append("://").append(transformServerName(serverName))
      .append(":").append(transformPort(port));

    String result = createUrl(url.toString(), transformPath(path));

    if (log.isTraceEnabled()) {
      log.trace("Leaving transform with " + url);
    }
    return result;
  }

  /** Transforms the server fragment of an url.
   *
   * This simply changes the original server name to the transformed server
   * name, or it leaves it unmodified if the transformed server name is null.
   *
   * @param serverName The server name to transform. It cannot be null.
   *
   * @return Returns the transformed server name.
   */
  private String transformServerName(final String serverName) {
    Validate.notNull(serverName, "The server cannot be null");
    String transformedServerName;
    if (replacementServerName == null) {
      // Do not modify the path.
      transformedServerName = serverName;
    } else {
      // Transform the path according to the url.
      transformedServerName = replacementServerName;
    }
    return transformedServerName;
  }

  /** Transforms the path according to pathRegex and pathReplacement.
   *
   * @param path The path to transform. It cannot be null.
   *
   * @return Returns the transformed path.
   */
  private String transformPath(final String path) {
    Validate.notNull(path, "The path cannot be null");
    String transformedPath;
    if (pathReplacement == null) {
      // Do not modify the path.
      transformedPath = path;
    } else if (pathRegex == null) {
      // Change the path without match.
      transformedPath = pathReplacement;
    } else {
      // Transform the path according to the url.
      transformedPath = path.replaceFirst(pathRegex, pathReplacement);
    }
    return transformedPath;
  }

  /** Transforms the port number.
   *
   * @param port The port number to transform. It cannot be null.
   *
   * @return Returns the transformed port.
   */
  private int transformPort(final int port) {
    Validate.notNull(port, "The port cannot be null");
    int transformedPort = port;
    if (portSpecification != null) {
      int offset = intValue(portSpecification);
      if (isPortRelative(portSpecification)) {
        transformedPort += offset;
      } else {
        transformedPort = offset;
      }
    }
    return transformedPort;
  }

  /** Converts a string to an integer, allowing an initial +.
   *
   * @param value The string value to convert. It cannot be null.
   *
   * @return Returns the integer represented in the value.
   */
  private int intValue(final String value) {
    Validate.notNull(value, "The value to convert cannot be null");
    String conformingValue = value;
    if (value.startsWith("+")) {
      conformingValue = value.substring(1);
    }
    return Integer.valueOf(conformingValue).intValue();
  }

  /** Tests if the port specification is relative.
   *
   * @param port The port specification to test.
   *
   * @return Returns true if the port specification is relative.
   */
  private boolean isPortRelative(final String port) {
    if (port == null) {
      return false;
    }
    return port.startsWith("-") || port.startsWith("+");
  }

  /** Creates a new url based on a base url and a path fragment.
   *
   * @param base The base url. It cannot be null.
   *
   * @param path The path fragment. It cannot be null.
   *
   * @return Returns the new url formed by the concatenation of the base url
   * and the path fragment, including the '/' if necessary.
   */
  private String createUrl(final String base, final String path) {
    Validate.notNull(base, "The base url cannot be null");
    Validate.notNull(path, "The path fragment cannot be null");
    String result = base;
    if (!result.endsWith("/") && !path.startsWith("/")) {
      result += "/";
    }
    result += path;
    return result;
  }
}

