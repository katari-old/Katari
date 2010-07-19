/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

import com.globant.katari.shindig.domain.Application;

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
    user = createNiceMock(CoreUser.class);
    expect(user.getId()).andReturn(1L);
    replay(user);
  }

  @Test
  public void testConstructorOk() {
    GadgetGroup cp = new GadgetGroup(user, "groupName", 1);
    assertThat(cp.getOwner(), is(user));
    assertTrue(cp.getId() == 0);
    assertTrue(cp.getName().equals("groupName"));
  }

  @Test
  public void testConstructorWithNullPageName() {
    try {
      new GadgetGroup(user, null, 1);
      fail("Should be an illegal argument exception because pagename is null");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testConstructorWithEmptyPageName() {
    try {
      new GadgetGroup(user, "", 1);
      fail("Should be an illegal argument exception because pagename is empty");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testAddGadget() {
    GadgetGroup group = new GadgetGroup(user, "1", 1);
    Application app = new Application("a");
    GadgetInstance instance = new GadgetInstance(app, 0, 0);
    group.addGadget(instance);
    assertTrue(group.getGadgets().contains(instance));
  }

  @Test(expected = RuntimeException.class)
  public void testAddGadget_columnTooLarge() {
    GadgetGroup group = new GadgetGroup(user, "1", 1);
    Application app = new Application("a");
    GadgetInstance instance = new GadgetInstance(app, 1, 0);
    group.addGadget(instance);
  }
}

