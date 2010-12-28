/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

/** Test for the bean {@link SharedGadgetGroup}
 */
public class SharedGadgetGroupTest {

  @Test
  public void testConstructor() {
    SharedGadgetGroup group = new SharedGadgetGroup("groupName", "default", 1);
    assertTrue(group.getId() == 0);
    assertTrue(group.getName().equals("groupName"));
    assertThat(group.getView(), is("default"));
  }

  @Test
  public void testConstructor_nullName() {
    try {
      new SharedGadgetGroup(null, "default", 1);
      fail("Should be an illegal argument exception because pagename is null");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testConstructor_emptyName() {
    try {
      new SharedGadgetGroup("", "default", 1);
      fail("Should be an illegal argument exception because pagename is empty");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_emptyView() {
    new SharedGadgetGroup("something", "", 1);
  }
}

