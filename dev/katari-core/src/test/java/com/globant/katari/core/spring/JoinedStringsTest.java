/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class JoinedStringsTest {

  @Test
  public void testCreateInstance() {
    JoinedStrings string = new JoinedStrings();
    string.setValues(new String[] {"String-1", ",", "String-2"});
    String value = (String) string.createInstance();
    assertThat(value, is("String-1,String-2"));
  }
}

