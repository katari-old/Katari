package com.globant.katari.core.web;

import junit.framework.TestCase;

public class ModuleUtilsTest extends TestCase {

  private static String url = "/module/module-name/following/something";
  private static String url2 = "/unknown/";

  public void testGetModuleNameFromUrl() {

    assertEquals("module-name", ModuleUtils.getModuleNameFromUrl(url));
  }

  public void testStripModuleNameFromUrl() {
    assertEquals("/following/something", ModuleUtils
        .stripModuleNameFromUrl(url));
  }

  public void testStripModuleNameFromUrl2() {
    try {
      ModuleUtils.stripModuleNameFromUrl(url2);
      fail();
    } catch (IllegalArgumentException iae) {
    }
  }

  public void testGetModuleNameFromUrl2() {
    assertEquals(null, ModuleUtils.getModuleNameFromUrl(url2));
  }

  public void testGetModuleNameFromNullUrl() {
    try {
      ModuleUtils.getModuleNameFromUrl(null);
      fail();
    } catch (IllegalArgumentException iae) {
    }
  }

  public void testGetModuleNameFromEmptyUrl() {
    assertNull(ModuleUtils.getModuleNameFromUrl(""));
  }

  public void testStripModuleNameFromNullUrl() {
    try {
      ModuleUtils.stripModuleNameFromUrl(null);
      fail();
    } catch (IllegalArgumentException iae) {
    }
  }

  public void testStripModuleNameFromEmptyUrl() {
    try {
      ModuleUtils.stripModuleNameFromUrl("");
      fail();
    } catch (IllegalArgumentException iae) {
    }
  }

  public void testGetGlobalContextPath() throws Exception {
    String global = ModuleUtils
        .getGlobalContextPath("/katari-sample/module/report/");
    assertEquals("/katari-sample", global);
    try {
      ModuleUtils.getGlobalContextPath("/katari-sample/no-module/report/");
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  public void testModuleNameFromBeanName() {
    assertEquals("user", ModuleUtils.getModuleNameFromBeanName("user"));
    assertEquals("user", ModuleUtils.getModuleNameFromBeanName("user.module"));
  }

}
