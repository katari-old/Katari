package com.globant.katari.sample.time.application;

import org.apache.commons.lang.Validate;

/**
 * DTO with a username, one of the projects he/she is into and the hours
 * spent in that project.
 * It's used in the UserProjectHours report and is constructed by hibernate.
 *
 * @author roman.cunci
 */
public class UserProjectHoursReportDTO {

  /** userName. Cannot be null. */
  private String userName;
  /** projectName. Cannot be null. */
  private String projectName;
  /** hours. Cannot be null. */
  private Number hours;

  /**
   * Constructor.
   *
   * @param theUserName A user name. Cannot be null.
   * @param theProjectName A project name. Cannot be null.
   * @param theHours The hours spent. Cannot be null.
   */
  public UserProjectHoursReportDTO(final String theUserName,
      final String theProjectName, final Number theHours) {
    Validate.notNull(theUserName, "The username cannot be null.");
    Validate.notNull(theProjectName, "The project name cannot be null.");
    Validate.notNull(theHours, "The hours cannot be null");

    userName = theUserName;
    projectName = theProjectName;
    hours = theHours;
  }

  /** Returns the name of the user.
   * @return String User name.
   */
  public String getUserName() {
    return userName;
  }

  /** Returns the name of the project.
   * @return String Project name.
   */
  public String getProjectName() {
    return projectName;
  }

  /** Returns the hours spent.
   * @return double Hours
   */
  public Number getHours() {
    return hours;
  }
}
