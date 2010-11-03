/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.application;

import static org.easymock.classextension.EasyMock.*;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

import java.io.StringWriter;

import org.json.JSONObject;

import com.globant.katari.core.application.JsonRepresentation;

import org.quartz.Scheduler;

public class RunTaskCommandTest {

  @Test
  public void testExecute() throws Exception {
    Scheduler scheduler = createMock(Scheduler.class);
    scheduler.triggerJob("job-name", "group-name");
    replay(scheduler);
    RunTaskCommand command = new RunTaskCommand(scheduler);
    command.setGroupName("group-name");
    command.setJobName("job-name");

    JsonRepresentation result = command.execute();

    StringWriter writer = new StringWriter();
    result.write(writer);

    assertThat(writer.toString(), is("{}"));
    verify(scheduler);
  }

  @Test
  public void testExecute_noGroupName() throws Exception {
    Scheduler scheduler = createMock(Scheduler.class);
    RunTaskCommand command = new RunTaskCommand(scheduler);
    command.setJobName("job-name");

    JsonRepresentation result = command.execute();

    JSONObject baseline = new JSONObject();
    baseline.put("error", "You must specify a group name");

    StringWriter writer = new StringWriter();
    result.write(writer);

    assertThat(writer.toString(), is(baseline.toString()));
  }

  @Test
  public void testExecute_noJobName() throws Exception {
    Scheduler scheduler = createMock(Scheduler.class);
    RunTaskCommand command = new RunTaskCommand(scheduler);
    command.setGroupName("group-name");

    JsonRepresentation result = command.execute();

    JSONObject baseline = new JSONObject();
    baseline.put("error", "You must specify a job name");

    StringWriter writer = new StringWriter();
    result.write(writer);

    assertThat(writer.toString(), is(baseline.toString()));
  }

}

