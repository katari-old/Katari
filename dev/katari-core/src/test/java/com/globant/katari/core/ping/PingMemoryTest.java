/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.ping;

import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.*;
import static org.junit.Assert.assertThat;

public class PingMemoryTest {
  @Test
  public void testPing() throws Exception {
    PingService service = new PingMemory();
    PingResult result = service.ping();
    assertThat(result.getMessage(), containsString("Heap Size = "));
  }
}

