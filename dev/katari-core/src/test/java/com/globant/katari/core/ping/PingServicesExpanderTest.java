/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.ping;

import java.util.List;
import java.util.LinkedList;

import org.junit.Test;
import static org.junit.Assert.*;

public class PingServicesExpanderTest {

  private static class MockPing implements PingService {
    public PingResult ping() {
      return new PingResult(true, "X");
    }
  };

  @Test
  public void testConstructor_first() {
    PingServices pingServices = new PingServices();
    List<PingService> services = new LinkedList<PingService>();
    services.add(new MockPing());
    services.add(new MockPing());
    new PingServicesExpander(pingServices, services);
    List<PingResult> result = pingServices.ping();
    assertEquals(2, result.size());
  }

  @Test
  public void testSetAdditionalPingServices_firstAndSecond() {
    PingServices pingServices = new PingServices();
    List<PingService> first = new LinkedList<PingService>();
    first.add(new MockPing());
    first.add(new MockPing());
    List<PingService> second = new LinkedList<PingService>();
    second.add(new MockPing());
    second.add(new MockPing());
    second.add(new MockPing());
    new PingServicesExpander(pingServices, first);
    new PingServicesExpander(pingServices, second);
    List<PingResult> result = pingServices.ping();
    assertEquals(5, result.size());
  }
}

