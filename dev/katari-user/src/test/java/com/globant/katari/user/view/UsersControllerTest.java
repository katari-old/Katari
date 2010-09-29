/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.view;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.user.SpringTestUtils;
import com.globant.katari.user.application.UserFilterCommand;

/** Tests the users controller.
 */
public class UsersControllerTest {

  /** This is the UserController to be tested.
   */
  private UsersController usersController;

  /** This is a set up method of this TestCase.
   */
  @Before
  public final void setUp() {
    usersController = (UsersController) SpringTestUtils
        .getServletBean("/users.do");
  }

  /** Test the handleRequestInternal method.
   */
  @Test
  public final void testhandleRequestInternal() throws Exception {
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
        .getServletBean("userFilterCommand");

    ModelAndView mav = usersController.handle(
        request, response, userFilterCommand,
        new BindException(userFilterCommand, "userFilter"));
    assertThat(mav, is(notNullValue()));
  }
}

