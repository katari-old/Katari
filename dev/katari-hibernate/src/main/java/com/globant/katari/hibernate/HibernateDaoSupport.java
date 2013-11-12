/**
 *
 */
package com.globant.katari.hibernate;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * This class is a copy of the spring hibernate dao support.
 *
 * @deprecated
 */
public class HibernateDaoSupport implements InitializingBean {

  /** The hibernate session factory. */
  private SessionFactory sessionFactory;

  /**
   * Obtain a Hibernate Session, either from the current transaction or a new
   * one. The latter is only allowed if the
   * {@link org.springframework.orm.hibernate3.HibernateTemplate#setAllowCreate
   * "allowCreate"} setting of this bean's {@link #setHibernateTemplate
   * HibernateTemplate} is "true".
   * <p>
   * <b>Note that this is not meant to be invoked from HibernateTemplate code
   * but rather just in plain Hibernate code.</b> Either rely on a thread-bound
   * Session or use it in combination with {@link #releaseSession}.
   * <p>
   * In general, it is recommended to use HibernateTemplate, either with the
   * provided convenience operations or with a custom HibernateCallback that
   * provides you with a Session to work on. HibernateTemplate will care for all
   * resource management and for proper exception conversion.
   *
   * @return the Hibernate Session
   */
  public Session getSession() {
    return sessionFactory.getCurrentSession();
  }

  /**
   * Return the HibernateTemplate for this DAO, pre-initialized with the
   * SessionFactory or set explicitly.
   * <p>
   * <b>Note: The returned HibernateTemplate is a shared instance.</b> You may
   * introspect its configuration, but not modify the configuration (other than
   * from within an {@link #initDao} implementation). Consider creating a custom
   * HibernateTemplate instance via
   * {@code new HibernateTemplate(getSessionFactory())}, in which case you're
   * allowed to customize the settings on the resulting instance.
   */
  public final HibernateTemplate getHibernateTemplate() {
    return new HibernateTemplate(sessionFactory);
  }

  /**
   * Retrieves the session factory.
   *
   * @return the sessionFactory the session factory, never null.
   */
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * Sets the session factory
   *
   * @param factory
   *          the sessionFactory to set
   */
  public void setSessionFactory(final SessionFactory factory) {
    sessionFactory = factory;
  }

