/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

/**
 * Test for the bean {@link GadgetGroup}
 * 
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 * 
 */
public class GadgetGroupTest {

  @Test
  public void testConstructorOk() {
    GadgetGroup cp = new GadgetGroup("oneUser", "pageName");
    assertTrue(cp.getOwner().equals("oneUser"));
    assertTrue(cp.getId() == 0);
    assertTrue(cp.getName().equals("pageName"));
  }

  @Test
  public void testConstructorWithNullPageName() {
    try {
      new GadgetGroup("user", null);
      fail("Should be an illegal argument exception because pagename is null");
    } catch (IllegalArgumentException e) {
    }
  }
  
  @Test
  public void testConstructorWithEmptyPageName() {
    try {
      new GadgetGroup("user", "");
      fail("Should be an illegal argument exception because pagename is empty");
    } catch (IllegalArgumentException e) {
    }
  }
  
  @Test
  public void testAddCanvasInstance() {
    GadgetGroup page = new GadgetGroup("1", "1");
    GadgetInstance instance = new GadgetInstance("a", "1");
    page.addGadget(instance);
    assertTrue(page.getGadgets().contains(instance));
  }
}

