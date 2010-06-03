package com.globant.katari.core.web;

import junit.framework.TestCase;

/* Test case for the menu bar component
 */
public class MenuBarTest extends TestCase {
  public void testConstructor() {
    MenuBar menuBar = new MenuBar("root", "root");

    assertNull(menuBar.getParent());
    assertNull(menuBar.getToolTip());
  }
}
