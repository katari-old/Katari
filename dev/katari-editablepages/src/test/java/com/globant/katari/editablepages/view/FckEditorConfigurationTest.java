
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/* Tests the page controller.
 */
public class FckEditorConfigurationTest {

  @Test
  public void testEditorAreaCss_null() {
    FckEditorConfiguration configuration = new FckEditorConfiguration();
    configuration.setEditorAreaCss(null);
    assertThat(configuration.getEditorAreaCss(), nullValue());
  }

  @Test
  public void testEditorAreaCss_slash() {
    FckEditorConfiguration configuration = new FckEditorConfiguration();
    configuration.setEditorAreaCss("/something");
    assertThat(configuration.getEditorAreaCss(), is("/something"));
  }

  @Test
  public void testEditorAreaCss_noSlash() {
    FckEditorConfiguration configuration = new FckEditorConfiguration();
    configuration.setEditorAreaCss("something");
    assertThat(configuration.getEditorAreaCss(), is("/something"));
  }
}

