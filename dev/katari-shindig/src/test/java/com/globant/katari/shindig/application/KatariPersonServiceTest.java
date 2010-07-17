/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.Future;

import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.UserId.Type;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.globant.katari.shindig.testsupport.SpringTestUtils;

/** @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class KatariPersonServiceTest {

  private KatariPersonService service;
  private Session session;

  @Before
  public void setUp() throws Exception {
    ApplicationContext appContext = SpringTestUtils.getBeanFactory();
    service = (KatariPersonService) appContext.getBean("shindig.personService");
    session = ((SessionFactory) appContext.getBean("katari.sessionFactory"))
      .openSession();
    session.createQuery("delete from CoreUser").executeUpdate();
    session.createSQLQuery("insert into"
        + " users (id, name, user_type)"
        + " values (1, 'admin', 'user')")
        .executeUpdate();
  }

  @Test
  public void testGetPerson() throws Exception {
    FakeGadgetToken token = new FakeGadgetToken();
    token.setViewerId("1");
    UserId me = new UserId(Type.me, "1");
    Future<Person> personFuture;
    personFuture = service.getPerson(me, null, token);
    Person person = personFuture.get();

    assertThat(person.getId(), is("1"));
    assertThat(person.getDisplayName(), is("admin"));
  }

  @Test
  public void testGetPeople() throws Exception {
    FakeGadgetToken token = new FakeGadgetToken();
    token.setViewerId("1");
    Set<UserId> userIds = new HashSet<UserId>();
    UserId me = new UserId(Type.me, "1");
    userIds.add(me);
    CollectionOptions options = new CollectionOptions();
    Future<RestfulCollection<Person>> peopleFuture;
    peopleFuture = service.getPeople(userIds, null, options, null, token);
    List<Person> people = peopleFuture.get().getEntry();

    assertThat(people.size(), is(1));
    assertThat(people.get(0).getId(), is("1"));
    assertThat(people.get(0).getDisplayName(), is("admin"));
  }
}


