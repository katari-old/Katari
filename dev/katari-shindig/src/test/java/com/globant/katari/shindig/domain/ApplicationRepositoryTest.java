/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

import com.globant.katari.shindig.testsupport.SpringTestUtils;
import com.globant.katari.shindig.domain.Application;

public class ApplicationRepositoryTest {

  private static final String REPOSITORY = "shindig.applicationRepository";
  private ApplicationRepository repository;
  private Session session;
  private long appId;

  private String gadgetXmlUrl = "file://" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  @Before
  public void setUp() throws Exception {
    ApplicationContext appContext = SpringTestUtils.getBeanFactory();
    repository = (ApplicationRepository) appContext.getBean(REPOSITORY);
    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
        .openSession();
    session.createQuery("delete from Application").executeUpdate();
    Application app = new Application(gadgetXmlUrl);
    session.saveOrUpdate(app);
    appId = app.getId();
  }

  @After
  public void tearDown() {
    session.close();
  }

  @Test
  public void testFindApplication() {

    Application application = repository.findApplication(appId);

    assertThat(application, notNullValue());
    assertThat(application.getUrl(), is(gadgetXmlUrl));
    // The title is obtained from the gadgetXml file.
    assertThat(application.getTitle(), is("Test title"));
  }

  @Test
  public void testFindApplicationByUrl() {

    Application application = repository.findApplicationByUrl(gadgetXmlUrl);

    assertThat(application, notNullValue());
    assertThat(application.getUrl(), is(gadgetXmlUrl));
  }
}

