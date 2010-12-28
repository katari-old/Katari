/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

import java.io.File;

public class ApplicationTest {

  private String fullGadgetXmlUrl = "file:///" + new File(
      "src/test/resources/FullSampleGadget.xml").getAbsolutePath();

  private String incompleteGadgetXmlUrl = "file:///" + new File(
      "src/test/resources/IncompleteSampleGadget.xml").getAbsolutePath();

  private String noViewsGadgetXmlUrl = "file:///" + new File(
      "src/test/resources/OtherSampleGadget.xml").getAbsolutePath();

  private String twoViewsGadgetXmlUrl = "file:///" + new File(
      "src/test/resources/NewsFeedSampleGadget.xml").getAbsolutePath();

  @Test
  public void testConstructor_full() {
    Application app = new Application(fullGadgetXmlUrl);
    assertThat(app.getUrl(), is(fullGadgetXmlUrl));
    assertThat(app.getTitle(), is("Test title"));
    // getDescription returns an empty string instead of null because the xpath
    // parser implementation does not return null when the xpath element is not
    // found.
    assertThat(app.getDescription(), is("Test description"));
    assertThat(app.getId(), is(0L));
    // The next three matchers makes sure that the list contains canvas and
    // default, independently of the order.
    assertThat(app.isViewSupported("canvas"), is(true));
    assertThat(app.isViewSupported("default"), is(true));
    assertThat(app.isViewSupported("not declared"), is(true));
  }

  @Test
  public void testConstructor_incomplete() {
    Application app = new Application(incompleteGadgetXmlUrl);
    assertThat(app.getUrl(), is(incompleteGadgetXmlUrl));
    assertThat(app.getTitle(), is("Test title"));
    // getDescription returns an empty string instead of null because the xpath
    // parser implementation does not return null when the xpath element is not
    // found.
    assertThat(app.getDescription(), is(""));
    assertThat(app.getId(), is(0L));
    assertThat(app.isViewSupported("default"), is(true));
  }

  @Test
  public void testConstructor_noViews() {
    Application app = new Application(noViewsGadgetXmlUrl);
    assertThat(app.getUrl(), is(noViewsGadgetXmlUrl));
    assertThat(app.getTitle(), is("Test title"));
    // getDescription returns an empty string instead of null because the xpath
    // parser implementation does not return null when the xpath element is not
    // found.
    assertThat(app.getDescription(), is(""));
    assertThat(app.getId(), is(0L));
    assertThat(app.isViewSupported("default"), is(false));
  }

  @Test
  public void testConstructor_twoViewsWithComma() {
    Application app = new Application(twoViewsGadgetXmlUrl);
    assertThat(app.getUrl(), is(twoViewsGadgetXmlUrl));
    assertThat(app.getTitle(), is("News Feed"));
    // getDescription returns an empty string instead of null because the xpath
    // parser implementation does not return null when the xpath element is not
    // found.
    assertThat(app.getDescription(), is(""));
    assertThat(app.getId(), is(0L));
    assertThat(app.isViewSupported("default"), is(false));
    assertThat(app.isViewSupported("profile"), is(true));
    assertThat(app.isViewSupported("canvas"), is(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_nullGadgetUrl() {
    new Application(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_emptyGadgetUrl() {
    new Application("");
  }
}

