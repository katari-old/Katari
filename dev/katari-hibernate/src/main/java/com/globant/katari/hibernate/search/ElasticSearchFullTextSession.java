package com.globant.katari.hibernate.search;

import org.apache.commons.lang.Validate;
import org.apache.lucene.search.Query;

import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.impl.FullTextSessionImpl;

/** Elastic search Hibernate client.*/
public class ElasticSearchFullTextSession extends FullTextSessionImpl {

  /** The serial version.*/
  private static final long serialVersionUID = 1L;

  /** The hibernate session, it's never null.*/
  private final Session session;

  /** The search factory implementor, it's never null.*/
  private final SearchFactoryImplementor searchFactory;

  /** The search session factory, it's never null. */
  private final SearchSessionFactory searchSessionFactory;

  /** Creates the Elastic search full text session.
   * @param hibernateSession the current hibernate session, cannot be null.
   * @param factory the search session factory, cannot be null.
   */
  public ElasticSearchFullTextSession(final Session hibernateSession,
      final SearchSessionFactory factory) {
    super(hibernateSession);
    Validate.notNull(hibernateSession, "The hibernate session cannot be null");
    Validate.notNull(factory, "The hibernate search factory cannot be null");
    session = hibernateSession;
    searchFactory = factory.getSearchFactory(session);
    searchSessionFactory = factory;
  }

  /** {@inheritDoc}.
   * Retrieves our search factory implementor, just because this class
   * creates the HSQuery.
   */
  @Override
  public SearchFactory getSearchFactory() {
    return searchFactory;
  }

  /** {@inheritDoc}.*/
  @SuppressWarnings("rawtypes")
  public FullTextQuery createFullTextQuery(final Query query,
      final Class... entities) {
    return new ElasticSearchFullTextQuery(query, entities,
        (SessionImplementor) session, searchSessionFactory);
  }
}
