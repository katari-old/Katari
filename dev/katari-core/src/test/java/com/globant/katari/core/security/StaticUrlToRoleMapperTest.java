package com.globant.katari.core.security;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

public class StaticUrlToRoleMapperTest extends TestCase {

  private StaticUrlToRoleMapper mapper = null;

  protected void setUp() {
    Map<String, String[]> urlsToRoles = new LinkedHashMap<String, String[]>();
    String[] twoRoles = new String[] {"ROLE_ADMINISTRATOR",
        "ROLE_REPORT_ADMIN" };
    String[] oneRole = new String[] {"IS_AUTHENTICATED_FULLY"};
    urlsToRoles.put("/edit.do", twoRoles);
    urlsToRoles.put("/**/*", oneRole);

    mapper = new StaticUrlToRoleMapper(urlsToRoles);
  }

  public void testGetRolesForUrl_allSimple() {
    assertEquals(1, mapper.getRolesForUrl("/reports.do").length);
  }

  public void testGetRolesForUrl_allWithParams() {
    assertEquals(1, mapper.getRolesForUrl("/reports.do?p1=10&p2=20").length);
  }

  public void testGetRolesForUrl_allWithParamsAndSlash() {
    assertEquals(1, mapper.getRolesForUrl("/reports.do?param=/root/x").length);
  }

  public void testGetRolesForUrl_simple() {
    assertEquals(2, mapper.getRolesForUrl("/edit.do").length);
  }

  public void testGetRolesForUrl_withParams() {
    assertEquals(2, mapper.getRolesForUrl("/edit.do?p1=10&p2=20").length);
  }

  public void testGetRolesForUrl_withParamsAndSlash() {
    assertEquals(2, mapper.getRolesForUrl("/edit.do?param=/root/x").length);
  }
}
