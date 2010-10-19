/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.ping;

import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.*;
import static org.junit.Assert.assertThat;

public class PingConfigurationTest {
  @Test
  public void testPing_allDisabled() throws Exception {
    PingService service = new PingConfiguration(false, false);
    PingResult result = service.ping();
    assertThat(result.getMessage(), containsString("Debug mode is off"));
    assertThat(result.getMessage(), containsString(
          "Html validation is disabled"));
  }

  @Test
  public void testPing_allEnabled() throws Exception {
    PingService service = new PingConfiguration(true, true);
    PingResult result = service.ping();
    assertThat(result.getMessage(), containsString("Debug mode is on"));
    assertThat(result.getMessage(), containsString(
          "Html validation is enabled"));
  }
}

