package com.globant.katari.hibernate.search;

import java.util.Iterator;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.search.SearchException;
import org.hibernate.search.cfg.impl.SearchConfigurationFromHibernateCore;
import org.hibernate.search.engine.impl.MutableEntityIndexBinding;
import org.hibernate.search.engine.spi.DocumentBuilderIndexedEntity;
import org.hibernate.search.event.impl.FullTextIndexEventListener;
import org.hibernate.search.impl.MutableSearchFactory;
import org.hibernate.search.spi.SearchFactoryBuilder;

/** Elasticsearch full text index event listener.
 *
 * We initialize hibernate search from this class because we need to override
 * the default DocumentBuilderIndexedEntity providing special features
 * for those indixes that needs special treatment.
 *
 */
public class ElasticsearchFullTextIndexEventListener extends
    FullTextIndexEventListener {

  /** The serial version.*/
  private static final long serialVersionUID = 1L;

  /** The instalation, it's never null.*/
  private final Installation installation;

  /** Creates a new instance of the listener.
   */
  public ElasticsearchFullTextIndexEventListener() {
    super(Installation.SINGLE_INSTANCE);
    installation = Installation.SINGLE_INSTANCE;
  }

  /** {@inheritDoc}. */
  @Override
  public void initialize(final Configuration cfg) {

    if (installation != Installation.SINGLE_INSTANCE) {
      throw new SearchException(
          "Only Installation.SINGLE_INSTANCE is supported");
    }

    if (searchFactoryImplementor == null) {
      searchFactoryImplementor = createFactory(cfg);
    }

    String indexingStrategy = searchFactoryImplementor.getIndexingStrategy();

    if ("event".equals(indexingStrategy)) {
      used = searchFactoryImplementor.getIndexBindingForEntity().size() != 0;
    } else if ("manual".equals(indexingStrategy)) {
      used = false;
    }

    skipDirtyChecks = !searchFactoryImplementor.isDirtyChecksEnabled();
  }

  /** Creates the session factory, and also overrides the defaults
   * DocumentBuilderIndexedEntity.2
   * @param cfg the hibernate configuration.
   * @return the session factory.
   */
  private ElasticSearchImmutableSearchFactory createFactory(
      final Configuration cfg) {
    try {
      SearchFactoryBuilder builder = new SearchFactoryBuilder();
      builder.configuration(new SearchConfigurationFromHibernateCore(cfg));
      MutableSearchFactory factory;
      factory = (MutableSearchFactory) builder.buildSearchFactory();

      Iterator<PersistentClass> persistenClassesIterator;
      persistenClassesIterator = cfg.getClassMappings();

      while (persistenClassesIterator.hasNext()) {
        PersistentClass persistentClass = persistenClassesIterator.next();

        Class<?> pClass = persistentClass.getMappedClass();
        if (pClass != null) {
          MutableEntityIndexBinding binder = (MutableEntityIndexBinding) factory
              .getIndexBindingForEntity(pClass);
          if (binder != null) {
            DocumentBuilderIndexedEntity<?> delegate = binder
                .getDocumentBuilder();
            if (neeedsProxy(delegate)) {
              DocumentBuilderIndexedEntity<?> proxy;
              proxy = DocumentBuilderIndexedEntityProxyFactory.proxy(delegate);
              /*
               * Ok, I really do not understand why 'yet' why if I do no t call
               * a method just after the proxy has been created, the interceptor
               * dissapear... This is a kind of hack, I have to understand
               * why... [waabox]
               */
              proxy.toString();
              binder.setDocumentBuilderIndexedEntity(proxy);
            }
          }
        }
      }
      return new ElasticSearchImmutableSearchFactory(factory,
          ElasticSearchClientFactory.getClient());
    } catch (Throwable e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  /** Checks if the given object needs to be proxied.
   * @param delegate the delegate to check.
   * @return true if needs to be proxied.
   */
  private boolean neeedsProxy(
      final DocumentBuilderIndexedEntity<?> delegate) {
    List<ElasticSearchIndexFactory> factories;
    factories = ElasticSearchClientFactory.instance().getIndexFactories();
    if (factories == null) {
      return false;
    }
    for (ElasticSearchIndexFactory f : factories) {
      if (f.supports(delegate.getBeanClass())) {
        return true;
      }
    }
    return false;
  }

}
