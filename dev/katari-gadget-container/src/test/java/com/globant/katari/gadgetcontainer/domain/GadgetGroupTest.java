/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import java.io.File;

import com.globant.katari.tools.ReflectionUtils;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

import com.globant.katari.shindig.domain.Application;

/** Test for the bean {@link GadgetGroup}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class GadgetGroupTest {

  private String gadgetXmlUrl = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private CoreUser user;

  private Application application;

  @Before
  public void setUp() {
    user = createNiceMock(CoreUser.class);
    expect(user.getId()).andReturn(1L);
    replay(user);

    application = new Application(gadgetXmlUrl);
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
    GadgetInstance instance = new GadgetInstance(application, 0, 0);
    group.add(instance);
    assertTrue(group.getGadgets().contains(instance));
  }

  @Test(expected = RuntimeException.class)
  public void testAddGadget_columnTooLarge() {
    GadgetGroup group = new GadgetGroup(user, "1", 1);
    GadgetInstance instance = new GadgetInstance(application, 1, 0);
    group.add(instance);
  }

  @Test
  public void move_pastLastColumn() {
    GadgetGroup group = new GadgetGroup(user, "name", 1);
    GadgetInstance instance;
     
    instance = new GadgetInstance(application, 0, 0);
    ReflectionUtils.setAttribute(instance, "id", 1);
    group.add(instance);

    instance = new GadgetInstance(application, 0, 1);
    ReflectionUtils.setAttribute(instance, "id", 2);
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
    GadgetGroup group = new GadgetGroup(user, "name", 1);
    GadgetInstance instance;
     
    instance = new GadgetInstance(application, 0, 0);
    ReflectionUtils.setAttribute(instance, "id", 1);
    group.add(instance);

    instance = new GadgetInstance(application, 0, 1);
    ReflectionUtils.setAttribute(instance, "id", 2);
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
    GadgetGroup group = new GadgetGroup(user, "name", 1);
     
    GadgetInstance col0Order0 = new GadgetInstance(application, 0, 0);
    ReflectionUtils.setAttribute(col0Order0, "id", 1);
    group.add(col0Order0);

    GadgetInstance col0Order1 = new GadgetInstance(application, 0, 1);
    ReflectionUtils.setAttribute(col0Order1, "id", 2);
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
    GadgetGroup group = new GadgetGroup(user, "name", 2);
     
    GadgetInstance col0Order0 = new GadgetInstance(application, 0, 0);
    ReflectionUtils.setAttribute(col0Order0, "id", 1);
    group.add(col0Order0);

    GadgetInstance col0Order1 = new GadgetInstance(application, 0, 1);
    ReflectionUtils.setAttribute(col0Order1, "id", 2);
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
    GadgetGroup group = new GadgetGroup(user, "name", 2);
     
    GadgetInstance col0Order0 = new GadgetInstance(application, 0, 0);
    ReflectionUtils.setAttribute(col0Order0, "id", 1);
    group.add(col0Order0);

    GadgetInstance col0Order1 = new GadgetInstance(application, 0, 1);
    ReflectionUtils.setAttribute(col0Order1, "id", 2);
    group.add(col0Order1);

    GadgetInstance col1Order0 = new GadgetInstance(application, 1, 1);
    ReflectionUtils.setAttribute(col1Order0, "id", 3);
    group.add(col1Order0);

    GadgetInstance col1Order1 = new GadgetInstance(application, 1, 2);
    ReflectionUtils.setAttribute(col1Order1, "id", 4);
    group.add(col1Order1);

    GadgetInstance col1Order2 = new GadgetInstance(application, 1, 3);
    ReflectionUtils.setAttribute(col1Order2, "id", 5);
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
  public void testCreateFromTemplate_nonTemplateGroup() {
    GadgetGroup group = new GadgetGroup(null, "shared group", 3);
    try {
      group.createFromTemplate(user);
      fail("Trying to create a group from a non template.");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testCreateFromTemplate() {
    // Create a group with tree gadget instances.
    GadgetGroup group = new GadgetGroup("main group", 3);
    // The template group must not have an owner.
    assertThat(group.getOwner(), nullValue());
    group.add(new GadgetInstance(application, 0, 0));
    group.add(new GadgetInstance(application, 0, 1));
    group.add(new GadgetInstance(application, 0, 3));

    GadgetGroup newGroup = group.createFromTemplate(user);

    assertThat(newGroup.getOwner(), is(user));
    assertThat(newGroup.getName(), is("main group"));
    assertThat(newGroup.getNumberOfColumns(), is(3));

    Set<GadgetInstance> instances = newGroup.getGadgets();

    assertThat(instances, notNullValue());
    assertThat(instances.size(), is(3));
    // We just check one gadget ...
    assertThat(instances.iterator().next().getTitle(), is("Test title"));
  }

  @Test
  public void testContains_false() {
    // Create a group with one gadget instance.
    GadgetGroup group = new GadgetGroup("main group", 3);
    assertThat(group.contains(application), is(false));
  }

  @Test
  public void testContains_true() {
    // Create a group with one gadget instance.
    GadgetGroup group = new GadgetGroup("main group", 3);
    group.add(new GadgetInstance(application, 0, 0));
    assertThat(group.contains(application), is(true));
  }
}

