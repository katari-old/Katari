/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import junit.framework.TestCase;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;

public class OverridingMapFactoryBeanTest extends TestCase {

  private Map<String, String> source = new LinkedHashMap<String, String>();
  private Map<String, String> override = new LinkedHashMap<String, String>();

  public void setUp() {
    source.put("1", "source-1");
    source.put("2", "source-2");

    override.put("3", "override-3");
  }

  public void testGetInstance_source() {
    OverridingMapFactoryBean factory = new OverridingMapFactoryBean();
    factory.setSourceMap(source);
    Map<?,?> result = (Map<?,?>) factory.createInstance();

    assertEquals(2, result.size());
    Iterator<?> it = result.keySet().iterator();
    assertEquals("1", (String) it.next());
    assertEquals("2", (String) it.next());
    assertEquals("source-1", result.get("1"));
    assertEquals("source-2", result.get("2"));
  }

  public void testGetInstance_sourcePlusKeys() {
    OverridingMapFactoryBean factory = new OverridingMapFactoryBean();
    factory.setSourceMap(source);
    factory.setOverridingMap(override);
    Map<?,?> result = (Map<?,?>) factory.createInstance();

    assertEquals(3, result.size());
    Iterator<?> it = result.keySet().iterator();
    assertEquals("1", (String) it.next());
    assertEquals("2", (String) it.next());
    assertEquals("3", (String) it.next());
    assertEquals("source-1", result.get("1"));
    assertEquals("source-2", result.get("2"));
    assertEquals("override-3", result.get("3"));
  }

  public void testGetInstance_modifyKeys() {
    OverridingMapFactoryBean factory = new OverridingMapFactoryBean();
    factory.setSourceMap(source);
    override.put("1", "override-1");
    factory.setOverridingMap(override);
    Map<?,?> result = (Map<?,?>) factory.createInstance();

    assertEquals(3, result.size());
    Iterator<?> it = result.keySet().iterator();
    assertEquals("1", (String) it.next());
    assertEquals("2", (String) it.next());
    assertEquals("3", (String) it.next());
    assertEquals("override-1", result.get("1"));
    assertEquals("source-2", result.get("2"));
    assertEquals("override-3", result.get("3"));
  }
}

