package com.globant.katari.sample.time.view;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.sample.testsupport.DataHelper;
import com.globant.katari.sample.testsupport.SecurityTestUtils;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.time.application.UserProjectHoursReportCommand;
import com.globant.katari.sample.time.domain.TimeRepository;
import com.globant.katari.sample.user.domain.User;
import com.globant.katari.sample.user.domain.UserRepository;

/** Test the UserProjectHoursReportController.
 *
 * @author roman.cunci
 */
public class UserProjectHoursReportControllerTest extends TestCase {

  /** This is the user project hours report controller.
  */
  private UserProjectHoursReportController userProjectHoursReportController;

  /** The time entry repository.
  */
  private TimeRepository timeRepository;

  /** The user repository.
   */
  private UserRepository userRepository;

  /** This is a set up method of this TestCase.
   */
  @Override
  protected final void setUp() {
    userProjectHoursReportController = (UserProjectHoursReportController)
      SpringTestUtils.getTimeModuleBeanFactory()
      .getBean("/userProjectHoursReport.do");
    timeRepository = (TimeRepository) SpringTestUtils
        .getTimeModuleBeanFactory().getBean("timeRepository");
    userRepository = (UserRepository) SpringTestUtils
        .getBeanFactory().getBean("userRepository");
    DataHelper.createTimeEntry(timeRepository, userRepository.findUser(1));
  }

  /** Test the referenceData method.
   * @throws Exception if the test fails
   */
  @SuppressWarnings("unchecked")
  public final void testReferenceData() throws Exception {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getAttribute("baseweb")).andReturn("path").anyTimes();
    replay(request);

    User user = userRepository.findUser(1);
    SecurityTestUtils.setContextUser(user);
    Map map = userProjectHoursReportController.referenceData(request);
    assertFalse(map.isEmpty());
  }

  /** Test the OnSubmit method.
   * @throws Exception if the test fails
   */
  public final void testOnSubmit() throws Exception {
    HttpServletRequest request;
    request = createMock(HttpServletRequest.class);
    HttpServletResponse response;
    response = createMock(HttpServletResponse.class);

    UserProjectHoursReportCommand userProjectHoursReportCommand =
      (UserProjectHoursReportCommand) SpringTestUtils
      .getTimeModuleBeanFactory().getBean("userProjectHoursReportCommand");

    userProjectHoursReportCommand.setFormat("pdf");
    userProjectHoursReportCommand.setFromDate("01/01/2008");
    userProjectHoursReportCommand.setToDate("02/02/2008");

    ModelAndView mav = userProjectHoursReportController.onSubmit(
        request, response, userProjectHoursReportCommand,
        new BindException(userProjectHoursReportCommand, "command"));
    assertNotNull(mav);
  }
}
