package com.globant.katari.sample.time.application;

import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.globant.katari.sample.testsupport.DataHelper;
import com.globant.katari.sample.testsupport.SecurityTestUtils;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.time.domain.TimeEntry;
import com.globant.katari.sample.time.domain.TimeRepository;
import com.globant.katari.sample.user.domain.User;
import com.globant.katari.sample.user.domain.UserRepository;

/** This class represents a TestCase of the save time entry commnad.
 *
 * @author nicolas.frontini
 */
public class SaveTimeEntryCommandTest extends TestCase {

  /** The time entry repository.
   */
  private TimeRepository repository;

  /** The user repository.
   */
  private UserRepository userRepository;

  /** This is a set up method of this TestCase.
   */
  public void setUp() {
    repository = (TimeRepository) SpringTestUtils.getTimeModuleBeanFactory()
        .getBean("timeRepository");
    userRepository = (UserRepository) SpringTestUtils
        .getBeanFactory().getBean("userRepository");
    DataHelper.removeExtraTimeEntries(repository);

    // Login a user.
    User user = userRepository.findUser(1);
    SecurityTestUtils.setContextUser(user);
  }

  private SaveTimeEntryCommand dafaultCommand() {
    SaveTimeEntryCommand saveCommand = (SaveTimeEntryCommand) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("saveTimeEntryCommand");
    // Create a new time entry.
    saveCommand.setDate(new Date());
    saveCommand.setProjectId(1);
    saveCommand.setActivityId(1);
    saveCommand.setDuration(60);
    saveCommand.setStart("09:00");
    saveCommand.setComment("Test note.");
    return saveCommand;
  }
  /** Test the execute method.
   */
  public void testExecute() {
    User user = userRepository.findUser(1);
    List<TimeEntry> timeEntries = repository.getTimeEntries(
        user, new Date());
    assertEquals(0, timeEntries.size());

    SaveTimeEntryCommand saveTimeEntryCommand = dafaultCommand();
    saveTimeEntryCommand.execute();
    timeEntries = repository.getTimeEntries(user, new Date());
    assertEquals(1, timeEntries.size());

    // Edit the created time entry.
    TimeEntry timeEntry = timeEntries.get(0);
    saveTimeEntryCommand.setTimeEntryId(timeEntry.getId());
    String newComment = "Test note changed.";
    saveTimeEntryCommand.setComment(newComment);
    saveTimeEntryCommand.execute();
    timeEntries = repository.getTimeEntries(user, new Date());
    assertEquals(1, timeEntries.size());
    timeEntry = timeEntries.get(0);
    assertEquals(newComment, timeEntry.getComment());
  }

  /** Tests the validate method without errors.
   */
  public final void testValidate_noError() throws Exception {
    SaveTimeEntryCommand saveTimeEntryCommand = dafaultCommand();

    Errors errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(0, errors.getAllErrors().size());
  }

  /** Tests the validate method with empty start.
   */
  public final void testValidate_emptyStart() throws Exception {
    SaveTimeEntryCommand saveTimeEntryCommand = dafaultCommand();

    saveTimeEntryCommand.setStart("");
    Errors errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(1, errors.getAllErrors().size());
  }

  /** Tests the validate method with empty start.
   */
  public final void testValidate_invalidStart() throws Exception {
    SaveTimeEntryCommand saveTimeEntryCommand = dafaultCommand();

    // Fails because it has an invalid start time.
    saveTimeEntryCommand.setStart("0800");
    Errors errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(1, errors.getAllErrors().size());

    // Fails because it has an invalid start time.
    saveTimeEntryCommand.setStart("08:00:99");
    errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(1, errors.getAllErrors().size());

    // Fails because it has an invalid start time.
    saveTimeEntryCommand.setStart("hh:60");
    errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(1, errors.getAllErrors().size());

    // Fails because it has an invalid start time.
    saveTimeEntryCommand.setStart("10:mm");
    errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(1, errors.getAllErrors().size());

    // Fails because the period expands a day.
    saveTimeEntryCommand.setStart("23:00");
    saveTimeEntryCommand.setDuration(120);
    errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(1, errors.getAllErrors().size());

    // Fails because it has an invalid start time and the period expands a day.
    saveTimeEntryCommand.setStart("25:00");
    saveTimeEntryCommand.setDuration(60);
    errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(2, errors.getAllErrors().size());

    // Fails because it has an invalid start time.
    saveTimeEntryCommand.setStart("22:60");
    errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(1, errors.getAllErrors().size());
  }

  /** Tests the validate method with empty comment.
   */
  public final void testValidate_emptyComment() throws Exception {
    SaveTimeEntryCommand saveTimeEntryCommand = dafaultCommand();

    saveTimeEntryCommand.setComment("");
    Errors errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(1, errors.getAllErrors().size());
  }

  /** Tests the validate method with invalid duration.
   */
  public final void testValidate_invalidDuration() throws Exception {
    SaveTimeEntryCommand saveTimeEntryCommand = dafaultCommand();

    saveTimeEntryCommand.setDuration(0);
    Errors errors = new BindException(saveTimeEntryCommand,
        saveTimeEntryCommand.getClass().getName());
    saveTimeEntryCommand.validate(errors);
    assertEquals(1, errors.getAllErrors().size());
  }
}
