/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.globant.katari.shindig.domain.Application;

/** Repository for shindig applications.
 */
public class ApplicationRepository extends HibernateDaoSupport {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      ApplicationRepository.class);

  /** Returns the application for the provided id.
   *
   * @param id the application id.
   *
   * @return The application with the provided id, null if not found.
   */
  public Application find(final long id) {
    return (Application) getHibernateTemplate().get(Application.class, id);
  }

  /** Returns all registered applications, ordered by title.
   *
   * This operation will eventually disappear when we provide pagination and
   * filtering of the applications.
   *
   * @return a list with all the registered applications. Never returns null.
   */
  @SuppressWarnings("unchecked")
  public List<Application> findAll() {

    log.trace("Entering findAll");

    List<Application> applications;
    applications = getHibernateTemplate().find(
        "from Application order by title");

    log.trace("Leaving findAll");
    return applications;
  }
}

