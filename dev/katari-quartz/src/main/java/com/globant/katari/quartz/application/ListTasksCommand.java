/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang.Validate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.JsonRepresentation;
import com.globant.katari.quartz.domain.ScheduledCommand;

/** Command to obtain the list of scheduled commands that quartz
 * executes.
 */
public class ListTasksCommand implements Command<JsonRepresentation> {

  /** The Quartz's scheduler, never null.
   */
  private final Scheduler scheduler;

  /** The date formatter, never null.
   *
   * This is initialized to an iso 8601 extended format.
   */
  private final SimpleDateFormat dateFormatter;

  /** Constructor.
   *
   * @param theScheduler the Quartz scheduler. It cannot be null.
   */
  public ListTasksCommand(final Scheduler theScheduler) {
    Validate.notNull(theScheduler, "The Scheduler cannot be null");
    scheduler = theScheduler;
    dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  /** Return the json representation of the Task.
   *
   * Example of the output json array below:
   * <code>
   * [
   *  {
   *    "progressPercent": "10",
   *    "friendlyName": "The Friendly Name",
   *    "isRunning": "true" / "false",
   *    "information": {...},
   *    "nextExecutionTime": "2010-10-19T20:00:00Z",
   *    "lastExecutionTime": "2010-10-19T14:00:00Z",
   *  }
   * ]
   * </code>
   *
   * The progressPercent, nextExecutionTime and lastExecutionTime are optional.
   * If they are not known, then they are not sent to the client.
   *
   * Dates are in iso 8601, extended format. They are in utc, designated with
   * the Z suffix. Precision is up to the second.
   *
   * @return a json representation, never null.
   */
  public JsonRepresentation execute() {
    try {
      List<Task> tasks = getTasks();

      JSONArray tasksJson = new JSONArray();
      for (Task task : tasks) {
        ScheduledCommand command = task.getCommand();
        JSONObject taskJson = new JSONObject();
        taskJson.put("progressPercent", command.getProgressPercent());
        taskJson.put("friendlyName", command.getDisplayName());
        taskJson.put("isRunning", task.isRunning());
        taskJson.put("information", command.getInformation());
        taskJson.put("nextExecutionTime",
            formatDate(task.getNextExecutionTime()));
        taskJson.put("lastExecutionTime",
            formatDate(task.getLastExecutionTime()));
        tasksJson.put(taskJson);
      }
      return new JsonRepresentation(tasksJson);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  /** Formats the given date with the pattern built in the constructor.
   *
   * @param date the date. If it is null, then it returns null.
   *
   * @return the string representation of the formated Date or null if the
   * given date is null.
   */
  private String formatDate(final Date date) {
    if(date == null) {
      return null;
    } else {
      return dateFormatter.format(date);
    }
  }

  /** Retrieves the list of Quartz tasks.
   *
   * @return the list of tasks, never null.
   */
  @SuppressWarnings("unchecked")
  public List<Task> getTasks() {
    List<Task> tasks = new ArrayList<Task>();
    try {
      List<JobExecutionContext> runningJobs;
      runningJobs = scheduler.getCurrentlyExecutingJobs();
      String[] groups = scheduler.getJobGroupNames();
      for(String group : groups) {
        String[] triggers = scheduler.getTriggerNames(group);
        String[] jobs = scheduler.getJobNames(group);
        for(String job : jobs) {
          JobDetail detail = scheduler.getJobDetail(job, group);
          JobDataMap dataMap =  detail.getJobDataMap();
          MethodInvokingJobDetailFactoryBean jobDetailFactoryBean;
          Object mi = dataMap.get("methodInvoker");
          if(mi instanceof MethodInvokingJobDetailFactoryBean) {
            jobDetailFactoryBean = (MethodInvokingJobDetailFactoryBean) mi;
            Object targetObject = jobDetailFactoryBean.getTargetObject();
            if(targetObject instanceof ScheduledCommand) {
              for(String theTrigger : triggers) {
                Trigger trigger = scheduler.getTrigger(theTrigger, group);
                if(trigger.getJobName().equals(detail.getName())) {
                  Date nextExecutionTime = trigger.getNextFireTime();
                  Date lastExecutionTime = trigger.getPreviousFireTime();
                  boolean jobIsRunning;
                  jobIsRunning = isJobDetailInContext(runningJobs, detail);
                  Task task = new Task((ScheduledCommand) targetObject,
                      jobIsRunning, nextExecutionTime, lastExecutionTime);
                  tasks.add(task);
                }
              }
            }
          }
        }
      }
      return tasks;
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }

  /** Decides if the job detail is in the provided list of JobExecutionContext.
   *
   * @param executionContexts the list of job execution contexts.
   *
   * @param detail the job detail to search.
   *
   * @return true if the job detail was found in any of the job execution
   * contexts.
   */
  private boolean isJobDetailInContext(
      final List<JobExecutionContext> executionContexts,
      final JobDetail detail) {
    for (JobExecutionContext context : executionContexts) {
      if (context.getJobDetail().equals(detail)) {
        return true;
      }
    }
    return false;
  }
}

