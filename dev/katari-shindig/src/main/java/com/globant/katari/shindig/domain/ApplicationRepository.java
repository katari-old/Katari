/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/** The application repository.
 *
 * This repository should probably need to cache the applications.
 */
public class ApplicationRepository extends HibernateDaoSupport {

  /** The class logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(ApplicationRepository.class);

  /** Find the application by id.
   *
   * @param id the id of the application.
   *
   * @return the application found or null.
   */
  public Application findApplication(final long id) {
    log.trace("Entering findApplication with id = {}", new Long(id));

    Application app;
    app = (Application) getHibernateTemplate().get(Application.class, id);

    log.trace("Leaving findApplication");
    return app;
  }

  /** Find the application by url.
  *
  * @param url the url of the application. It cannot be null.
  *
  * @return the application found or null.
  */
  public Application findApplicationByUrl(final String url) {
    Validate.notNull(url, "The url cannot be null.");
    log.trace("Entering findApplicationByUrl('{}')", url);

    Criteria criteria = getSession().createCriteria(Application.class);
    criteria.add(Restrictions.eq("url", url));
    Application app = (Application) criteria.uniqueResult();

    log.trace("Leaving findApplicationByUrl");
    return app;
  }
}

