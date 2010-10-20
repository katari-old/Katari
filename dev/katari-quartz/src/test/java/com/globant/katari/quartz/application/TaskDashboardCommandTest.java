/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.application;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.junit.Test;

import com.globant.katari.core.application.JsonRepresentation;
import com.globant.katari.quartz.domain.ScheduledCommand;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

/** @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
@SuppressWarnings("deprecation")
public class TaskDashboardCommandTest {

  private Date last = new Date(110, 9, 20, 13, 30, 0);
  private Date next = new Date(110, 9, 20, 15, 30, 0);

  /** Creates a scheduler that contains the provided command.
   */
  private Scheduler createScheduler(final ScheduledCommand command)
      throws Exception {
    Scheduler scheduler = createMock(Scheduler.class);
    JobDetail detail = createMock(JobDetail.class);
    JobDataMap dataMap = createMock(JobDataMap.class);
    MethodInvokingJobDetailFactoryBean methodInvoking;
    methodInvoking = createMock(MethodInvokingJobDetailFactoryBean.class);
    Trigger trigger = createMock(Trigger.class);

    String group1 = "group1";
    String trigger1 = "trigger1";
    String job1 = "job1";
    String[] jobs = new String[] { job1 };
    String[] groups = new String[] { group1 };
    String[] triggers = new String[] { trigger1 };

    expect(scheduler.getJobGroupNames()).andReturn(groups);
    expect(scheduler.getTriggerNames(group1)).andReturn(triggers);
    expect(scheduler.getJobNames(group1)).andReturn(jobs);
    expect(scheduler.getJobDetail(job1, group1)).andReturn(detail);
    expect(detail.getJobDataMap()).andReturn(dataMap);
    expect(dataMap.get("methodInvoker")).andReturn(methodInvoking);
    expect(methodInvoking.getTargetObject()).andReturn(command);
    expect(scheduler.getTrigger(trigger1, group1)).andReturn(trigger);
    String jobName = "jobName";
    expect(trigger.getJobName()).andReturn(jobName);
    expect(detail.getName()).andReturn(jobName);
    expect(trigger.getNextFireTime()).andReturn(next);
    expect(trigger.getPreviousFireTime()).andReturn(last);

    replay(scheduler);
    replay(detail);
    replay(dataMap);
    replay(methodInvoking);
    replay(trigger);

    return scheduler;
  }

  @Test
  public void testExecute() throws Exception {

    ScheduledCommand job = createMock(ScheduledCommand.class);
    expect(job.getProgressPercent()).andReturn(new Integer(4));
    expect(job.getDisplayName()).andReturn("The friendly name");
    Map<String, String> information = new HashMap<String,String>();
    information.put("Processing row","104");
    expect(job.getInformation()).andReturn(information);
    replay(job);

    TaskDashboardCommand command;
    command = new TaskDashboardCommand(createScheduler(job));

    JsonRepresentation result = command.execute();

    StringWriter writer = new StringWriter();
    result.write(writer);

    assertThat(writer.toString(), is(baselineJson(true)));
  }

  @Test
  public void testExecute_noProgress() throws Exception {

    ScheduledCommand job = createMock(ScheduledCommand.class);
    expect(job.getProgressPercent()).andReturn(null);
    expect(job.getDisplayName()).andReturn("The friendly name");
    Map<String, String> information = new HashMap<String,String>();
    information.put("Processing row","104");
    expect(job.getInformation()).andReturn(information);
    replay(job);

    TaskDashboardCommand command;
    command = new TaskDashboardCommand(createScheduler(job));

    JsonRepresentation result = command.execute();

    StringWriter writer = new StringWriter();
    result.write(writer);

    assertThat(writer.toString(), is(baselineJson(false)));
  }

  /** Creates the baseline json string, a string with a sample json object.
   *
   * @return the json string.
   *
   * @throws JSONException
   */
  private String baselineJson(final boolean isRunning) throws JSONException {

    JSONObject task = new JSONObject();
    if (isRunning) {
      task.put("progressPercent", 4);
    }

    task.put("friendlyName", "The friendly name");
    task.put("nextExecutionTime", "10-20-2010 15:30");
    task.put("lastExecutionTime", "10-20-2010 13:30");

    JSONObject information = new JSONObject();
    information.put("Processing row", "104");

    task.put("information", information);

    JSONArray tasks = new JSONArray();
    tasks.put(task);

    return tasks.toString();
  }
}

