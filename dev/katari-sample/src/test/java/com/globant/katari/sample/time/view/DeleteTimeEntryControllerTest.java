package com.globant.katari.sample.time.view;

import static org.easymock.EasyMock.createMock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.sample.testsupport.DataHelper;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.time.application.DeleteTimeEntryCommand;
import com.globant.katari.sample.time.domain.TimeRepository;
import com.globant.katari.user.domain.User;
import com.globant.katari.user.domain.UserRepository;

/** Test the DeleteTimeEntryController.
 */
public class DeleteTimeEntryControllerTest extends TestCase {

  /** This is the delete time entry controller.
  */
  private DeleteTimeEntryController deleteTimeEntryController;

  /** The time entry repository.
  */
  private TimeRepository timeRepository;

  /** The user repository.
   */
  private UserRepository userRepository;

  private long timeEntryId;

  /** This is a set up method of this TestCase.
   */
  protected final void setUp() {
    deleteTimeEntryController = (DeleteTimeEntryController) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("/deleteTimeEntry.do");
    timeRepository = (TimeRepository) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("timeRepository");
    userRepository = (UserRepository) SpringTestUtils.get()
        .getBeanFactory().getBean("user.userRepository");
    User user = userRepository.findUserByName("admin");
    DataHelper.createTimeEntry(timeRepository, user);
    timeEntryId = timeRepository.getTimeEntries().get(0).getId();
  }

  /** Test the OnSubmit method.
   */
  public final void testOnSubmit() {
    HttpServletRequest request;
    request = createMock(HttpServletRequest.class);
    HttpServletResponse response;
    response = createMock(HttpServletResponse.class);

    DeleteTimeEntryCommand deleteTimeEntryCommand = (DeleteTimeEntryCommand)
        SpringTestUtils.getTimeModuleBeanFactory().getBean(
        "deleteTimeEntryCommand");
    deleteTimeEntryCommand.setTimeEntryId(timeEntryId);
    try {
      ModelAndView mav = deleteTimeEntryController.onSubmit(request, response,
          deleteTimeEntryCommand, new BindException(deleteTimeEntryCommand,
          "command"));
      assertNotNull(mav);
    } catch (Exception e) {
      fail();
    }
  }
}
