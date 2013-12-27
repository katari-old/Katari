package com.globant.katari.hibernate.search;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import org.springframework.beans.BeanUtils;

import org.apache.lucene.document.Document;
import org.hibernate.search.engine.impl.HibernateStatelessInitializer;
import org.hibernate.search.engine.spi.DocumentBuilderIndexedEntity;
import org.hibernate.search.spi.InstanceInitializer;
import org.hibernate.search.backend.AddLuceneWork;
import org.hibernate.search.backend.DeleteLuceneWork;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.bridge.spi.ConversionContext;

import com.globant.katari.hibernate.search.LuceneWorks.LuceneWorkHolder;

/** Holds proxies for the hibernate document builders.*/
public final class DocumentBuilderIndexedEntityProxyFactory {

  /** The instance.*/
  private static final DocumentBuilderIndexedEntityProxyFactory ME;

  /** The cache. */
  private final ConcurrentHashMap<DocumentBuilderIndexedEntity<?>,
    DocumentBuilderIndexedEntity<?>> cache;

  /** The static constructor, initializes the cache.*/
  static {
    ME = new DocumentBuilderIndexedEntityProxyFactory();
  }

  /** Creates a new instance of the factory.*/
  private DocumentBuilderIndexedEntityProxyFactory() {
    cache = new ConcurrentHashMap<DocumentBuilderIndexedEntity<?>,
        DocumentBuilderIndexedEntity<?>>();
  }

  /** Generates a proxy given by the hibernate implementation of the document
   * builder.
   * @param hibernateBuilder the document builder.
   * @return a proxied instance.
   */
  static DocumentBuilderIndexedEntity<?> proxy(
      final DocumentBuilderIndexedEntity<?> hibernateBuilder) {

    if (Enhancer.isEnhanced(hibernateBuilder.getClass())) {
      return hibernateBuilder;
    }

    if (ME.cache.containsKey(hibernateBuilder)) {
      return ME.cache.get(hibernateBuilder);
    }

    Interceptor interceptor = new Interceptor(hibernateBuilder,
        ElasticSearchClientFactory.instance().getIndexFactories());

    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(DocumentBuilderIndexedEntity.class);
    enhancer.setCallbackType(interceptor.getClass());

    Class<?> proxyClass = enhancer.createClass();
    Enhancer.registerCallbacks(proxyClass, new Callback[] {interceptor});

    /* So... since I do not have all the information needed to create a proxy
     * with its original constructor arguments, I bypass it.
     */
    Objenesis objenesis = new ObjenesisStd();
    ObjectInstantiator<?> instantiator;
    instantiator = objenesis.getInstantiatorOf(proxyClass);
    Object proxied = instantiator.newInstance();

    ME.cache.put(hibernateBuilder, (DocumentBuilderIndexedEntity<?>) proxied);

    return (DocumentBuilderIndexedEntity<?>) proxied;
  }

  /** Method interceptor for the add lucene work. */
  private static class Interceptor implements MethodInterceptor {

    /** The delegate object.*/
    private final DocumentBuilderIndexedEntity<?> delegate;

    /** The list of index factories.*/
    private final List<ElasticSearchIndexFactory> indexFactories;

    /** Creates a new instance of the Interceptor.
     * @param hibernateImpl the hibernate implementation.
     * @param factories the list of factories.
     */
    Interceptor(final DocumentBuilderIndexedEntity<?> hibernateImpl,
        final List<ElasticSearchIndexFactory> factories) {
      delegate = hibernateImpl;
      indexFactories = factories;
    }

