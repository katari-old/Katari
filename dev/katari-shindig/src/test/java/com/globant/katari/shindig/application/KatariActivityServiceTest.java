/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.model.SortOrder;
import org.apache.shindig.social.core.model.ActivityImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.globant.katari.shindig.domain.Application;
import com.globant.katari.shindig.domain.KatariActivity;
import com.globant.katari.shindig.domain.SampleUser;
import com.globant.katari.shindig.testsupport.SpringTestUtils;

public class KatariActivityServiceTest {

  private KatariActivityService service;

  private String gadgetXmlUrl = "file:///" + new File(
      "src/test/resources/FullSampleGadget.xml").getAbsolutePath();

  private String otheGadgetXmlUrl = "file:///" + new File(
      "src/test/resources/OtherSampleGadget.xml").getAbsolutePath();

  private String newsFeedGadgetXmlUrl = "file:///" + new File(
      "src/test/resources/NewsFeedSampleGadget.xml").getAbsolutePath();

  // This is the same applicationId but in string format.
  private String appId;

  private String userId1;
  private String userId2;

  private Session session;

  @Before
  public void setUp() {
    service = (KatariActivityService) SpringTestUtils.getBeanFactory().getBean(
        "shindig.activityService");

    session = ((SessionFactory) SpringTestUtils.getBeanFactory()
        .getBean("katari.sessionFactory")).openSession();
    session.createQuery("delete from KatariActivity").executeUpdate();
    // Creates a sample application.
    session.createQuery("delete from Application").executeUpdate();
    Application app = new Application(gadgetXmlUrl);
    session.saveOrUpdate(app);
    appId = gadgetXmlUrl;

    session.saveOrUpdate(new Application(otheGadgetXmlUrl));
    session.saveOrUpdate(new Application(newsFeedGadgetXmlUrl));

    session.createQuery("delete from CoreUser").executeUpdate();
    SampleUser user = new SampleUser("test1");
    session.saveOrUpdate(user);
    userId1 = session.createQuery("select id from CoreUser").uniqueResult()
      .toString();

    user = new SampleUser("test2");
    session.saveOrUpdate(user);
    userId2 = session.createQuery(
        "select id from CoreUser where name = 'test2'").uniqueResult()
        .toString();
  }

  @After
  public void tearDown() {
    session.createQuery("delete from KatariActivity").executeUpdate();
    session.close();
  }

  @Test
  public void testGetActivities_singleActivity() throws Exception {

    createSampleActivity(userId1, "title", appId);
    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(new UserId(UserId.Type.userId, userId1));

    GroupId groupId = new GroupId(GroupId.Type.self, null);

    CollectionOptions options = new CollectionOptions();
    options.setMax(10);

    SecurityToken token = new FakeGadgetToken();

    List<Activity> activities = service.getActivities(
        userIds, groupId, appId, null, options, token).get().getEntry();
    assertThat(activities.size(), is(1));
    assertThat(activities.get(0).getAppId(), is(appId));
    assertThat(activities.get(0).getTitle(), is("title"));
    assertThat(activities.get(0).getUserId(), is(userId1));
  }

  @Test
  public void testGetActivities_paged() throws Exception {
    // Create 20 activities for the same user
    for (int i = 1; i <= 20; i ++) {
      createSampleActivity(userId1, "title-" + i, appId);
    }
    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(new UserId(UserId.Type.userId, userId1));
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    CollectionOptions options = new CollectionOptions();
    options.setMax(10);
    SecurityToken token = new FakeGadgetToken();

    // finds the first 10 activities.
    options.setFirst(0);
    List<Activity> activities = service.getActivities(
        userIds, groupId, appId, null, options, token).get().getEntry();

    assertThat(activities.size(), is(10));
    assertThat(activities.get(0).getAppId(), is(appId));
    assertThat(activities.get(0).getTitle(), is("title-1"));
    assertThat(activities.get(0).getUserId(), is(userId1));
    assertThat(activities.get(9).getTitle(), is("title-10"));

    // finds the last 10 activities.
    options.setFirst(10);
    activities = service.getActivities(
        userIds, groupId, appId, null, options, token)
      .get().getEntry();

    assertThat(activities.size(), is(10));
    assertThat(activities.get(0).getAppId(), is(appId));
    assertThat(activities.get(0).getTitle(), is("title-11"));
    assertThat(activities.get(0).getUserId(), is(userId1));
    assertThat(activities.get(9).getTitle(), is("title-20"));
  }

  @Test
  public void testGetActivities_sorted() throws Exception {
    createSampleActivity(userId1, "title-1", appId);
    createSampleActivity(userId1, "title-2", appId);

    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(new UserId(UserId.Type.userId, userId1));
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    CollectionOptions options = new CollectionOptions();
    // Looks like the default, by looking at shindig sources.
    options.setMax(20);
    SecurityToken token = new FakeGadgetToken();

    List<Activity> activities;

    options.setSortBy("title");
    // Default sort order, should be asc.
    activities = service.getActivities(
        userIds, groupId, appId, null, options, token).get().getEntry();

    assertThat(activities.size(), is(2));
    assertThat(activities.get(0).getTitle(), is("title-1"));
    assertThat(activities.get(1).getTitle(), is("title-2"));

    options.setSortOrder(SortOrder.ascending);
    activities = service.getActivities(
        userIds, groupId, appId, null, options, token).get().getEntry();

    assertThat(activities.size(), is(2));
    assertThat(activities.get(0).getTitle(), is("title-1"));
    assertThat(activities.get(1).getTitle(), is("title-2"));

    options.setSortOrder(SortOrder.descending);
    activities = service.getActivities(
        userIds, groupId, appId, null, options, token).get().getEntry();

    assertThat(activities.size(), is(2));
    assertThat(activities.get(0).getTitle(), is("title-2"));
    assertThat(activities.get(1).getTitle(), is("title-1"));
  }

