package com.globant.katari.core.security;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.easymock.classextension.EasyMock;

import com.globant.katari.core.web.ConfigurableModule;
import com.globant.katari.core.web.ModuleContext;
import com.globant.katari.core.web.ModuleContextRegistrar;

public class SecureUserModuleIntegrationTest extends TestCase {

  private ModuleFilterInvocationDefinitionSource definitionSource;

  private Map<String, String[]> roles;

  StaticUrlToRoleMapper urlMapper;
  ConfigurableModule delegatingSecureModule;
  ModuleContext moduleContext;
  ModuleContextRegistrar registrar;

  @Override
  protected void setUp() throws Exception {

    urlMapper = new StaticUrlToRoleMapper(buildMockMapRoles());
    delegatingSecureModule = new ConfigurableModule();
    moduleContext = EasyMock.createMock(ModuleContext.class);
    registrar = EasyMock.createMock(ModuleContextRegistrar.class);
    definitionSource = new ModuleFilterInvocationDefinitionSource(
        registrar, urlMapper);

    EasyMock.expect(registrar.getModuleContext("user"))
        .andReturn(moduleContext).anyTimes();
    EasyMock.expect(registrar.getModuleContext("unknown"))
    .andReturn(null).anyTimes();
    EasyMock.expect(registrar.getModuleContext("usero")).andReturn(null)
        .anyTimes();
    EasyMock.expect(moduleContext.getUrlToRoleMapper()).andReturn(urlMapper)
        .anyTimes();
    EasyMock.replay(registrar);
    EasyMock.replay(moduleContext);

  }

  private Map<String, String[]> buildMockMapRoles() {
    roles = new LinkedHashMap<String, String[]>();
    roles.put("/admin.html", new String[] { "ADMIN_ROLE" });
    roles.put("/**", new String[] { "ADMIN_ROLE", "LICHE_ROLE",
        "CESAR_ROLE" });
    return roles;
  }

  @SuppressWarnings("unchecked")
  public void testSecurityFlowCheckingValidURL1() {
    ConfigAttributeDefinition configAttributeDefinition
      = definitionSource.lookupAttributes("/module/user/admin.html");
    assertNotNull(configAttributeDefinition);
    String[] rolesForURL = roles.get("/admin.html");
    ConfigAttribute currentConfigAttribute;
    Iterator<ConfigAttribute> configAttributesIterator
      = configAttributeDefinition.getConfigAttributes();
    int rolesIt = 0;
    while (configAttributesIterator.hasNext()
        && (rolesIt < rolesForURL.length)) {
      currentConfigAttribute = configAttributesIterator.next();
      assertEquals(currentConfigAttribute.getAttribute(), rolesForURL[rolesIt]);
      rolesIt++;
    }
    assertEquals(rolesForURL.length, configAttributeDefinition.size());
  }

  @SuppressWarnings("unchecked")
  public void testSecurityFlowCheckingValidURL2() {
    ConfigAttributeDefinition configAttributeDefinition
      = definitionSource.lookupAttributes(
          "/module/user/page.html?accion=doTest");
    assertNotNull(configAttributeDefinition);
    String[] rolesForURL = roles.get("/**");
    ConfigAttribute currentConfigAttribute;
    Iterator<ConfigAttribute> configAttributesIterator
      = configAttributeDefinition.getConfigAttributes();
    int rolesIt = 0;
    while (configAttributesIterator.hasNext()
        && (rolesIt < rolesForURL.length)) {
      currentConfigAttribute = configAttributesIterator.next();
      assertEquals(currentConfigAttribute.getAttribute(), rolesForURL[rolesIt]);
      rolesIt++;
    }
    assertEquals(rolesForURL.length, configAttributeDefinition.size());
  }

  public void testSecurityFlowCheckingInValidURL1() {
    ConfigAttributeDefinition configAttributeDefinition = definitionSource
        .lookupAttributes("/unknown/");
    assertEquals(3, configAttributeDefinition.size());
  }

  public void testSecurityFlowCheckingInValidURL2() {
    try {
      definitionSource.lookupAttributes("/module/usero/");
      fail("definitionSource.lookupAttributes(\"/module/usero/\")"
          + " should've thrown an exception!");
    } catch (Exception e) {
      assertEquals(IllegalArgumentException.class, e.getClass());
    }
  }
}
