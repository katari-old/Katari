package com.globant.katari.sample.time.application;

import java.util.Calendar;
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
public class UserProjectHoursReportCommandTest extends TestCase {

  /** The user project hours report command.
   */
  private UserProjectHoursReportCommand userProjectHoursReportCommand;

  /** This is a set up method of this TestCase.
   */
  @Override
  public void setUp() {
    userProjectHoursReportCommand =
      (UserProjectHoursReportCommand) SpringTestUtils.getTimeModuleBeanFactory()
      .getBean("userProjectHoursReportCommand");
    TimeRepository repository = (TimeRepository) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("timeRepository");
    UserRepository userRepository = (UserRepository)
        SpringTestUtils.getBeanFactory().getBean("user.userRepository");

    User user = userRepository.findUser(1);
    Calendar tmpCalendar = Calendar.getInstance();
    tmpCalendar.set(2008, 1, 15);
    DataHelper.removeExtraTimeEntries(repository);
    DataHelper.createTimeEntry(repository, user, tmpCalendar.getTime());

    SecurityTestUtils.setContextUser(user);
  }

  /** Test the execute method with one entry.
   */
  public void testExecuteSuccess() {
    userProjectHoursReportCommand.setFromDate("01/01/2008");
    userProjectHoursReportCommand.setToDate("02/01/2008");
    List<UserProjectHoursReportDTO> dtos = userProjectHoursReportCommand
      .execute();

    assertFalse(dtos.isEmpty());
    assertEquals(1, dtos.size());
    assertEquals(1L, dtos.get(0).getHours().longValue());
  }

  /** Test the execute method with no entries.
   */
  public void testExecuteFail() {
    userProjectHoursReportCommand.setFromDate("02/01/2008");
    userProjectHoursReportCommand.setToDate("03/01/2008");
    List<UserProjectHoursReportDTO> dtos = userProjectHoursReportCommand
      .execute();

    assertTrue(dtos.isEmpty());
  }
}
