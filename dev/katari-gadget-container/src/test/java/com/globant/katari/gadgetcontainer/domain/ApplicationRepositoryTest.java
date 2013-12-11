/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.globant.katari.gadgetcontainer.SpringTestUtils;
import com.globant.katari.shindig.domain.Application;

/** Test for the repository ApplicationRepository.
 */
public class ApplicationRepositoryTest {

  private static final String REPOSITORY
    = "gadgetcontainer.applicationRepository";
  private ApplicationRepository repository;
  private ApplicationContext appContext;
  private Session session;

  private String gadgetUrl1 = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String gadgetUrl2 = "file:///" + new File(
      "target/test-classes/SampleGadget2.xml").getAbsolutePath();

  private long applicationId;

  @Before
  public void setUp() throws Exception {
    SpringTestUtils.get().clearDatabase();
    SpringTestUtils.get().beginTransaction();

    appContext = SpringTestUtils.get().getBeanFactory();
    repository = (ApplicationRepository) appContext.getBean(REPOSITORY);
    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
        .openSession();

    Application app1 = new Application(gadgetUrl1);
    Application app2 = new Application(gadgetUrl2);

    session.saveOrUpdate(app1);
    session.saveOrUpdate(app2);

    applicationId = app1.getId();
  }

  @After public void tearDown() {
    SpringTestUtils.get().endTransaction();
  }

  @Test
  public void testFindAll() {

    List<Application> applications = repository.findAll();

    assertThat(applications, notNullValue());
    assertThat(applications.size(), is(2));
    assertThat(applications.get(0).getTitle(), is("Test title"));
    assertThat(applications.get(1).getTitle(), is("Test title 2"));
  }

  @Test
  public void testFind() {

    Application application = repository.find(applicationId);

    assertThat(application, notNullValue());
    assertThat(application.getTitle(), is("Test title"));
  }
}

