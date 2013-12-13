package com.globant.katari.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class EntityTuplizerFactoryTest {
  @Before
  public void setup() {
    SpringTestUtils.get().clearDatabase();

    SpringTestUtils.get().beginTransaction();

    SessionFactory factory = (SessionFactory) SpringTestUtils.get()
      .getBeanFactory().getBean("katari.sessionFactory");
    Session session = factory.getCurrentSession();

    session.save(new EntityWithParameterInConstructor("1"));

    SpringTestUtils.get().endTransaction();
  }

  @Test
  public void constructBeanDefinedObject() throws Exception {
    SpringTestUtils.get().beginTransaction();

    SessionFactory factory = (SessionFactory) SpringTestUtils.get()
      .getBeanFactory().getBean("katari.sessionFactory");
    Session session = factory.getCurrentSession();

    EntityWithParameterInConstructor withDefaultParameter
      = (EntityWithParameterInConstructor) session.get(
          EntityWithParameterInConstructor.class, 1L);
    assertThat(withDefaultParameter, notNullValue());
    assertThat(withDefaultParameter.getName(), is("parameterName"));
    assertThat(withDefaultParameter.getOneEmbedded().getTransientValue(),
        is("hello there"));

    SpringTestUtils.get().endTransaction();
  }

  @Test
  public void constructBeanDefinedObject2() throws Exception {
    SpringTestUtils.get().beginTransaction();

    SessionFactory factory = (SessionFactory) SpringTestUtils.get()
      .getBeanFactory().getBean("katari.sessionFactory");
    Session session = factory.getCurrentSession();

    EntityWithParameterInConstructor withParameter =
        (EntityWithParameterInConstructor) session.get(
            EntityWithParameterInConstructor.class, 1L);
    assertThat(withParameter, notNullValue());
    assertThat(withParameter.getName(), is("parameterName"));

    SpringTestUtils.get().endTransaction();
  }

}

