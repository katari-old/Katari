/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

/**
 * Test for the bean {@link GadgetGroup}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class GadgetGroupTest {

  private CoreUser user;

  @Before
  public void setUp() {
    user = createMock(CoreUser.class);
    replay(user);
  }

  @Test
  public void testConstructorOk() {
    GadgetGroup cp = new GadgetGroup(user, "groupName");
    assertThat(cp.getOwner(), is(user));
    assertTrue(cp.getId() == 0);
    assertTrue(cp.getName().equals("groupName"));
  }

  @Test
  public void testConstructorWithNullPageName() {
    try {
      new GadgetGroup(user, null);
      fail("Should be an illegal argument exception because pagename is null");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testConstructorWithEmptyPageName() {
    try {
      new GadgetGroup(user, "");
      fail("Should be an illegal argument exception because pagename is empty");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testAddCanvasInstance() {
    GadgetGroup page = new GadgetGroup(user, "1");
    GadgetInstance instance = new GadgetInstance("a", "1");
    page.addGadget(instance);
    assertTrue(page.getGadgets().contains(instance));
  }
}

