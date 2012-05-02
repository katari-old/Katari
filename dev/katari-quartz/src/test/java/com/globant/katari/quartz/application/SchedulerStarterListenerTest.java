/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.application;

import static org.easymock.EasyMock.*;

import org.junit.Test;

import org.quartz.Scheduler;

public class SchedulerStarterListenerTest {

  @Test public void contextInitialized() throws Exception {
    Scheduler scheduler = createMock(Scheduler.class);
    scheduler.start();
    replay(scheduler);
    SchedulerStarterListener listener;
    listener = new SchedulerStarterListener(scheduler);
    listener.contextInitialized(null);
    verify(scheduler);
  }

  @Test public void contextDestroyed() throws Exception {
    Scheduler scheduler = createMock(Scheduler.class);
    scheduler.shutdown();
    replay(scheduler);
    SchedulerStarterListener listener;
    listener = new SchedulerStarterListener(scheduler);
    listener.contextDestroyed(null);
    verify(scheduler);
  }
}