  /** {@inheritDoc}. */
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(sessionFactory, "The session factory cannot be null");
  }

  /**
   * Helper class that simplifies Hibernate data access code.
   */
  public static class HibernateTemplate {

    /** The session factory. */
    private final SessionFactory sessionFactory;

    /** The current session. */
    private final Session session;

    /**
     * Creates a new instance of the hibernate template.
     *
     * @param factory
     *          the session factory.
     */
    private HibernateTemplate(final SessionFactory factory) {
      sessionFactory = factory;
      session = sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final String entityClass, final Serializable id) {
      return (T) session.get(entityClass, id);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> entityClass, final Serializable id) {
      return (T) session.get(entityClass, id);
    }

    @SuppressWarnings("unchecked")
    public <T> T load(final Class<T> entityClass, final Serializable id) {
      return (T) session.load(entityClass, id);
    }

    public Object load(final String entityName, final Serializable id) {
      return session.load(entityName, id);
    }

    public <T> List<T> loadAll(final Class<T> entityClass) {
      Criteria criteria = session.createCriteria(entityClass);
      criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
      return criteria.list();
    }

    public void load(final Object entity, final Serializable id) {
      session.load(entity, id);
    }

    public void refresh(final Object entity) {
      session.refresh(entity);
    }

    public boolean contains(final Object entity) {
      return session.contains(entity);
    }

    public void evict(final Object entity) {
      session.evict(entity);
    }

    public void initialize(final Object proxy) {
     Hibernate.initialize(proxy);
    }

    public Filter enableFilter(final String filterName) {
      Filter filter = session.getEnabledFilter(filterName);
      if (filter == null) {
        filter = session.enableFilter(filterName);
      }
      return filter;
    }

    // -------------------------------------------------------------------------
    // Convenience methods for storing individual objects
    // -------------------------------------------------------------------------

    public void lock(final Object entity, final LockMode lockMode) {
      session.lock(entity, lockMode);
    }

    public void lock(final String entityName, final Object entity,
        final LockMode lockMode) {
      session.lock(entityName, entity, lockMode);
    }

    public Serializable save(final Object entity) {
      return session.save(entity);
    }

    public Serializable save(final String entityName, final Object entity) {
      return session.save(entityName, entity);
    }

    public void update(final Object entity) {
      update(entity, null);
    }

    public void update(final Object entity, final LockMode lockMode) {
      session.update(entity);
      if (lockMode != null) {
        session.lock(entity, lockMode);
      }
    }

    public void update(final String entityName, final Object entity) {
      update(entityName, entity, null);
    }

    public void update(final String entityName, final Object entity,
        final LockMode lockMode) {
      session.update(entityName, entity);
      if (lockMode != null) {
        session.lock(entity, lockMode);
      }
    }

    public void saveOrUpdate(final Object entity) {
      session.saveOrUpdate(entity);
    }

    public void saveOrUpdate(final String entityName, final Object entity) {
      session.saveOrUpdate(entityName, entity);
    }

    public void saveOrUpdateAll(final Collection entities) {
      for (Object entity : entities) {
        session.saveOrUpdate(entity);
      }
    }

    public void replicate(final Object entity,
        final ReplicationMode replicationMode) {
      session.replicate(entity, replicationMode);
    }

    public void replicate(final String entityName, final Object entity,
        final ReplicationMode replicationMode) {
      session.replicate(entityName, entity, replicationMode);
    }

    public void persist(final Object entity) {
      session.persist(entity);
    }

    public void persist(final String entityName, final Object entity) {
      session.persist(entityName, entity);
    }

    public <T> T merge(final T entity) {
      return (T) session.merge(entity);
    }

    public <T> T merge(final String entityName, final T entity) {
      return (T) session.merge(entityName, entity);
    }

    public void delete(final Object entity) {
      delete(entity, null);
    }

    public void delete(final Object entity, final LockMode lockMode) {
      if (lockMode != null) {
        session.lock(entity, lockMode);
      }
      session.delete(entity);
    }

    public void delete(final String entityName, final Object entity) {
      delete(entityName, entity, null);
    }

    public void delete(final String entityName, final Object entity,
        final LockMode lockMode) {
      if (lockMode != null) {
        session.lock(entityName, entity, lockMode);
      }
      session.delete(entityName, entity);
    }

    public void deleteAll(final Collection entities) {
      for (Object entity : entities) {
        session.delete(entity);
      }
    }

    public void flush() {
      session.flush();
    }

    public void clear() {
      session.clear();
    }

    // -------------------------------------------------------------------------
    // Convenience finder methods for HQL strings
    // -------------------------------------------------------------------------

    public List find(final String queryString) {
      return find(queryString, (Object[]) null);
    }

    public List find(final String queryString, final Object value) {
      return find(queryString, new Object[] { value });
    }

    public List find(final String queryString, final Object... values) {
      Query queryObject = session.createQuery(queryString);
      if (values != null) {
        for (int i = 0; i < values.length; i++) {
          queryObject.setParameter(i, values[i]);
        }
      }
      return queryObject.list();
    }

    public List findByNamedParam(final String queryString,
        final String paramName, final Object value) {

      return findByNamedParam(queryString, new String[] { paramName },
          new Object[] { value });
    }

    public List findByNamedParam(final String queryString,
        final String[] paramNames, final Object[] values) {
      if (paramNames.length != values.length) {
        throw new IllegalArgumentException(
            "Length of paramNames array must match length of values array");
      }
      Query queryObject = session.createQuery(queryString);

      if (values != null) {
        for (int i = 0; i < values.length; i++) {
          applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
        }
      }
      return queryObject.list();
    }

    public List findByValueBean(final String queryString,
        final Object valueBean) {
      Query queryObject = session.createQuery(queryString);

      queryObject.setProperties(valueBean);
      return queryObject.list();
    }

    // -------------------------------------------------------------------------
    // Convenience finder methods for named queries
    // -------------------------------------------------------------------------

    public List findByNamedQuery(final String queryName) {
      return findByNamedQuery(queryName, (Object[]) null);
    }

    public List findByNamedQuery(final String queryName, final Object value) {
      return findByNamedQuery(queryName, new Object[] { value });
    }

    public List findByNamedQuery(final String queryName,
        final Object... values) {
      Query queryObject = session.getNamedQuery(queryName);

      if (values != null) {
        for (int i = 0; i < values.length; i++) {
          queryObject.setParameter(i, values[i]);
        }
      }
      return queryObject.list();
    }

    public List findByNamedQueryAndNamedParam(final String queryName,
        final String paramName, final Object value) {
      return findByNamedQueryAndNamedParam(queryName,
          new String[] { paramName }, new Object[] { value });
    }

    public List findByNamedQueryAndNamedParam(final String queryName,
        final String[] paramNames, final Object[] values) {
      if (paramNames != null && values != null
          && paramNames.length != values.length) {
        throw new IllegalArgumentException(
            "Length of paramNames array must match length of values array");
      }
      Query queryObject = session.getNamedQuery(queryName);

      if (values != null) {
        for (int i = 0; i < values.length; i++) {
          applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
        }
      }
      return queryObject.list();
    }

    public List findByNamedQueryAndValueBean(final String queryName,
        final Object valueBean) {
      Query queryObject = session.getNamedQuery(queryName);

      queryObject.setProperties(valueBean);
      return queryObject.list();
    }

    // -------------------------------------------------------------------------
    // Convenience finder methods for detached criteria
    // -------------------------------------------------------------------------

    public List findByCriteria(final DetachedCriteria criteria) {
      return findByCriteria(criteria, -1, -1);
    }

    public List findByCriteria(final DetachedCriteria criteria,
        final int firstResult, final int maxResults) {
      Criteria executableCriteria = criteria.getExecutableCriteria(session);
      if (firstResult >= 0) {
        executableCriteria.setFirstResult(firstResult);
      }
      if (maxResults > 0) {
        executableCriteria.setMaxResults(maxResults);
      }
      return executableCriteria.list();
    }

    public List findByExample(final Object exampleEntity) {
      return findByExample(null, exampleEntity, -1, -1);
    }

    public List findByExample(final String entityName,
        final Object exampleEntity) {
      return findByExample(entityName, exampleEntity, -1, -1);
    }

    public List findByExample(final Object exampleEntity,
        final int firstResult, final int maxResults) {
      return findByExample(null, exampleEntity, firstResult, maxResults);
    }

    public List findByExample(final String entityName,
        final Object exampleEntity, final int firstResult, final int maxResults) {
      Assert.notNull(exampleEntity, "Example entity must not be null");
      Criteria executableCriteria = (entityName != null ? session
          .createCriteria(entityName) : session.createCriteria(exampleEntity
          .getClass()));
      executableCriteria.add(Example.create(exampleEntity));
      if (firstResult >= 0) {
        executableCriteria.setFirstResult(firstResult);
      }
      if (maxResults > 0) {
        executableCriteria.setMaxResults(maxResults);
      }
      return executableCriteria.list();
    }

    // -------------------------------------------------------------------------
    // Convenience query methods for iteration and bulk updates/deletes
    // -------------------------------------------------------------------------

    public Iterator<?> iterate(final String queryString) {
      return iterate(queryString, (Object[]) null);
    }

    public Iterator<?> iterate(final String queryString, final Object value) {
      return iterate(queryString, new Object[] { value });
    }

    public Iterator<?> iterate(final String queryString, final Object... values) {
      Query queryObject = session.createQuery(queryString);

      if (values != null) {
        for (int i = 0; i < values.length; i++) {
          queryObject.setParameter(i, values[i]);
        }
      }
      return queryObject.iterate();
    }

    public void closeIterator(final Iterator it) {
      Hibernate.close(it);
    }

    public int bulkUpdate(final String queryString) {
      return bulkUpdate(queryString, (Object[]) null);
    }

    public int bulkUpdate(final String queryString, final Object value) {
      return bulkUpdate(queryString, new Object[] { value });
    }

    public int bulkUpdate(final String queryString, final Object... values) {
      Query queryObject = session.createQuery(queryString);

      if (values != null) {
        for (int i = 0; i < values.length; i++) {
          queryObject.setParameter(i, values[i]);
        }
      }
      return queryObject.executeUpdate();
    }

    // -------------------------------------------------------------------------
    // Helper methods used by the operations above
    // -------------------------------------------------------------------------

    /**
     * Apply the given name parameter to the given Query object.
     *
     * @param queryObject
     *          the Query object
     * @param paramName
     *          the name of the parameter
     * @param value
     *          the value of the parameter
     * @throws HibernateException
     *           if thrown by the Query object
     */
    protected void applyNamedParameterToQuery(final Query queryObject,
        final String paramName, final Object value) {

      if (value instanceof Collection) {
        queryObject.setParameterList(paramName, (Collection) value);
      } else if (value instanceof Object[]) {
        queryObject.setParameterList(paramName, (Object[]) value);
      } else {
        queryObject.setParameter(paramName, value);
      }
    }

    /**
     * Invocation handler that suppresses close calls on Hibernate Sessions.
     * Also prepares returned Query and Criteria objects.
     *
     * @see org.hibernate.Session#close
     */
    private class CloseSuppressingInvocationHandler implements
        InvocationHandler {

      private final Session target;

      public CloseSuppressingInvocationHandler(final Session target) {
        this.target = target;
      }

      public Object invoke(final Object proxy, final Method method,
          final Object[] args) throws Throwable {
        // Invocation on Session interface coming in...

        if (method.getName().equals("equals")) {
          // Only consider equal when proxies are identical.
          return (proxy == args[0]);
        } else if (method.getName().equals("hashCode")) {
          // Use hashCode of Session proxy.
          return System.identityHashCode(proxy);
        } else if (method.getName().equals("close")) {
          // Handle close method: suppress, not valid.
          return null;
        }

        // Invoke method on target Session.
        try {
          Object retVal = method.invoke(this.target, args);
          // If return value is a Query or Criteria, apply transaction timeout.
          // Applies to createQuery, getNamedQuery, createCriteria.
          return retVal;
        } catch (InvocationTargetException ex) {
          throw ex.getTargetException();
        }
      }
    }
  }

}
