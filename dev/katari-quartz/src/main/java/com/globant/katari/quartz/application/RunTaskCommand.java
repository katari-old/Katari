/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.quartz.application;

import org.apache.commons.lang.Validate;

import org.json.JSONException;
import org.json.JSONObject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.JsonRepresentation;

/** Command to execute a quartz job.
 *
 * The job to run is identified by groupName and jobName, as returned from
 * ListTasksCommand.
 */
public class RunTaskCommand implements Command<JsonRepresentation> {

  /** The Quartz's scheduler, never null.
   */
  private final Scheduler scheduler;

  /** The name of the group of the job to run.
   *
   * It cannot be null.
   */
  private String groupName;

  /** The name of the job to run.
   *
   * It cannot be null.
   */
  private String jobName;

  /** Constructor.
   *
   * @param theScheduler the Quartz scheduler. It cannot be null.
   */
  public RunTaskCommand(final Scheduler theScheduler) {
    Validate.notNull(theScheduler, "The Scheduler cannot be null");
    scheduler = theScheduler;
  }

  /** Executes the job and returns status information.
   *
   * This operation returns an empty json object on success, or, in case of
   * error, a json object of the form:
   *
   * <code>
   *  {
   *    "error": "error message",
   *  }
   * </code>
   *
   * This operation does not wait for the job to finish.
   *
   * @return a json representation, never null.
   */
  public JsonRepresentation execute() {
    JSONObject result = new JSONObject();
    try {
      if (groupName == null) {
        result.put("error", "You must specify a group name");
      } else if (jobName == null) {
        result.put("error", "You must specify a job name");
      } else {
        try {
          scheduler.triggerJob(jobName, groupName);
        } catch (SchedulerException e) {
          result.put("error", e.getMessage());
        }
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }

    return new JsonRepresentation(result);
  }

  /** Sets the quartz group name.
   *
   * @param theGroupName the group name, it cannot be null.
   */
  public void setGroupName(final String theGroupName) {
    groupName = theGroupName;
  }

  /** Sets the quartz job name.
   *
   * @param theJobName the job name, it cannot be null.
   */
  public void setJobName(final String theJobName) {
    jobName = theJobName;
  }
}

