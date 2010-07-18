/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.UUID;

import org.junit.Test;

import com.globant.katari.shindig.domain.Application;

/**
 * Test for the bean  {@link GadgetInstance}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class GadgetInstanceTest {

  private Application app = new Application("url");

  @Test
  public void testConstructorOk() {
    GadgetInstance gi = new GadgetInstance(app, "position");
    assertTrue(gi.getUrl().equals("url"));
    assertTrue(gi.getGadgetPosition().equals("position"));
    assertTrue(gi.getId() == 0);
  }

  @Test
  public void testConstructorWithNullGadgetUrl() {
    try {
      new GadgetInstance(null, "1");
      fail("Should be an illegal argument exception because gadget url is null");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testConstructorWithNullPosition() {
    try {
      new GadgetInstance(app, null);
      fail("Should be an illegal argument exception because position is null");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testConstructorWithEmptyPosition() {
    try {
      new GadgetInstance(app, "");
      fail("Should be an illegal argument exception because position is empty");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testSetSecurityToken() {
    GadgetInstance gi = new GadgetInstance(app, "1");
    try {
      gi.associateToViewer("", 1);
      fail("Should be an illegal argument exception because securityToken is empty");
    } catch (IllegalArgumentException e) {
    }
    try {
      gi.associateToViewer(null, 1);
      fail("Should be an illegal argument exception because securityToken is null");
    } catch (IllegalArgumentException e) {
    }

    String st = "theToken";
    gi.associateToViewer(st, 1);

    assertTrue(gi.getSecurityToken().equals(st));
  }

  @Test
  public void testSetViewer() {
    GadgetInstance gi = new GadgetInstance(app, "1");
    try {
      gi.associateToViewer("token",0);
      fail("Should be an illegal argument exception because viewer is empty");
    } catch (IllegalArgumentException e) {
    }

    gi.associateToViewer("token", 1);

    assertThat(gi.getViewer(), is(1l));
  }

  @Test
  public void testChangePosition() {
    GadgetInstance gi = new GadgetInstance(app, "1");
    try {
      gi.move("");
      fail("Should be an illegal argument exception because position is empty");
    } catch (IllegalArgumentException e) {
    }
    try {
      gi.move(null);
      fail("Should be an illegal argument exception because position is null");
    } catch (IllegalArgumentException e) {
    }
    String position = UUID.randomUUID().toString();
    gi.move(position);
    assertTrue(gi.getGadgetPosition().equals(position));
  }
}

