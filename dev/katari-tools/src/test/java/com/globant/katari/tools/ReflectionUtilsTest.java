/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import org.junit.Test;
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

  @Test
  public void testGetAttribute() {
    C c = new C();
    assertEquals("old", ReflectionUtils.getAttribute(c, "s"));
  }

  @Test
  public void testSetAttribute() {
    C c = new C();
    ReflectionUtils.setAttribute(c, "s", "new");
    assertEquals("new", c.getS());
  }

}

