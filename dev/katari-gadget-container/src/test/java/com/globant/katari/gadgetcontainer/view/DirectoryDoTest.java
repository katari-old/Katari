/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.view;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import com.globant.katari.gadgetcontainer.application.ListApplicationsCommand;
import com.globant.katari.gadgetcontainer.domain.CustomizableGadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;
import com.globant.katari.gadgetcontainer.domain.SampleUser;
import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.shindig.domain.Application;

public class DirectoryDoTest {

  private String gadgetXmlUrl = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private ApplicationContext appContext;

  private GadgetGroupRepository repository;

  @Before
  public void setUp() throws Exception {

    SpringTestUtils.get().clearDatabase();
    SpringTestUtils.get().beginTransaction();

    appContext = SpringTestUtils.get().getBeanFactory();
    repository = ((GadgetGroupRepository) appContext.getBean(
          "gadgetcontainer.gadgetGroupRepository"));
  }

  @After public void after() {
    SpringTestUtils.get().endTransaction();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test() throws Exception {

    CoreUser user = new SampleUser("me");
    repository.getSession().saveOrUpdate(user);
    user = (CoreUser) repository.find("from CoreUser").get(0);

    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "gadget group", "default", 2);
    repository.getSession().saveOrUpdate(group);

    Application app = new Application(gadgetXmlUrl);
    repository.getSession().saveOrUpdate(app);

    // Sets the currently logged on user
    SpringTestUtils.setLoggedInUser(user);

    ViewCommandController controller;
    controller = (ViewCommandController) appContext.getBean("/directory.do");

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(os);

    MockHttpServletRequest request;
    request = new MockHttpServletRequest("GET", "directory.do");
    request.setParameter("gadgetGroupName", "gadget group");

    HttpServletResponse response = createMock(HttpServletResponse.class);
    response.addHeader("Content-type", "application/json");
    expect(response.getWriter()).andReturn(writer);
    replay(response);

    ModelAndView mv;
    mv = controller.handleRequest(request, response);

    assertThat(mv.getViewName(), is("directory.ftl"));
    assertThat(mv.getModel().get("command"), is(ListApplicationsCommand.class));
    assertThat(mv.getModel().get("result"), is(List.class));
    List<Application> applications;
    applications = (List<Application>) mv.getModel().get("result");
    assertThat(applications.get(0).getTitle(), is("Test title"));
  }
}

