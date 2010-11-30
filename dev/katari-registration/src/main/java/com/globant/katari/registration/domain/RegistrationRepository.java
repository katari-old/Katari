package com.globant.katari.registration.domain;

import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The repository for all operations related to registration and password
 * recovery.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class RegistrationRepository extends HibernateDaoSupport {

  /** The class logger. */
  private static final Logger LOG = getLogger(RegistrationRepository.class);

  /** Store the forgot password token.
   * @param request the recover password request. Cannot be null.
   */
  public void saveRecoverPasswordRequest(final RecoverPasswordRequest request) {
    Validate.notNull(request, "The RecoverPasswordRequest cannot be null");
    LOG.debug("storing new forgot password token for the user: "
        + request.getUserId());
    getHibernateTemplate().saveOrUpdate(request);
  }

  /** Search the recover password request for the given user and token.
   * @param userId the user id.
   * @param token the token to search. Cannot be null.
   * @return the recover password or null.
   */
  public RecoverPasswordRequest findRecoverPasswordRequest(final long userId,
      final String token) {
    Validate.notNull(token, "The token cannot be null");
    LOG.debug("Searching the recover password request for the user: " + userId);
    Criteria criteria = getSession().createCriteria(
        RecoverPasswordRequest.class);
    criteria.add(Restrictions.eq("token", token));
    criteria.add(Restrictions.eq("userId", userId));
    return (RecoverPasswordRequest) criteria.uniqueResult();
  }
}
