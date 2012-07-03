package com.globant.katari.sample.time.domain;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/** This class represents a TestCase of the time entry repository.
 */
public class TimeRepositoryTest extends TestCase {

  /** The time entry repository.
   */
  private TimeRepository timeRepository;

  /** The user repository.
   */
  private UserRepository userRepository;

  Activity activity;

  Project project;

  User user;

  /** This is a set up method of this TestCase.
   */
  protected final void setUp() {
    timeRepository = (TimeRepository) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("timeRepository");
    userRepository = (UserRepository) SpringTestUtils
    		.get().getBeanFactory().getBean("user.userRepository");
    user = userRepository.findUserByName("admin");
    List<TimeEntry> list = timeRepository.getTimeEntries(user, new Date());
    for (TimeEntry timeEntry : list) {
      timeRepository.remove(timeEntry);
    }
    List<Activity> activities = timeRepository.getActivities();
    activity = activities.get(0);
    List<Project> projects = timeRepository.getProjects();
    project = projects.get(0);
    TimePeriod period = new TimePeriod("09:00", 120);
    TimeEntry timeEntry = new TimeEntry(activity, user, project,
        new Date(), period, "Test note");
    timeRepository.save(timeEntry);
  }

  /** Test find activity feature.
   */
  public final void testFindActivity() {
    Activity loadedActivity = timeRepository.findActivity(activity.getId());
    assertEquals(activity.getId(), loadedActivity.getId());
    loadedActivity = timeRepository.findActivity(-1);
    assertNull(loadedActivity);
  }

  /** Test find project feature.
   */
  public final void testFindProject() {
    Project loadedProject = timeRepository.findProject(project.getId());
    assertEquals(project.getId(), loadedProject.getId());
    loadedProject = timeRepository.findProject(-1);
    assertNull(loadedProject);
  }

  /** Test find time entry feature.
   */
  public final void testFindTimeEntry() {
    List<TimeEntry> timeEntryList;
    timeEntryList = timeRepository.getTimeEntries(user, new Date());
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
    TimePeriod period = new TimePeriod("09:00", 120);
    TimeEntry timeEntry = new TimeEntry(activity, user, project,
        new Date(), period, "Test note");

    timeRepository.save(timeEntry);
    TimeEntry savedTimeEntry = timeRepository.findTimeEntry(timeEntry.getId());
    assertEquals(timeEntry.getId(), savedTimeEntry.getId());
  }
}

