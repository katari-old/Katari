package com.globant.katari.sample.time.application;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.globant.katari.sample.testsupport.DataHelper;
import com.globant.katari.sample.testsupport.SecurityTestUtils;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.time.domain.TimeEntry;
import com.globant.katari.sample.time.domain.TimeRepository;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/** This class represents a TestCase of the view time entries commnad.
 *
 * @author nicolas.frontini
 */
public class ViewTimeEntriesCommandTest extends TestCase {

  /** The view time entries command.
   */
  private ViewTimeEntriesCommand viewTimeEntriesCommand;

  /** The user of the time entry.
   */
  private User user;

  /** This is a set up method of this TestCase.
   */
  public void setUp() {
    viewTimeEntriesCommand = (ViewTimeEntriesCommand) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("viewTimeEntriesCommand");
    TimeRepository repository = (TimeRepository) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("timeRepository");
    UserRepository userRepository = (UserRepository)
        SpringTestUtils.getBeanFactory().getBean("user.userRepository");
    user = userRepository.findUser(1);
    DataHelper.createTimeEntry(repository, user);
    SecurityTestUtils.setContextUser(user);
  }

  /** Test the execute method.
   */
  public void testExecute() {
    viewTimeEntriesCommand.setDate(new Date());
    List<TimeEntry> timeEntries = viewTimeEntriesCommand.execute();
    assertFalse(timeEntries.isEmpty());
  }
}
