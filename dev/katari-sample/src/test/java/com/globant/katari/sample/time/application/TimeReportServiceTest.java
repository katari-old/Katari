package com.globant.katari.sample.time.application;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.globant.katari.sample.testsupport.DataHelper;
import com.globant.katari.sample.testsupport.SecurityTestUtils;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.time.domain.TimeRepository;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/** This class represents a TestCase of the user project hours report command.
 *
 * @author roman.cunci
 */
public class TimeReportServiceTest extends TestCase {

  /** The time report service.
   */
  private TimeReportService timeReportService;

  private User user;

  /** This is a set up method of this TestCase.
   */
  @Override
  public void setUp() {

    SpringTestUtils.get().beginTransaction();

    timeReportService = (TimeReportService) SpringTestUtils
      .getTimeModuleBeanFactory() .getBean("timeReportService");
    TimeRepository repository = (TimeRepository) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("timeRepository");
    UserRepository userRepository = (UserRepository)
        SpringTestUtils.get().getBeanFactory().getBean("user.userRepository");

    user = userRepository.findUserByName("admin");
    DataHelper.removeExtraTimeEntries(repository);

    Calendar tmpCalendar = Calendar.getInstance();
    tmpCalendar.set(2008, 1, 15);
    DataHelper.createTimeEntry(repository, user, tmpCalendar.getTime());
    tmpCalendar.set(2008, 2, 15);
    DataHelper.createTimeEntry(repository, user, tmpCalendar.getTime());

    SecurityTestUtils.setContextUser(user);
  }

  /** {@inheritDoc}. */
  @Override
  protected void tearDown() throws Exception {
    SpringTestUtils.get().endTransaction();
  }

  /** Test the execute method with one entry.
   */
  public void testExecuteSuccess() {
    Calendar tmpCalendar = Calendar.getInstance();
    tmpCalendar.set(2008, 1, 1);
    Date from = tmpCalendar.getTime();
    tmpCalendar.set(2008, 2, 1);
    Date to = tmpCalendar.getTime();

    List<UserProjectHoursReportDTO> dtos =
      timeReportService.getUserProjectHours(from, to);

    assertFalse(dtos.isEmpty());
    assertEquals(1, dtos.size());
    assertEquals(1L, dtos.get(0).getHours().longValue());
  }

  /** Test the execute method with two entries from the same project and user.
   */
  public void testExecuteSum() {
    Calendar tmpCalendar = Calendar.getInstance();
    tmpCalendar.set(2008, 1, 1);
    Date from = tmpCalendar.getTime();
    tmpCalendar.set(2008, 3, 1);
    Date to = tmpCalendar.getTime();

    List<UserProjectHoursReportDTO> dtos =
      timeReportService.getUserProjectHours(from, to);

    assertFalse(dtos.isEmpty());
    assertEquals(1, dtos.size());
    assertEquals(2L, dtos.get(0).getHours().longValue());
  }
}

