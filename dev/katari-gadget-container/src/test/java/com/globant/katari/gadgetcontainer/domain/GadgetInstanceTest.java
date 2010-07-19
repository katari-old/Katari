/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.globant.katari.shindig.domain.Application;

public class GadgetInstanceTest {

  @Test
  public void testConstructor_ok() {
    Application app = new Application("url");
    GadgetInstance gi = new GadgetInstance(app, 1, 1);
    assertThat(gi.getUrl(), is("url"));
    assertThat(gi.getColumn(), is(1));
    assertThat(gi.getOrder(), is(1));
    assertThat(gi.getId(), is(0L));
  }

  @Test
  public void testConstructor_nullUrl() {
    try {
      new GadgetInstance(null, 0, 0);
      fail("Should be an illegal argument exception because gadget url is null");
    } catch (IllegalArgumentException e) {
    }
  }
}

