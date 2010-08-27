/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import java.io.File;

import com.globant.katari.shindig.domain.Application;

public class GadgetInstanceTest {

  private String gadgetXmlUrl = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  @Test
  public void testConstructor_ok() {
    Application app = new Application(gadgetXmlUrl);
    GadgetInstance gi = new GadgetInstance(app, 1, 1);
    assertThat(gi.getUrl(), is(gadgetXmlUrl));
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

