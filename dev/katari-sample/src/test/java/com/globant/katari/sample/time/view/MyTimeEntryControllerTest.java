/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.time.view;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.sample.testsupport.DataHelper;
import com.globant.katari.sample.testsupport.SecurityTestUtils;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.time.application.SaveTimeEntryCommand;
import com.globant.katari.sample.time.domain.TimeEntry;
import com.globant.katari.sample.time.domain.TimeRepository;
import com.globant.katari.sample.user.domain.User;
import com.globant.katari.sample.user.domain.UserRepository;

/** Test the MyTimeController.
 *
 * @author nicolas.frontini
 */
public class MyTimeEntryControllerTest extends TestCase {

  /** This is the my time controller.
  */
  private MyTimeController myTimeController;

  /** The time entry repository.
  */
  private TimeRepository timeRepository;

  /** The user repository.
   */
  private UserRepository userRepository;

  /** This is a set up method of this TestCase.
   */
  protected final void setUp() {
    myTimeController = (MyTimeController) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("/myTime.do");
    timeRepository = (TimeRepository) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("timeRepository");
    userRepository = (UserRepository) SpringTestUtils
        .getBeanFactory().getBean("userRepository");
    User user = userRepository.findUser(1);
    DataHelper.createTimeEntry(timeRepository, user);

    SecurityTestUtils.setContextUser(user);
  }

  /** Test the referenceData method.
   */
  @SuppressWarnings("unchecked")
  public final void testReferenceData() throws Exception {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getAttribute("baseweb")).andReturn("path").anyTimes();
    expect(request.getParameterNames()).andReturn(null).anyTimes();
    replay(request);

    Map map = myTimeController.referenceData(request);

    List<TimeEntry> list = (List<TimeEntry>) map.get("timeEntryList");
    assertNotNull(list);

    User user = (User) map.get("user");
    assertNotNull(user);

    String date = (String) map.get("date");
    assertNotNull(date);
  }

  /** Test the OnSubmit method.
   */
  public final void testOnSubmit() throws Exception {
    HttpServletRequest request;
    request = createMock(HttpServletRequest.class);
    HttpServletResponse response;
    response = createMock(HttpServletResponse.class);

    SaveTimeEntryCommand saveTimeEntryCommand = (SaveTimeEntryCommand)
        SpringTestUtils.getTimeModuleBeanFactory().getBean(
        "saveTimeEntryCommand");
    saveTimeEntryCommand.setActivityId(1);
    saveTimeEntryCommand.setProjectId(1);
    saveTimeEntryCommand.setComment("Test comment.");
    saveTimeEntryCommand.setDuration(60);
    saveTimeEntryCommand.setStart("09:00");
    saveTimeEntryCommand.setDate(new Date());
    ModelAndView mav = myTimeController.onSubmit(request, response,
          saveTimeEntryCommand, new BindException(saveTimeEntryCommand,
          "command"));
      assertNotNull(mav);
  }
}

