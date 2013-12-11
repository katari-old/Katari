/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Date;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import com.globant.katari.hibernate.HibernateDaoSupport;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import org.apache.commons.lang.Validate;
import org.apache.shindig.common.util.ImmediateFuture;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.model.FilterOperation;
import org.apache.shindig.protocol.model.SortOrder;
import org.apache.shindig.social.opensocial.model.Activity;

import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.shindig.domain.KatariActivity;
import com.globant.katari.shindig.domain.Application;
import com.globant.katari.shindig.domain.ApplicationRepository;

/** An implementation of shindig ActivityService that persists the activities
 * to a database with hibernate.
 *
 * Shindig uses the application url as the appId.
 */
public class KatariActivityService extends HibernateDaoSupport implements
    ActivityService {

  /** The class logger. */
  private static Logger log =
    LoggerFactory.getLogger(KatariActivityService.class);

  /** The application repository.
   *
   * This is never null.
   */
  private final ApplicationRepository applicationRepository;

  /** The news feed application.
   *
   * Gadget id of an application that can read all the activities no matter
   * which application generated it. This is normally used to implement a news
   * feed gadget.
   *
   * It's optional. If null, all applications will only be able to read their
   * own activities.
   */
  private String newsFeedApplicationId;

  /** The filter for the activities, mainly use to implement the Activity's
   * social graph.
   * It's optional. If null, will only show activities for group 'self'.
   */
  private KatariActivityFilter katariActivityFilter;

  /** Constructor, builds a KatariActivityService.
   *
   * @param theApplicationRepository the application repository. It cannot be
   * null.
   */
  public KatariActivityService(final ApplicationRepository
      theApplicationRepository) {
    Validate.notNull(theApplicationRepository, "the application repository"
        + " cannot be null");
    applicationRepository = theApplicationRepository;
  }

  /** {@inheritDoc}
   *
   * TODO: provide a hook to return the other groups: all, friends, groupId and
   * deleted.
   *
   * @param userIds The list of user ids that generated the requested
   * activities. It cannot be null.
   *
   * @param groupId only supports @self, that returns all the activities from
   * all the provided users. It cannot be null.
   *
   * @param appId The application id of activities. It cannot be null.
   *
   * @param fields The list of fields to return. It looks like the spec (1.0)
   * says nothing about this attribute. We ignore it here.
   *
   * @param options Options related to the resulting activities, like
   * filtering, number of returned activities, etc. It returns the
   * activities in the specified order. It cannot be null.
   *
   * @param token the security token of the currently logged on user or
   * application. It cannot be null.
   *
   * @return the found activities, in the specified order. It never returns
   * null.
   */
  @SuppressWarnings("unchecked")
  public Future<RestfulCollection<Activity>> getActivities(
      final Set<UserId> userIds, final GroupId groupId, final String appId,
      final Set<String> fields, final CollectionOptions options,
      final SecurityToken token) {

    log.trace("Entering getActivities");

    Validate.notNull(userIds, "The list of user ids cannot be null.");
    Validate.notNull(groupId, "The group id cannot be null.");
    Validate.notNull(appId, "The app id cannot be null.");
    Validate.notNull(options, "The options cannot be null.");
    Validate.notNull(token, "The security token cannot be null.");
    Validate.isTrue(options.getMax() > 0,
        "You should ask for at least one row.");

    // Workaround: shindig sets this to topFriends if no sort order is
    // specified.
    if ("topFriends".equals(options.getSortBy())) {
      options.setSortBy("id");
      options.setSortOrder(SortOrder.descending);
    }

    Criteria criteria;
    criteria = createCriteriaFor(userIds, groupId, appId, options, token);

    // Obtains the count of activities that matches the search.
    long totalResults = (Long) criteria.setProjection(
        Projections.rowCount()).uniqueResult();

    // Restore the original projection, removing the count.
    criteria.setProjection(null);
    criteria.setResultTransformer(Criteria.ROOT_ENTITY);

    List<Activity> activities;
    if (totalResults > 0) {
      // No need to execute the query if count(*) said 0.
      addOptionsToCriteria(criteria, options);
      activities = criteria.list();
    } else {
      activities = new ArrayList<Activity>();
    }

    log.trace("Leaving getActivities");

    // Possible loss of precision, but we will hopefully never have more than
    // 2^31 activities.
    return ImmediateFuture.newInstance(new RestfulCollection<Activity>(
        activities, options.getFirst(), (int) totalResults,
        options.getMax()));
  }

  /** {@inheritDoc}
   *
   * Returns the activities for a single user. See javadoc for the overloaded
   * getActivity operation.
   */
  @SuppressWarnings("unchecked")
  public Future<RestfulCollection<Activity>> getActivities(final UserId userId,
      final GroupId groupId, final String appId,
      final Set<String> fields, final CollectionOptions options,
      final Set<String> activityIds, final SecurityToken token) {

    log.trace("Entering getActivities");

    Validate.notNull(userId, "The user id cannot be null.");
    Validate.notNull(groupId, "The group id cannot be null.");
    Validate.notNull(appId, "The app id cannot be null.");
    Validate.notNull(options, "The options cannot be null.");
    Validate.notNull(token, "The security token cannot be null.");
    Validate.isTrue(options.getMax() > 0,
        "You should ask for at least one row.");
    Validate.notNull(activityIds, "The list of activity ids cannot be null");

    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(userId);
    Criteria criteria;
    criteria = createCriteriaFor(userIds, groupId, appId, options, token);
    List<Long> activityIdAsLongs = new ArrayList<Long>(activityIds.size());
    for (String id : activityIds) {
      activityIdAsLongs.add(Long.parseLong(id));
    }
    criteria.add(Restrictions.in("id", activityIdAsLongs));

    // Obtains the count of activities that matches the search.
    long totalResults = (Long) criteria.setProjection(
        Projections.rowCount()).uniqueResult();

    // Restore the original projection, removing the count.
    criteria.setProjection(null);
    criteria.setResultTransformer(Criteria.ROOT_ENTITY);

    List<Activity> activities;
    if (totalResults > 0) {
      // No need to execute the query if count(*) said 0.
      addOptionsToCriteria(criteria, options);
      activities = criteria.list();
    } else {
      activities = new ArrayList<Activity>();
    }

    log.trace("Leaving getActivities");

    // Possible loss of precision, but we will hopefully never have more than
    // 2^31 activities.
    return ImmediateFuture.newInstance(new RestfulCollection<Activity>(
        activities, options.getFirst(), (int) totalResults,
        options.getMax()));
  }

  /** {@inheritDoc}
   *
   * This implementation ignores the groupId and the fields parameter.
   */
  public Future<Activity> getActivity(final UserId userId,
      final GroupId groupId, final String appId,
      final Set<String> fields, final String activityId,
      final SecurityToken token) {

    log.trace("Entering getActivity");

    Criteria criteria = getSession().createCriteria(Activity.class);
    criteria.add(Restrictions.eq("id", Long.parseLong(activityId)));

    Application app;
    app = applicationRepository.findApplicationByUrl(appId);
    criteria.add(Restrictions.eq("application", app));

    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(userId);

    addGroupFilterToCriteria(criteria, userIds, groupId, token, appId);

    Activity activity = (Activity) criteria.uniqueResult();

    if (activity == null) {
      throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
          "Activity not found");
    }
    log.trace("Leaving getActivity");
    return ImmediateFuture.newInstance(activity);
  }

  /** {@inheritDoc}
   *
   * This operation is not implemented, throws UnsupportedOperationException.
   */
  public Future<Void> deleteActivities(final UserId userId,
      final GroupId groupId, final String appId,
      final Set<String> activityIds, final SecurityToken token) {

    log.trace("Entering deleteActivity");
    throw new UnsupportedOperationException();
    /*
    log.trace("Leaving deleteActivity");
    return null;
    */
  }

  /** {@inheritDoc}
   *
   * Creates (and persists) a new activity, obtaining the fields from the
   * provided activity.
   *
   * @param fields this parameter is ignored in this implementation.
   *
   * @param token an opaque token that represents the logged in user. It cannot
   * be null.
   *
   * TODO: decide what to do with the groupId parameter, it is not used in this
   * implementation, nor in the sample ActivityServiceDb implementation.
   */
  public Future<Void> createActivity(final UserId userId,
      final GroupId groupId, final String appId, final Set<String> fields,
      final Activity activity, final SecurityToken token) {

    log.trace("Entering createActivity");

    Application application;
    application = applicationRepository.findApplicationByUrl(appId);

    CoreUser user = (CoreUser) applicationRepository.getSessionFactory().getCurrentSession().get(
        CoreUser.class, Long.parseLong(userId.getUserId(token)));

    KatariActivity newActivity = new KatariActivity(new Date().getTime(),
        application, user, activity);
    applicationRepository.getSessionFactory()
      .getCurrentSession().saveOrUpdate(newActivity);

    log.trace("Leaving createActivity");
    return null;
  }

  /** Creates a hibernate criteria for the provided conditions.
   *
   * This operation only considers the conditions that affect the number of
   * returned activities. The criteria is intended to be used to count the
   * number of matching activities and then add the sort conditions.
   *
   * @param userIds The user ids of the activities to search. It cannot be null.
   *
   * @param groupId only supports @self, that returns all the activities from
   * all the provided users. It cannot be null.
   *
   * @param appId The application id of activities. It cannot be null.
   *
   * @param options Options related to the resulting activities, like
   * filtering, etc. The returned criteria only contains the options that affect
   * the number or returned activities.. It cannot be null.
   *
   * @param token an opaque token that represents the logged in user. It cannot
   * be null.
   *
   * @return A criteria that matches the conditions inferred from the
   * parameters.
   */
  private Criteria createCriteriaFor(final Set<UserId> userIds,
      final GroupId groupId, final String appId,
      final CollectionOptions options, final SecurityToken token) {
    log.trace("Entering createCriteriaFor");

    Validate.notNull(userIds, "The list of user ids cannot be null.");
    Validate.notNull(groupId, "The group id cannot be null.");
    Validate.notNull(options, "The options cannot be null.");
    Validate.notNull(token, "The security token cannot be null.");
    Validate.isTrue(options.getMax() > 0,
        "You should ask for at least one row.");

    Criteria criteria = getSession().createCriteria(Activity.class);

    addGroupFilterToCriteria(criteria, userIds, groupId, token, appId);

    if (!appId.equals(newsFeedApplicationId)) {
      Application app;
      app = applicationRepository.findApplicationByUrl(appId);
      criteria.add(Restrictions.eq("application", app));
    }

    if (options.getFilter() != null) {
      if (options.getFilterOperation() == null) {
        throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
          "If you request a filter, you must specify the filter operation.");
      } else if (options.getFilterOperation().equals(FilterOperation.present)) {
        criteria.add(Restrictions.isNotNull(options.getFilter()));
      } else {
        if (options.getFilterValue() == null) {
          throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
          "If you request a filter, you must specify the value.");
        }
        switch (options.getFilterOperation()) {
        case equals:
          criteria.add(Restrictions.eq(options.getFilter(),
              options.getFilterValue()));
          break;
        case startsWith:
          criteria.add(Restrictions.like(options.getFilter(),
              options.getFilterValue(), MatchMode.START));
          break;
        case present:
          criteria.add(Restrictions.like(options.getFilter(),
              options.getFilterValue(), MatchMode.ANYWHERE));
          break;
        default:
          throw new RuntimeException("Unsupported filter operation "
              + options.getFilterOperation());
        }
      }
    }
    log.trace("Leaving createCriteriaFor");
    return criteria;
  }

  /** Adds the group related query conditions to the criteria.
   *
   * @param criteria The criteria to modify. It cannot be null.
   *
   * @param userIds The user ids of the activities to search. It cannot be null.
   *
   * @param groupId only supports @self, that returns all the activities from
   * all the provided users. It cannot be null.
   *
   * @param token an opaque token that represents the logged in user. It cannot
   * be null.
   *
   * TODO: implement the other group types besides @self.
   */
  private void addGroupFilterToCriteria(final Criteria criteria,
      final Set<UserId> userIds, final GroupId groupId,
      final SecurityToken token, final String appId) {

    log.trace("Entering addGroupFilterToCriteria");

    Validate.notNull(criteria, "The criteria cannot be null.");
    Validate.notNull(userIds, "The userIds cannot be null.");
    Validate.notNull(groupId, "The group id cannot be null.");
    Validate.notNull(token, "The token cannot be null.");
    List<Long> userIdList = getUserIdList(userIds, token);
    if (katariActivityFilter != null) {
      katariActivityFilter.resolveSocialGraph(criteria, userIdList, groupId);
    } else {
      switch (groupId.getType()) {
      case self:
        if (userIdList.size() == 1) {
          criteria.createCriteria("user")
            .add(Restrictions.eq("id", userIdList.get(0)));
        } else {
          criteria.createCriteria("user").add(Restrictions.in("id",
              userIdList));
        }
        break;
      default:
        throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
            "Group parameter not supported");
      }
    }
    log.trace("Leaving addGroupFilterToCriteria");
  }

  /** Adds the option to the criteria that are not related to the number of
   * elements that match the search, like sort order and min/max results.
   *
   * @param criteria The criteria to modify. It cannot be null.
   *
   * @param options The options to add to the criteria. The options that are
   * considered here do not change the number activities that match the search
   * conditions. It cannot be null.
   */
  private void addOptionsToCriteria(final Criteria criteria,
      final CollectionOptions options) {

    log.trace("Entering addOptionsToCriteria");

    Validate.notNull(criteria, "The criteria cannot be null.");
    Validate.notNull(options, "The options cannot be null.");

    if (options.getSortBy() != null) {
      if (options.getSortOrder() == null) {
        criteria.addOrder(Order.asc(options.getSortBy()));
      } else {
        switch (options.getSortOrder()) {
        case ascending:
          criteria.addOrder(Order.asc(options.getSortBy()));
          break;
        case descending:
          criteria.addOrder(Order.desc(options.getSortBy()));
          break;
        default:
          throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
            "Unrecognized sort order");
        }
      }
    }
    criteria.setFirstResult(options.getFirst());
    criteria.setMaxResults(options.getMax());

    log.trace("Leaving addOptionsToCriteria");
  }

  /** Returns a list with the id (as long) of each user id.
  *
  * @param userIds the set of user ids. It cannot be null.
  *
  * @param token the security token of the currently logged on user or
  * application. It cannot be null.
  *
  * @return the list of user id, as a list of long. Never returns null.
  */
 private List<Long> getUserIdList(final Set<UserId> userIds,
     final SecurityToken token) {
    Validate.notNull(userIds, "The user ids cannot be null.");
    Validate.notNull(token, "The token cannot be null.");
    List<Long> result = new ArrayList<Long>();
    for (UserId userId : userIds) {
      String uid = userId.getUserId(token);
      if (uid != null) {
        result.add(Long.parseLong(uid));
      }
    }
    return result;
  }

  /** Sets the news feed application id.
   * @param theNewsFeedApplicationId the application id.
   */
  public void setNewsFeedApplicationId(final String theNewsFeedApplicationId) {
    newsFeedApplicationId = theNewsFeedApplicationId;
  }

  /** Sets the Katari's activity filter.
   * @param filter the activity filter.
   */
  public void setKatariActivityFilter(final KatariActivityFilter filter) {
    katariActivityFilter = filter;
  }

}

