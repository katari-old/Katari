package com.globant.katari.sample.time.application;

import java.util.Date;

import junit.framework.TestCase;

import com.globant.katari.sample.testsupport.DataHelper;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.time.domain.TimeEntry;
import com.globant.katari.sample.time.domain.TimeRepository;
import com.globant.katari.sample.user.domain.UserRepository;

/** This class represents a TestCase of the delete time entry commnad.
 *
 * @author nicolas.frontini
 */
public class DeleteTimeEntryCommandTest extends TestCase {

  /** The delete time entry command.
   */
  private DeleteTimeEntryCommand deleteTimeEntryCommand;

  /** The time entry repository.
   */
  private TimeRepository timeRepository;

  /** The user repository.
   */
  private UserRepository userRepository;

  /** The user id of the time entry.
   */
  private long userId = 1;

  /** This is a set up method of this TestCase.
   */
  public void setUp() {
    deleteTimeEntryCommand = (DeleteTimeEntryCommand) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("deleteTimeEntryCommand");
    timeRepository = (TimeRepository) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("timeRepository");
    userRepository = (UserRepository) SpringTestUtils.getBeanFactory()
        .getBean("userRepository");
    DataHelper.removeExtraTimeEntries(timeRepository);
    DataHelper.createTimeEntry(
        timeRepository, userRepository.findUser(userId));
  }

  /** Test the execute method.
   */
  public void testExecute() {
    TimeEntry timeEntry = timeRepository.getTimeEntries(
        userRepository.findUser(userId), new Date()).get(0);
    long timeEntryId = timeEntry.getId();
    assertNotNull(timeRepository.findTimeEntry(timeEntryId));
    deleteTimeEntryCommand.setTimeEntryId(timeEntryId);
    deleteTimeEntryCommand.execute();
    assertNull(timeRepository.findTimeEntry(timeEntryId));
  }
}