    /** {@inheritDoc}.
     *
     * The idea of this interceptors is to controll elements within the queue
     * that hibernate provides.
     *
     * Method that we intercept:
     * createAddWork
     * createUpdateWork
     * addWorkToQueue.
     *
     */
    public Object intercept(final Object object, final Method method,
        final Object[] args, final MethodProxy methodProxy) {
      try {

        method.setAccessible(true);
        String methodName = method.getName();

        if ("createAddWork".equals(methodName)
            || "createUpdateWork".equals(methodName)) {

          /* Method signature in DocumentBuilderIndexedEntity
          AddLuceneWork createAddWork(
            Class<T> entityClass,
            T entity,
            Serializable id,
            String idInString,
            InstanceInitializer sessionInitializer,
            ConversionContext conversionContext) {
          */

          LuceneWork work = (LuceneWork) method.invoke(delegate, args);

          Class<?> objectClass = (Class<?>) args[0];
          Object entity = args[1];
          Serializable id = (Serializable) args[2];
          String idInString = (String) args[3];

          ElasticSearchIndexFactory factory;
          factory = search(objectClass, indexFactories);

          if (factory != null) {
            Document document = factory.createDocument(entity, id);
            AddLuceneWork newWork;
            newWork = new AddLuceneWork(id, idInString,
                factory.extractDataType(entity), document);
            LuceneWorkHolder holder;
            holder = new LuceneWorkHolder(newWork, factory.getIndexName());

            if (entity.getClass().isAnnotationPresent(
                IgnoreDefaultHibernateSearchBehaviour.class)) {
              return new AddLuceneWorks(holder);
            } else {
              LuceneWorks works = new AddLuceneWorks(work);
              works.addWork(holder);
              return works;
            }

          }

          return work;

        } else if ("addWorkToQueue".equals(methodName)) {
          /*
          void addWorkToQueue(
            Class<T> entityClass,
            T entity,
            Serializable id,
            boolean delete,
            boolean add,
            List<LuceneWork> queue,
            ConversionContext contextualBridge) {
          */
          String fieldName = null;
          Class<?> objectClass = (Class<?>) args[0];
          Object entity = args[1];
          Serializable id = (Serializable) args[2];
          ConversionContext contextualBridge;

          boolean delete = (Boolean) args[3];
          boolean add = (Boolean) args[4];

          @SuppressWarnings("unchecked")
          List<LuceneWork> queue = (List<LuceneWork>) args[5];

          contextualBridge = (ConversionContext) args[6];
          contextualBridge.pushProperty(fieldName);

          String idInString = contextualBridge
              .setClass(delegate.getBeanClass())
              .twoWayConversionContext(delegate.getIdBridge())
              .objectToString(id);

          contextualBridge.popProperty();

          // Delete
          if (delete && !add) {
            DeleteLuceneWork work;
            work = new DeleteLuceneWork(id, idInString, objectClass);

            DeleteLuceneWorks works = new DeleteLuceneWorks(work);
            ElasticSearchIndexFactory factory;
            factory = search(objectClass, indexFactories);

            if (factory != null) {
              DeleteLuceneWork newWork = new DeleteLuceneWork(id, idInString,
                  factory.extractDataType(entity));
              works.addWork(new LuceneWorkHolder(newWork, factory
                  .getIndexName()));
            }

            queue.add(works);

            return Void.TYPE;

            // Add or Update.
          } else if (add && !delete || add && delete) {
            Method createAddWorkMethod = BeanUtils.findDeclaredMethod(
                DocumentBuilderIndexedEntity.class, "createAddWork",
                new Class[] {
                  Class.class,
                  Object.class,
                  Serializable.class,
                  String.class,
                  InstanceInitializer.class,
                  ConversionContext.class
                });

            queue.add((LuceneWork) createAddWorkMethod.invoke(object,
                objectClass,
                entity,
                id,
                idInString,
                HibernateStatelessInitializer.INSTANCE,
                contextualBridge));

            return Void.TYPE;

          } else {
            return Void.TYPE;
          }

        } else {
          return method.invoke(delegate, args);
        }

      } catch (Exception e) {
        throw new RuntimeException("Cannot execute the proxy method", e);
      }

    }

    /** Search an implementation for the given class.
     * @param entityClass the entity class to search for.
     * @param indexFactories the list of factories.
     * @return the factory or null.
     */
    private static ElasticSearchIndexFactory search(
        final Class<?> entityClass,
        final List<ElasticSearchIndexFactory> indexFactories) {
      for (ElasticSearchIndexFactory factory : indexFactories) {
        if (factory.supports(entityClass)) {
          return factory;
        }
      }
      return null;
    }
  };

}
