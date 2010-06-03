/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.user.view;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.user.application.UserFilterCommand;

/** Tests the users controller.
 */
public class UsersControllerTest extends TestCase {

  /** This is the UserController to be tested.
   */
  private UsersController usersController;

  /** This is a set up method of this TestCase.
   */
  protected final void setUp() {
    usersController = (UsersController) SpringTestUtils.getBeanFactory()
        .getBean("/users.do");
  }

  /** Test the handleRequestInternal method.
   */
  public final void testhandleRequestInternal() {
    HttpServletRequest request;
    request = createMock(HttpServletRequest.class);
    HttpServletResponse response;
    response = createMock(HttpServletResponse.class);

    String pageNumberParameter = "1";
    String parameterName = "pageNumber";
    TreeMap<String, String> MyMap = new TreeMap<String, String>();
    MyMap.put("pageNumber", "666");

    expect(request.getParameter(parameterName)).andReturn(pageNumberParameter);
    expectLastCall().anyTimes();
    expect(request.getParameterMap()).andReturn(MyMap);
    expectLastCall().anyTimes();
    replay(request);

    UserFilterCommand userFilterCommand = (UserFilterCommand) SpringTestUtils
        .getBeanFactory().getBean("userFilterCommand");
    try {
      ModelAndView mav = usersController.handle(
          request, response, userFilterCommand,
          new BindException(userFilterCommand, "userFilter"));
      assertNotNull(mav);
    } catch (Exception e) {
      fail();
    }
  }
}
