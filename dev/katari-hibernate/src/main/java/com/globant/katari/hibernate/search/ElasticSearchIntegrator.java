package com.globant.katari.hibernate.search;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.search.hcore.impl.HibernateSearchIntegrator
  .DuplicationStrategyImpl;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/** Integrates Hibernate Search into Hibernate Core by registering its needed
 * listeners
 *
 * @author waabox
 */
public class ElasticSearchIntegrator implements Integrator {

  /** The full text index event listener.*/
  private ElasticsearchFullTextIndexEventListener listener;

  /** {@inheritDoc}. */
  @Override
  public void integrate(final Configuration configuration,
      final SessionFactoryImplementor sessionFactory,
      final SessionFactoryServiceRegistry serviceRegistry) {

    listener = new ElasticsearchFullTextIndexEventListener();

    EventListenerRegistry registry = serviceRegistry
        .getService(EventListenerRegistry.class);

    DuplicationStrategy duplicationStrategy;
    duplicationStrategy = new DuplicationStrategyImpl(getClass());
    registry.addDuplicationStrategy(duplicationStrategy);

    append(registry, listener, EventType.POST_INSERT);
    append(registry, listener, EventType.POST_UPDATE);
    append(registry, listener, EventType.POST_DELETE);
    append(registry, listener, EventType.POST_COLLECTION_RECREATE);
    append(registry, listener, EventType.POST_COLLECTION_REMOVE);
    append(registry, listener, EventType.POST_COLLECTION_UPDATE);
    append(registry, listener, EventType.FLUSH);

    listener.initialize(configuration);
  }

  /** Append into the registry the given event listener.
   * @param registry the hibernate service registry.
   * @param eventListener the hibernate event listener.
   * @param type the type of event to register.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void append(final EventListenerRegistry registry,
      final ElasticsearchFullTextIndexEventListener eventListener,
      final EventType type) {
    registry.getEventListenerGroup(type).appendListener(eventListener);
  }

  /** {@inheritDoc}. */
  @Override
  public void integrate(final MetadataImplementor metadata,
      final SessionFactoryImplementor sessionFactory,
      final SessionFactoryServiceRegistry serviceRegistry) {
  }

  /** {@inheritDoc}. */
  @Override
  public void disintegrate(final SessionFactoryImplementor sessionFactory,
      final SessionFactoryServiceRegistry serviceRegistry) {
    if ( listener != null ) {
      listener.cleanup();
    }
  }
}
