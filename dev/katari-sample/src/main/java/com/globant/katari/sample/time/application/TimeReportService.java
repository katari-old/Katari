package com.globant.katari.sample.time.application;


import org.apache.commons.lang.Validate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.Date;
import java.util.List;

/** This class is responsible for obtaining DTOS for time reports.
 *
 * @author roman.cunci@globant.com
 */
public class TimeReportService extends HibernateDaoSupport {

  /**
   * Finds the DTOs to show in JasperReports.
   * Sums up the duration of each project for a certain user in
   * a determined time.
   * @param from Starting date from the report. Cannot be null.
   * @param to Ending date from the report. Cannot be null.
   * @return List< UserProjectHoursDTO > for JasperReports
   */
  @SuppressWarnings("unchecked")
  public List<UserProjectHoursReportDTO> getUserProjectHours(final Date from,
      final Date to) {

    Validate.notNull(from, "The starting date cannot be null");
    Validate.notNull(to, "The ending date cannot be null");

    String queryString = "SELECT "
      + "new com.globant.katari.sample.time.application"
      + " .UserProjectHoursReportDTO(user.name, project.name, "
      + "   coalesce(sum(timeEntry.period.duration), 0.0)) "
      + "FROM TimeEntry as timeEntry "
      + "JOIN timeEntry.project as project "
      + "JOIN timeEntry.user as user "
      + "WHERE timeEntry.entryDate BETWEEN :from AND :to "
      + "GROUP BY user.name, project.name";

    return getHibernateTemplate().findByNamedParam(
        queryString,
        new String[] {"from", "to"},
        new Object[] {from, to});
  }
}
