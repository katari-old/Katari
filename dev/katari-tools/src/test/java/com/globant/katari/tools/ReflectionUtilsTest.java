/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;

import static org.junit.Assert.*;

/* Test the HibernateUtils.
 */
public class ReflectionUtilsTest {

  /* A sample class with a private attribute.
   */
  private static class C {
    private String s = "old";

    public String getS() {
      return s;
    }
  }

  private static class D extends C {
  }

  @Test
  public void testGetAttribute() {
    C c = new C();
    assertEquals("old", new DirectFieldAccessor(c).getPropertyValue("s"));
  }

  @Test
  public void testSetAttribute() {
    C c = new C();
    new DirectFieldAccessor(c).setPropertyValue("s", "new");
    assertEquals("new", c.getS());
  }

  @Test
  public void testGetAttribute_baseClass() {
    D d = new D();
    assertEquals("old", new DirectFieldAccessor(d).getPropertyValue("s"));
  }

  @Test
  public void testSetAttribute_baseClass() {
    D d = new D();
    new DirectFieldAccessor(d).setPropertyValue("s", "new");
    assertEquals("new", d.getS());
  }
}

