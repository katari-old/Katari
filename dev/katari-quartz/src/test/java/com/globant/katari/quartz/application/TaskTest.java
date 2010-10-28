package com.globant.katari.quartz.application;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Date;

import org.junit.Test;

import com.globant.katari.quartz.domain.ScheduledCommand;
import com.globant.katari.quartz.MockScheduledCommand;

/** @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class TaskTest {

  @Test(expected = IllegalArgumentException.class)
  public void testTask_invalid() {
    new Task(null, false, new Date(), new Date());
  }

  @Test
  public void testTask() {
    Date next = new Date(System.currentTimeMillis());
    Date last = new Date(System.currentTimeMillis() - 1000);

    MockScheduledCommand cmd = new MockScheduledCommand();
    Task task = new Task(cmd, false, next, last);

    assertThat(task.getCommand(), is((ScheduledCommand) cmd));
    assertThat(task.getCommand().getDisplayName(), is("Mock Impl"));
    assertThat(task.getCommand().getProgressPercent(), is(100));

    assertThat(task.getNextExecutionTime(), is(next));
    assertThat(task.getLastExecutionTime(), is(last));

    assertThat(task.isRunning(), is(false));
  }
}

