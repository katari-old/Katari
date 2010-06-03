/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

public class IpBlacklistTest {

  IpBlacklist blacklist = null;

  @After
  public final void cleanup() {
    if (blacklist != null) {
      blacklist.destroy();
    }
  }

  @Test
  public final void testIsBlacklisted() throws Exception {
    blacklist = new IpBlacklist(100000, true, false);
    assertFalse(blacklist.isBlacklisted("1"));
    blacklist.blacklistIp("1");
    assertTrue(blacklist.isBlacklisted("1"));
  }

  @Test
  public final void testIsBlacklisted_timeout() throws Exception {
    blacklist = new IpBlacklist(1, true, false);
    assertFalse(blacklist.isBlacklisted("1"));
    blacklist.blacklistIp("1");
    Thread.sleep(500);
    assertFalse(blacklist.isBlacklisted("1"));
  }

  @Test
  public final void testIsBlacklisted_disabled() throws Exception {
    blacklist = new IpBlacklist(100000, false, false);
    assertFalse(blacklist.isBlacklisted("1"));
    blacklist.blacklistIp("1");
    assertFalse(blacklist.isBlacklisted("1"));
  }

  @Test
  public final void testIsBlacklisted_force() throws Exception {
    blacklist = new IpBlacklist(100000, true, true);
    assertTrue(blacklist.isBlacklisted("1"));
    blacklist.blacklistIp("1");
    assertTrue(blacklist.isBlacklisted("1"));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void testConstructorPrecondition() throws Exception {
    blacklist = new IpBlacklist(100000, false, true);
  }

  @Test
  public final void testIsBlacklisted_sameIp() throws Exception {
    blacklist = new IpBlacklist(1500, true, false);
    assertFalse(blacklist.isBlacklisted("1"));
    blacklist.blacklistIp("1");
    Thread.sleep(1000);
    blacklist.blacklistIp("1");
    Thread.sleep(1000);
    assertTrue(blacklist.isBlacklisted("1"));
  }
}

