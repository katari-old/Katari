/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.security;

import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.SecurityConfig;
import org.apache.commons.lang.Validate;

/** This class contain common methods used for modules that implements security
 * based on acegi security.
 *
 * @author ulises.bocchio
 */
public final class SecurityUtils {

  /** Constructor.
   *
   * The default private constructor for an Utility Class.
   */
  private SecurityUtils() {
  }

  /** It builds a <code>ConfigAttributeDefinition</code> from the array of
   * roles.
   *
   * The config attribute definition is built of differents
   * <code>ConfigAttribute</code>.
   *
   * @param roles The roles to build the ConfigAttributeDefinition for.  It
   * cannot be null.
   *
   * @return ConfigAttributeDefinition. It returns null if an empty array is
   * given.
   */
  public static ConfigAttributeDefinition buildConfigAttributeDefinition(
      final String[] roles) {
    Validate.notNull(roles, "The roles array cannot be null");
    if (roles.length == 0) {
      return null;
    }
    ConfigAttributeDefinition configAttributeDefinition
      = new ConfigAttributeDefinition();
    for (String currentRole : roles) {
      if (currentRole == null || "".equals(currentRole.trim())) {
        throw new IllegalArgumentException("The Roles array contains an empty"
            + " role value");
      }
      ConfigAttribute configAttribute = new SecurityConfig(currentRole.trim());
      configAttributeDefinition.addConfigAttribute(configAttribute);
    }
    return configAttributeDefinition;
  }
}