  @Test
  public void testGetActivities_withActivityId() throws Exception {
    createSampleActivity(userId1, "title-1", appId);
    createSampleActivity(userId1, "title-2", appId);
    createSampleActivity(userId1, "title-3", appId);

    List<?> idList;
    idList = session.createQuery("select id from KatariActivity").list();

    // These are here to make sure that we do not match the wrong user or
    // app-id.
    createSampleActivity(userId1, "title-3", appId);
    createSampleActivity(userId2, "title-3", appId);

    UserId userId = new UserId(UserId.Type.userId, userId1);
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    CollectionOptions options = new CollectionOptions();
    // Looks like the default, by looking at shindig sources.
    options.setMax(20);
    SecurityToken token = new FakeGadgetToken();

    Set<String> activityIds = new HashSet<String>();
    for (Object o: idList) {
      activityIds.add(o.toString());
    }
    List<Activity> activities = service.getActivities(userId, groupId, appId,
        null, options, activityIds, token).get().getEntry();
    assertThat(activities.size(), is(3));
    assertThat(activities.get(0).getTitle(), is("title-1"));
    assertThat(activities.get(1).getTitle(), is("title-2"));
    assertThat(activities.get(2).getTitle(), is("title-3"));
  }

  @Test
  public void testGetActivity() throws Exception {
    createSampleActivity(userId1, "title", appId);
    String id = session.createQuery("select id from KatariActivity")
      .uniqueResult().toString();

    UserId userId = (new UserId(UserId.Type.userId, userId1));
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    // Looks like the default, by looking at shindig sources.
    SecurityToken token = new FakeGadgetToken();

    Activity activity = service.getActivity(userId, groupId, appId,
        null, id, token).get();

    assertThat(activity.getAppId(), is(appId));
    assertThat(activity.getTitle(), is("title"));
    assertThat(activity.getUserId(), is(userId1));
  }

  @Test
  public void testCreateActivity() {
    createSampleActivity(userId1, "title", appId);
    KatariActivity activity = (KatariActivity) session.createQuery(
        "from KatariActivity where title = 'title'").uniqueResult();
    assertThat(activity.getAppId(), is(appId));
    assertThat(activity.getTitle(), is("title"));
    assertThat(activity.getUserId(), is(userId1));
  }

  @Test
  public void testDeleteActivities() {
  }

  @Test
  public void testGetActivities_news_feed() throws Exception {

    service.setNewsFeedApplicationId(newsFeedGadgetXmlUrl);

    createSampleActivity(userId1, "title-1", appId);
    createSampleActivity(userId1, "title-2", otheGadgetXmlUrl);
    createSampleActivity(userId1, "title-3", appId);
    createSampleActivity(userId1, "title-4", otheGadgetXmlUrl);

    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(new UserId(UserId.Type.userId, userId1));
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    // Looks like the default, by looking at shindig sources.
    SecurityToken token = new FakeGadgetToken();
    CollectionOptions options = new CollectionOptions();
    options.setMax(20);
    RestfulCollection<Activity> activitiesCollection;
    activitiesCollection = service.getActivities(userIds, groupId,
        newsFeedGadgetXmlUrl, null, options, token).get();

    List<Activity> activities = activitiesCollection.getEntry();
    assertThat(4, is(activities.size()));
    for(int i=0; i<4; i++) {
      assertThat(activities.get(i).getTitle(), equalTo("title-"+(i+1)));
    }
  }

  @Test
  public void testGetActivities_katariActivyFilter() throws Exception {

    service.setNewsFeedApplicationId(newsFeedGadgetXmlUrl);
    service.setKatariActivityFilter(new KatariActivyFilterTestImpl());

    createSampleActivity(userId1, "title-1", appId);
    createSampleActivity(userId1, "title-2", otheGadgetXmlUrl);
    createSampleActivity(userId1, "title-3", appId);
    createSampleActivity(userId1, "title-4", otheGadgetXmlUrl);

    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(new UserId(UserId.Type.userId, userId1));
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    // Looks like the default, by looking at shindig sources.
    SecurityToken token = new FakeGadgetToken();
    CollectionOptions options = new CollectionOptions();
    options.setMax(20);
    RestfulCollection<Activity> activitiesCollection;
    activitiesCollection = service.getActivities(userIds, groupId,
        newsFeedGadgetXmlUrl, null, options, token).get();

    List<Activity> activities = activitiesCollection.getEntry();
    assertThat(0, is(activities.size()));

  }

  /** Creates a sample activity for the provided user id.
   *
   * @param userId the userId of the sample activity. It cannot be null.
   *
   * @param title the activity title. It cannot be null.
   */
  private void createSampleActivity(final String userId, final String title,
      final String appId) {
    Activity activity = new ActivityImpl();
    activity.setTitle(title);

    service.createActivity(new UserId(UserId.Type.userId, userId),
        new GroupId(GroupId.Type.self, "@self"), appId, null, activity, null);
  }

  class KatariActivyFilterTestImpl implements KatariActivityFilter {
    public void resolveSocialGraph(final Criteria criteria,
        final List<Long> userIds, final GroupId groupId) {
      Long user = userIds.get(0);
      if(user.toString().equals(userId1)) {
        switch (groupId.getType()) {
        case self:
          criteria.createCriteria("user").add(Restrictions.ne("id", 
              userIds.get(0)));
          break;
        default:
          throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST,
              "Group parameter not supported");
        }
      }
    }
  }
}

