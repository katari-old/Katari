/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import java.io.File;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

import com.globant.katari.shindig.domain.Application;

/** Test for the bean {@link GadgetGroupTemplate}
 */
public class GadgetGroupTemplateTest {

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
    GadgetGroupTemplate group = new GadgetGroupTemplate("groupName", "default", 1);
    assertTrue(group.getId() == 0);
    assertTrue(group.getName().equals("groupName"));
    assertThat(group.getView(), is("default"));
  }

  @Test
  public void testConstructorWithNullPageName() {
    try {
      new GadgetGroupTemplate(null, "default", 1);
      fail("Should be an illegal argument exception because pagename is null");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testConstructorWithEmptyPageName() {
    try {
      new GadgetGroupTemplate("", "default", 1);
      fail("Should be an illegal argument exception because pagename is empty");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_emptyView() {
    new GadgetGroupTemplate("something", "", 1);
  }

  @Test
  public void testCreateFromTemplate() {
    // Create a group with tree gadget instances.
    GadgetGroupTemplate group;
    group = new GadgetGroupTemplate("main group", "default", 3);
    // The template group must not have an owner.
    group.add(new GadgetInstance(application, 0, 0));
    group.add(new GadgetInstance(application, 0, 1));
    group.add(new GadgetInstance(application, 0, 3));

    CustomizableGadgetGroup newGroup = group.createFromTemplate(user);

    assertThat(newGroup.getOwner(), is(user));
    assertThat(newGroup.getName(), is("main group"));
    assertThat(newGroup.getView(), is("default"));
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
    GadgetGroupTemplate group;
    group = new GadgetGroupTemplate("main group", "default", 3);
    assertThat(group.contains(application), is(false));
  }

  @Test
  public void testContains_true() {
    // Create a group with one gadget instance.
    GadgetGroupTemplate group;
    group = new GadgetGroupTemplate("main group", "default", 3);
    group.add(new GadgetInstance(application, 0, 0));
    assertThat(group.contains(application), is(true));
  }
}

