/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.view;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.net.URL;

public class ResourceSetTest {

  @Test
  public void testCreate() throws Exception {
    URL propertiesUrl = new File(
        "./src/test/resources/META-INF/katari-resource-set").toURI().toURL();
    ResourceSet resourceSet = new ResourceSet(propertiesUrl);

    assertThat(resourceSet.getBasePath(),
        is("com/globant/katari/jsmodule/view"));
    assertThat(resourceSet.getDebugPrefix(),
        is("../katari-jsmodule/src/test/resources"));
    assertThat(resourceSet.getMimeTypes().size(), is(2));
    assertThat(resourceSet.getMimeTypes().get("js"), is("text/javascript"));
    assertThat(resourceSet.getMimeTypes().get("png"), is("image/png"));
  }
}

