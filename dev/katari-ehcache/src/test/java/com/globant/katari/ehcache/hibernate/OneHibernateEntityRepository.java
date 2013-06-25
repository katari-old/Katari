package com.globant.katari.ehcache.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import com.globant.katari.hibernate.HibernateDaoSupport;

/** Just a repository test purposes.
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class OneHibernateEntityRepository extends HibernateDaoSupport {

  @SuppressWarnings("unchecked")
  public List<OneHibernateEntity> getAll() {
    Criteria criteria = getSession().createCriteria(OneHibernateEntity.class);
    criteria.setCacheable(true);
    return criteria.list();
  }

  public void save(final OneHibernateEntity entity) {
    getHibernateTemplate().saveOrUpdate(entity);
  }

  public OneHibernateEntity get(final long id) {
    return getHibernateTemplate().get(OneHibernateEntity.class, id);
  }


}
