/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.view;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.classextension.EasyMock.*;

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

import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

import com.globant.katari.gadgetcontainer.domain.SampleUser;

public class RemoveApplicationFromGroupDoTest {

  private String gadgetXmlUrl1 = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String gadgetXmlUrl2 = "file:///" + new File(
      "target/test-classes/SampleGadget2.xml").getAbsolutePath();

  private ApplicationContext appContext;

  private Session session;

  @Before
  public void setUp() throws Exception {

    appContext = SpringTestUtils.getContext();

    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
      .openSession();

    session.createQuery("delete from GadgetInstance").executeUpdate();
    session.createQuery("delete from GadgetGroup").executeUpdate();
    session.createQuery("delete from CoreUser").executeUpdate();
    session.createSQLQuery("delete from supported_views").executeUpdate();
    session.createQuery("delete from Application").executeUpdate();
  }

  @Test
  public void test() throws Exception {

    CoreUser user = new SampleUser("me");
    session.saveOrUpdate(user);
    user = (CoreUser) session.createQuery("from CoreUser").uniqueResult();

    GadgetGroupRepository repository = (GadgetGroupRepository)
      appContext.getBean("gadgetcontainer.gadgetGroupRepository");

    Application app1 = new Application(gadgetXmlUrl1);
    session.saveOrUpdate(app1);
    Application app2 = new Application(gadgetXmlUrl2);
    session.saveOrUpdate(app2);

    GadgetGroup group = new GadgetGroup(user, "sample", "default", 2);
    group.add(new GadgetInstance(app1, 0, 0));
    GadgetInstance instanceToRemove = new GadgetInstance(app1, 0, 0);
    group.add(instanceToRemove);
    repository.save(group);

    // Sets the currently logged on user
    SpringTestUtils.setLoggedInUser(user);

    JsonCommandController controller = (JsonCommandController)
      appContext.getBean("/removeApplicationFromGroup.do");

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(os);

    MockHttpServletRequest request = new MockHttpServletRequest("GET",
        "removeApplicationFromGroup.do");
    request.setParameter("groupName", "sample");
    request.setParameter("gadgetId", Long.toString(instanceToRemove.getId()));

    HttpServletResponse response = createMock(HttpServletResponse.class);
    response.addHeader("Content-type", "application/json; charset=UTF-8");
    expect(response.getWriter()).andReturn(writer);
    replay(response);

    ModelAndView mv;
    mv = controller.handleRequest(request, response);

    assertThat(mv, nullValue());

    writer.flush();
    assertThat(os.toString(), is("{}"));

    // Now we verify. There should be two gadgets in the first column.
    group = repository.findGadgetGroup(user.getId(), "sample");
    assertThat(group.getGadgets().size(), is(1));
  }

  @After
  public void tearDown() {
    session.close();
  }
}

