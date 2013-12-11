package com.globant.katari.hibernate;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;

/** Base repository class that holds the session factory.
 * Also, this class provides helper methods that simplifies the Hibernate
 * data access code.
 */
public class HibernateDaoSupport implements InitializingBean {

  /** The hibernate session factory. */
  private SessionFactory sessionFactory;

  /** Obtain a Hibernate's session from the current transaction.
   * This method should be invoked if an active transaction is bounded
   * to the current thread, else, will raise an exception.
   *
   * @return the Hibernate's session
   */
  public Session getSession() {
    return sessionFactory.getCurrentSession();
  }

  /** Retrieves the session factory.
   *
   * @return the sessionFactory the session factory, never null.
   */
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /** Sets the session factory
   *
   * @param factory the sessionFactory to set
   */
  public void setSessionFactory(final SessionFactory factory) {
    sessionFactory = factory;
  }

  /** {@inheritDoc}. */
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(sessionFactory, "The session factory cannot be null");
  }

  /** Execute the specified action assuming that the result object is a List.
   * @param stringQuery the query, cannot be null.
   * @param parameters the query's parameters, can be null.
   * @return the list of objects.
   */
  public List<?> find(final String stringQuery,
      final Object... parameters) {
    Validate.notNull(stringQuery, "The query string cannot be null");
    Query query = getSession().createQuery(stringQuery);
    if (parameters != null) {
      int position = 0;
      for (Object parameter : parameters) {
        query.setParameter(position, parameter);
        position++;
      }
    }
    return query.list();
  }

}
