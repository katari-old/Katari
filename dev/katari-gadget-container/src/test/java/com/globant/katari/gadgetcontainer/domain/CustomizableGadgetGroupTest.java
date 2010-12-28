/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;

import java.io.File;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

import com.globant.katari.shindig.domain.Application;

/** Test for the bean {@link CustomizableGadgetGroup}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class CustomizableGadgetGroupTest {

  private String gadgetXmlUrl = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String gadgetProfileXmlUrl = "file:///" + new File(
      "target/test-classes/SampleProfileGadget.xml").getAbsolutePath();

  private CoreUser user;

  private Application application;
  private Application applicationProfile;

  @Before
  public void setUp() {
    user = createNiceMock(CoreUser.class);
    expect(user.getId()).andReturn(1L);
    replay(user);

    application = new Application(gadgetXmlUrl);
    applicationProfile = new Application(gadgetProfileXmlUrl);
  }

  @Test
  public void testConstructorOk() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "groupName", "default", 1);
    assertThat(group.getOwner(), is(user));
    assertTrue(group.getId() == 0);
    assertTrue(group.getName().equals("groupName"));
    assertThat(group.getView(), is("default"));
  }

  @Test
  public void testConstructorWithNullPageName() {
    try {
      new CustomizableGadgetGroup(user, null, "default", 1);
      fail("Should be an illegal argument exception because pagename is null");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testConstructorWithEmptyPageName() {
    try {
      new CustomizableGadgetGroup(user, "", "default", 1);
      fail("Should be an illegal argument exception because pagename is empty");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_emptyView() {
    new CustomizableGadgetGroup(user, "something", "", 1);
  }

  @Test
  public void testAddGadget() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "1", "default", 1);
    GadgetInstance instance = new GadgetInstance(application, 0, 0);
    group.add(instance);
    assertTrue(group.getGadgets().contains(instance));
  }

  @Test(expected = RuntimeException.class)
  public void testAddGadget_columnTooLarge() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "1", "default", 1);
    GadgetInstance instance = new GadgetInstance(application, 1, 0);
    group.add(instance);
  }

  @Test(expected = RuntimeException.class)
  public void testAddGadget_viewNotSupported() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "1", "canvas", 1);
    GadgetInstance instance = new GadgetInstance(applicationProfile, 0, 0);
    group.add(instance);
  }

  @Test
  public void testAddGadget_defaultGadgetView() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "1", "canvas", 1);
    GadgetInstance instance = new GadgetInstance(application, 0, 0);
    group.add(instance);
    assertTrue(group.getGadgets().contains(instance));
  }

  @Test
  public void move_pastLastColumn() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "name", "default", 1);
    GadgetInstance instance;

    instance = new GadgetInstance(application, 0, 0);
    new DirectFieldAccessor(instance).setPropertyValue("id", 1);
    group.add(instance);

    instance = new GadgetInstance(application, 0, 1);
    new DirectFieldAccessor(instance).setPropertyValue("id", 2);
    group.add(instance);

    // Should fail because there is only 1 column in the group.
    try {
      group.move(2, 1, 0);
      fail("Moved passed the last column should have failed.");
    } catch (RuntimeException e) {
    }
  }

  @Test
  public void move_wrongGadget() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "name", "default", 1);
    GadgetInstance instance;

    instance = new GadgetInstance(application, 0, 0);
    new DirectFieldAccessor(instance).setPropertyValue("id", 1);
    group.add(instance);

    instance = new GadgetInstance(application, 0, 1);
    new DirectFieldAccessor(instance).setPropertyValue("id", 2);
    group.add(instance);

    // Should fail because gadget 100 is not in the group.
    try {
      group.move(100, 0, 0);
      fail("Moved passed the last column should have failed.");
    } catch (RuntimeException e) {
    }
  }

  @Test
  public void move_sameColumn() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "name", "default", 1);

    GadgetInstance col0Order0 = new GadgetInstance(application, 0, 0);
    new DirectFieldAccessor(col0Order0).setPropertyValue("id", 1);
    group.add(col0Order0);

    GadgetInstance col0Order1 = new GadgetInstance(application, 0, 1);
    new DirectFieldAccessor(col0Order1).setPropertyValue("id", 2);
    group.add(col0Order1);

    // Move the second gadget to the begining of the column.
    group.move(2, 0, 0);
    assertThat(col0Order0.getColumn(), is(0));
    assertThat(col0Order0.getOrder(), is(1));
    assertThat(col0Order1.getColumn(), is(0));
    assertThat(col0Order1.getOrder(), is(0));
  }

  @Test
  public void move_toEmptyColumn() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "name", "default", 2);

    GadgetInstance col0Order0 = new GadgetInstance(application, 0, 0);
    new DirectFieldAccessor(col0Order0).setPropertyValue("id", 1);
    group.add(col0Order0);

    GadgetInstance col0Order1 = new GadgetInstance(application, 0, 1);
    new DirectFieldAccessor(col0Order1).setPropertyValue("id", 2);
    group.add(col0Order1);

    // Move the second gadget to the begining of the column.
    group.move(2, 1, 0);
    assertThat(col0Order0.getColumn(), is(0));
    assertThat(col0Order0.getOrder(), is(0));
    assertThat(col0Order1.getColumn(), is(1));
    assertThat(col0Order1.getOrder(), is(0));
  }

  @Test
  public void move_toLargeColumn() {
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "name", "default", 2);

    GadgetInstance col0Order0 = new GadgetInstance(application, 0, 0);
    new DirectFieldAccessor(col0Order0).setPropertyValue("id", 1);
    group.add(col0Order0);

    GadgetInstance col0Order1 = new GadgetInstance(application, 0, 1);
    new DirectFieldAccessor(col0Order1).setPropertyValue("id", 2);
    group.add(col0Order1);

    GadgetInstance col1Order0 = new GadgetInstance(application, 1, 1);
    new DirectFieldAccessor(col1Order0).setPropertyValue("id", 3);
    group.add(col1Order0);

    GadgetInstance col1Order1 = new GadgetInstance(application, 1, 2);
    new DirectFieldAccessor(col1Order1).setPropertyValue("id", 4);
    group.add(col1Order1);

    GadgetInstance col1Order2 = new GadgetInstance(application, 1, 3);
    new DirectFieldAccessor(col1Order2).setPropertyValue("id", 5);
    group.add(col1Order2);

    // Move the second gadget to the begining of the column.
    group.move(2, 1, 1);
    assertThat(col0Order0.getColumn(), is(0));
    assertThat(col0Order0.getOrder(), is(0));
    assertThat(col1Order0.getColumn(), is(1));
    assertThat(col1Order0.getOrder(), is(0));
    assertThat(col0Order1.getColumn(), is(1));
    assertThat(col0Order1.getOrder(), is(1));
    assertThat(col1Order1.getColumn(), is(1));
    assertThat(col1Order1.getOrder(), is(2));
    assertThat(col1Order2.getColumn(), is(1));
    assertThat(col1Order2.getOrder(), is(3));
  }

  @Test
  public void testRemove() {
    // Create a group with one gadget instance.
    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "main group", "default", 3);
    group.add(new GadgetInstance(application, 0, 0));
    assertThat(group.getGadgets().size(), is(1));
    group.remove(0);
    assertThat(group.getGadgets().size(), is(0));
  }
}

