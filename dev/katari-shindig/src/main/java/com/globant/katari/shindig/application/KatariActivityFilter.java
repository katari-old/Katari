package com.globant.katari.shindig.application;

import java.util.List;

import org.apache.shindig.social.opensocial.spi.GroupId;
import org.hibernate.Criteria;

/** This interface hooks the
 *  {@link com.globant.katari.shindig.application.KatariActivityService} 
 *  filtering the activities.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public interface KatariActivityFilter {

  /** Configure the Hibernate's Criteria to perform operations over the domain
   * layer to ensure that the Activities matches / belongs to the Social Graph.
   *
   * @param criteria the Hibernate criteria. It's never null.
   * @param userIds the User ids. It's never null.
   * @param groupId the group id. It's never null.
   */
  void resolveSocialGraph(final Criteria criteria, final List<Long> userIds,
      final GroupId groupId);
}
