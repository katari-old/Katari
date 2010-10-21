package com.globant.katari.core.security;

import java.util.Iterator;

import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.intercept.web.AbstractFilterInvocationDefinitionSource;
import org.acegisecurity.SecurityConfig;
import org.acegisecurity.ConfigAttribute;
import org.apache.commons.lang.Validate;

import com.globant.katari.core.web.ModuleContext;
import com.globant.katari.core.web.ModuleContextRegistrar;
import com.globant.katari.core.web.ModuleUtils;

/**
 * This class extends {@link AbstractFilterInvocationDefinitionSource} in order
 * to have a different way to look for attributes, since we do not have just
 * one definition source (url's matching roles), but each module has its own
 * source.
 * @author maximiliano.roman
 * @author ulises.bocchio
 */
public class ModuleFilterInvocationDefinitionSource extends
    AbstractFilterInvocationDefinitionSource {

  /**This is the module context registrar.
   *
   * It cannot be null.
   */
  private ModuleContextRegistrar moduleContextRegistrar;

  /**
   * The default UrlToRoleMapper. It is use in case no module name is found
   * in the url given to the lookupAttributes() method.
   * It cannot be null.
   */
  private UrlToRoleMapper defaultMapper;

  /**Constructor.
   *
   * @param theModuleContextRegistrar
   *          the ModuleContextRegistrar.
   *          It is used to get the differents Module Context to get the
   *          <code>UrlToRoleMapper</code> of each module to perform security.
   *          It cannot be null.
   * @param theDefaultMapper The default UrlToRoleMapper used in case no module
   *  name is found in the url given to the lookupAttributes() method.
   */
  public ModuleFilterInvocationDefinitionSource(
      final ModuleContextRegistrar theModuleContextRegistrar,
      final UrlToRoleMapper theDefaultMapper) {
    Validate.notNull(theModuleContextRegistrar, "The "
        + "ModuleContextRegistrar cannot be null");
    Validate.notNull(theDefaultMapper, "The default UrlToRoleMapper cannot"
        + "be null");
    moduleContextRegistrar = theModuleContextRegistrar;
    defaultMapper = theDefaultMapper;
  }

  /** It returns the ConfigAttributeDefinition for the specified url.
   *
   * If the request corresponds to a module (mapped to [ctx]/module), it asks
   * the module for ConfigAttributeDefinition. Otherwise, it uses a global
   * mapper. If no ConfigAttributeDefinition is found, it throws an exception.
   *
   * @param url the url to look for the <code>ConfigAttributeDefinition</code>.
   * It cannot be null.
   *
   * @return the config attribute definition for the given url. It returns null
   * if the url is public.
   */
  @Override
  public ConfigAttributeDefinition lookupAttributes(final String url) {

    Validate.notNull(url, "The url cannot be null");
    String moduleName = ModuleUtils.getModuleNameFromUrl(url);

    if (moduleName != null) {
      UrlToRoleMapper moduleMapper;
      ModuleContext moduleContext;
      moduleContext = moduleContextRegistrar.getModuleContext(moduleName);
      if (moduleContext == null) {
        throw new IllegalArgumentException("The module name: " + moduleName
          + " extracted from the url doesn't match any module");
      }
      moduleMapper = moduleContext.getUrlToRoleMapper();

      String strippedUrl = ModuleUtils.stripModuleNameFromUrl(url);
      String[] roles = moduleMapper.getRolesForUrl(strippedUrl);
      return buildConfigAttributeDefinition(roles);
    }

    String[] roles = defaultMapper.getRolesForUrl(url);
    return buildConfigAttributeDefinition(roles);
  }

  /** It returns null, there's no need to implement this method it is optional.
   * @return it returns null
   */
  @SuppressWarnings("unchecked")
  public Iterator getConfigAttributeDefinitions() {
    return null;
  }

  /** Builds a <code>ConfigAttributeDefinition</code> from the array of
   * roles.
   *
   * The config attribute definition is built from different
   * <code>ConfigAttribute</code>.
   *
   * @param roles The roles to build the ConfigAttributeDefinition for.  It
   * cannot be null.
   *
   * @return ConfigAttributeDefinition. It returns null if an empty array is
   * given.
   */
  private static ConfigAttributeDefinition buildConfigAttributeDefinition(
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

