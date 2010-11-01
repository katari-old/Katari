/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.application;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;

import org.hibernate.Query;

import org.slf4j.Logger;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.globant.katari.shindig.lang.Validate;

import org.apache.shindig.social.core.model.PersonImpl;

/** A very simple (and limited) implementation of PersonService.
 *
 * This is the default katari implementation. It uses katari CoreUser as the
 * storage for the person information. It only supports id and display name for
 * the person. The display name is the CoreUser name.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class KatariPersonService extends HibernateDaoSupport implements
    PersonService {

  /** The class logger. */
  private static Logger log = getLogger(KatariPersonService.class);

  /** {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Future<RestfulCollection<Person>> getPeople(final Set<UserId> userIds,
      final GroupId groupId, final CollectionOptions collectionOptions,
      final Set<String> fields, final SecurityToken token) {

    log.trace("Entering getPeople");

    // We use a query to obtain the list of abstarct CoreUsers.
    Query query;
    query = getSession().createQuery("select count(*) from CoreUser");

    long totalResults = (Long) query.uniqueResult();

    query = getSession().createQuery("select id, name from CoreUser");

    List<Object[]> users = query.list();
    List<Person> people = new LinkedList<Person>();
    for (Object[] user: users) {
      people.add(new PersonImpl(Long.toString((Long) user[0]), (String) user[1],
          null));
    }

    RestfulCollection<Person> collection = new RestfulCollection<Person>(
        people, collectionOptions.getFirst(), (int) totalResults,
        collectionOptions.getMax());

    log.trace("Leaving getPeople");

    return ImmediateFuture.newInstance(collection);
  }

  /**
   * Returns a person that corresponds to the passed in person id.
   *
   * @param id The id of the person to fetch. It cannot be null.
   *
   * @param fields The fields to fetch. This implmentation ignes this
   * parameter.
   *
   * @param token The gadget token.It cannot be null.
   *
   * @return a person.
   */
  public Future<Person> getPerson(final UserId id, final Set<String> fields,
      final SecurityToken token) {
    log.trace("Entering getPerson");

    Validate.notNull(id, "The Id can not be null");
    Validate.notNull(token, "The security Token cannot be null");

    Long personId = new Long(id.getUserId(token));

    // We use a query to obtain the list of abstarct CoreUsers.
    Query query;
    query = getSession().createQuery(
        "select id, name from CoreUser where id = ?");
    query.setParameter(0, personId);

    Object[] user = (Object[]) query.uniqueResult();
    Person person;
    person = new PersonImpl(Long.toString((Long) user[0]), (String) user[1],
          null);

    log.trace("Leaving getPerson");

    return ImmediateFuture.newInstance(person);
  }
}

