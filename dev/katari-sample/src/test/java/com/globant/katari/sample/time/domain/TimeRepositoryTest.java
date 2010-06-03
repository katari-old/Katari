package com.globant.katari.sample.time.domain;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.user.domain.User;
import com.globant.katari.sample.user.domain.UserRepository;

/** This class represents a TestCase of the time entry repository.
 *
 * @author nicolas.frontini
 */
public class TimeRepositoryTest extends TestCase {

  /** The time entry repository.
   */
  private TimeRepository timeRepository;

  /** The user repository.
   */
  private UserRepository userRepository;

  /** This is a set up method of this TestCase.
   */
  protected final void setUp() {
    timeRepository = (TimeRepository) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("timeRepository");
    userRepository = (UserRepository) SpringTestUtils.getBeanFactory().getBean(
        "userRepository");
    List<TimeEntry> list = timeRepository.getTimeEntries(
        userRepository.findUser(1), new Date());
    for (TimeEntry timeEntry : list) {
      timeRepository.remove(timeEntry);
    }
    Activity activity = timeRepository.findActivity(1);
    Project project = timeRepository.findProject(1);
    TimePeriod period = new TimePeriod("09:00", 120);
    User user = userRepository.findUser(1);
    TimeEntry timeEntry = new TimeEntry(activity, user, project,
        new Date(), period, "Test note");
    timeRepository.save(timeEntry);
  }

  /** Test find activity feature.
   */
  public final void testFindActivity() {
    Activity activity = timeRepository.findActivity(1);
    assertEquals(1, activity.getId());
    activity = timeRepository.findActivity(-1);
    assertNull(activity);
  }

  /** Test find prject feature.
   */
  public final void testFindProject() {
    Project project = timeRepository.findProject(1);
    assertEquals(1, project.getId());
    project = timeRepository.findProject(-1);
    assertNull(project);
  }

  /** Test find time entry feature.
   */
  public final void testFindTimeEntry() {
    List<TimeEntry> timeEntryList = timeRepository.getTimeEntries(
        userRepository.findUser(1), new Date());
    TimeEntry timeEntry = timeRepository.findTimeEntry(
        timeEntryList.get(0).getId());
    assertEquals(timeEntryList.get(0).getId(), timeEntry.getId());
    timeEntry = timeRepository.findTimeEntry(-1);
    assertNull(timeEntry);
  }

  /** Test get prjects method.
   */
  public final void testGetProjects() {
    List<Project> list = timeRepository.getProjects();
    assertFalse(list.isEmpty());
  }

  /** Test get activities method.
   */
  public final void testGetActivities() {
    List<Activity> list = timeRepository.getActivities();
    assertFalse(list.isEmpty());
  }

  /** Test save time entry method.
   */
  public final void testSave_timeEntry() {
    Activity activity = timeRepository.findActivity(1);
    Project project = timeRepository.findProject(1);
    TimePeriod period = new TimePeriod("09:00", 120);
    User user = userRepository.findUser(1);
    TimeEntry timeEntry = new TimeEntry(activity, user, project,
        new Date(), period, "Test note");

    timeRepository.save(timeEntry);
    TimeEntry savedTimeEntry = timeRepository.findTimeEntry(timeEntry.getId());
    assertEquals(timeEntry.getId(), savedTimeEntry.getId());
  }
}
