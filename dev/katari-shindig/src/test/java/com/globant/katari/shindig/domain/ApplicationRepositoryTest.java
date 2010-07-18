/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

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

  @Before
  public void setUp() throws Exception {
    ApplicationContext appContext = SpringTestUtils.getBeanFactory();
    repository = (ApplicationRepository) appContext.getBean(REPOSITORY);
    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
        .openSession();
    session.createQuery("delete from Application").executeUpdate();
    Application app = new Application("http://somewhere/something.xml");
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
    assertThat(application.getUrl(), is("http://somewhere/something.xml"));
  }

  @Test
  public void testFindApplicationByUrl() {

    Application application = repository.findApplicationByUrl(
        "http://somewhere/something.xml");

    assertThat(application, notNullValue());
    assertThat(application.getUrl(), is("http://somewhere/something.xml"));
  }
}

