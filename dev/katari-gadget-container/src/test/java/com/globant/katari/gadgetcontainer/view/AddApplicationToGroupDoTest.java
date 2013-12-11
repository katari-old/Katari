/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.view;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.core.spring.controller.JsonCommandController;

import javax.servlet.http.HttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import org.springframework.context.ApplicationContext;

import com.globant.katari.shindig.domain.Application;

import com.globant.katari.gadgetcontainer.domain.CustomizableGadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

import com.globant.katari.gadgetcontainer.domain.SampleUser;

public class AddApplicationToGroupDoTest {

  private String gadgetXmlUrl1 = "file:///" + new File(
      "src/test/resources/SampleGadget.xml").getAbsolutePath();

  private String gadgetXmlUrl2 = "file:///" + new File(
      "src/test/resources/SampleGadget2.xml").getAbsolutePath();

  private ApplicationContext appContext;

  private Session session;

  @Before
  public void setUp() throws Exception {
    SpringTestUtils.get().clearDatabase();
    SpringTestUtils.get().beginTransaction();
    appContext = SpringTestUtils.get().getBeanFactory();
    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
      .openSession();
  }

  @After public void after() {
    SpringTestUtils.get().endTransaction();
  }


  @Test
  public void test() throws Exception {

    CoreUser user = new SampleUser("me");
    session.saveOrUpdate(user);
    user = (CoreUser) session.createQuery("from CoreUser").uniqueResult();

    GadgetGroupRepository repository = (GadgetGroupRepository)
      appContext.getBean("gadgetcontainer.gadgetGroupRepository");

    Application app1 = new Application(gadgetXmlUrl1);
    repository.getSession().saveOrUpdate(app1);
    Application app2 = new Application(gadgetXmlUrl2);
    repository.getSession().saveOrUpdate(app2);

    CustomizableGadgetGroup group;
    group = new CustomizableGadgetGroup(user, "sample", "default", 2);
    group.add(new GadgetInstance(app1, 0, 0));
    repository.save(group);

    // Sets the currently logged on user
    SpringTestUtils.setLoggedInUser(user);

    JsonCommandController controller = (JsonCommandController)
      appContext.getBean("/addApplicationToGroup.do");

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(os);

    MockHttpServletRequest request;
    request = new MockHttpServletRequest("GET", "addApplicationToGroup.do");
    request.setParameter("groupName", "sample");
    request.setParameter("applicationId", Long.toString(app2.getId()));

    HttpServletResponse response = createMock(HttpServletResponse.class);
    response.addHeader("Content-type", "application/json; charset=UTF-8");
    expect(response.getWriter()).andReturn(writer);
    replay(response);

    ModelAndView modelAndView = controller.handleRequest(request, response);
    assertThat(modelAndView, nullValue());

    writer.flush();
    assertThat(os.toString(), is("{}"));

    // Now we verify. There should be two gadgets in the first column.
    group = repository.findCustomizableGadgetGroup(user.getId(), "sample");
    int col0 = 0;
    int col1 = 0;
    for (GadgetInstance gadget: group.getGadgets()) {
      if (gadget.getColumn() == 0) {
        ++ col0;
      } else if (gadget.getColumn() == 1) {
        ++ col1;
      }
    }
    assertThat(col0, is(2));
    assertThat(col1, is(0));
  }

}

