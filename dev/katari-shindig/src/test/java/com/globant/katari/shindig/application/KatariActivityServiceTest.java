/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.shindig.domain.KatariActivity;
import com.globant.katari.shindig.testsupport.SpringTestUtils;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.protocol.model.SortOrder;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.GroupId;

import org.apache.shindig.social.opensocial.model.Activity;

import org.apache.shindig.social.core.model.ActivityImpl;

public class KatariActivityServiceTest {

  private KatariActivityService service;
  
  Session session;

  @Before
  public void setUp() {
    service = (KatariActivityService) SpringTestUtils.getBeanFactory().getBean(
        "shindig.activityService");

    session = ((SessionFactory) SpringTestUtils.getBeanFactory()
        .getBean("katari.sessionFactory")).openSession();
    session.createQuery("delete from KatariActivity").executeUpdate();
  }
  
  @After
  public void tearDown() {
    session.close();
  }

  @Test
  public void testGetActivities_singleActivity() throws Exception {
    
    createSampleActivity("1", "app-id", "body");
    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(new UserId(UserId.Type.userId, "1"));
    
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    
    CollectionOptions options = new CollectionOptions();
    options.setMax(10);
    
    SecurityToken token = new FakeGadgetToken();

    List<Activity> activities = service.getActivities(
        userIds, groupId, "app-id", null, options, token).get().getEntry();
    assertThat(activities.size(), is(1));
    assertThat(activities.get(0).getAppId(), is("app-id"));
    assertThat(activities.get(0).getBody(), is("body"));
    assertThat(activities.get(0).getUserId(), is("1"));
  }
  
  @Test
  public void testGetActivities_paged() throws Exception {
    // Create 20 activities for the same user
    for (int i = 1; i <= 20; i ++) {
      createSampleActivity("1", "app-id", "body-" + i);
    }
    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(new UserId(UserId.Type.userId, "1"));
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    CollectionOptions options = new CollectionOptions();
    options.setMax(10);
    SecurityToken token = new FakeGadgetToken();

    // finds the first 10 activities.
    options.setFirst(0);
    List<Activity> activities = service.getActivities(
        userIds, groupId, "app-id", null, options, token).get().getEntry();

    assertThat(activities.size(), is(10));
    assertThat(activities.get(0).getAppId(), is("app-id"));
    assertThat(activities.get(0).getBody(), is("body-1"));
    assertThat(activities.get(0).getUserId(), is("1"));
    assertThat(activities.get(9).getBody(), is("body-10"));

    // finds the last 10 activities.
    options.setFirst(10);
    activities = service.getActivities(
        userIds, groupId, "app-id", null, options, token).get().getEntry();

    assertThat(activities.size(), is(10));
    assertThat(activities.get(0).getAppId(), is("app-id"));
    assertThat(activities.get(0).getBody(), is("body-11"));
    assertThat(activities.get(0).getUserId(), is("1"));
    assertThat(activities.get(9).getBody(), is("body-20"));
  }

  @Test
  public void testGetActivities_sorted() throws Exception {
    createSampleActivity("1", "app-id", "body-1");
    createSampleActivity("1", "app-id", "body-2");
    
    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(new UserId(UserId.Type.userId, "1"));    
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    CollectionOptions options = new CollectionOptions();
    // Looks like the default, by looking at shindig sources.
    options.setMax(20);
    SecurityToken token = new FakeGadgetToken();
    
    List<Activity> activities;

    options.setSortBy("body");
    // Default sort order, should be asc.
    activities = service.getActivities(
        userIds, groupId, "app-id", null, options, token).get().getEntry();

    assertThat(activities.size(), is(2));
    assertThat(activities.get(0).getBody(), is("body-1"));
    assertThat(activities.get(1).getBody(), is("body-2"));

    options.setSortOrder(SortOrder.ascending);
    activities = service.getActivities(
        userIds, groupId, "app-id", null, options, token).get().getEntry();

    assertThat(activities.size(), is(2));
    assertThat(activities.get(0).getBody(), is("body-1"));
    assertThat(activities.get(1).getBody(), is("body-2"));
    
    options.setSortOrder(SortOrder.descending);
    activities = service.getActivities(
        userIds, groupId, "app-id", null, options, token).get().getEntry();

    assertThat(activities.size(), is(2));
    assertThat(activities.get(0).getBody(), is("body-2"));
    assertThat(activities.get(1).getBody(), is("body-1"));
  }
  
  @Test
  public void testGetActivities_withActivityId() throws Exception {
    createSampleActivity("1", "app-id", "body-1");
    createSampleActivity("1", "app-id", "body-2");
    createSampleActivity("1", "app-id", "body-3");
    
    List<?> idList = session.createQuery("select id from KatariActivity")
      .list();

    // These are here to make sure that we do not match the wrong user or
    // app-id.
    createSampleActivity("1", "app-id-2", "body-3");
    createSampleActivity("2", "app-id", "body-3");
    
    UserId userId = new UserId(UserId.Type.userId, "1");    
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    CollectionOptions options = new CollectionOptions();
    // Looks like the default, by looking at shindig sources.
    options.setMax(20);
    SecurityToken token = new FakeGadgetToken();

    Set<String> activityIds = new HashSet<String>();
    for (Object o: idList) {
      activityIds.add(o.toString());
    }
    List<Activity> activities = service.getActivities(userId, groupId, "app-id",
        null, options, activityIds, token).get().getEntry();
    assertThat(activities.size(), is(3));
    assertThat(activities.get(0).getBody(), is("body-1"));
    assertThat(activities.get(1).getBody(), is("body-2"));
    assertThat(activities.get(2).getBody(), is("body-3"));
  }
  
  @Test
  public void testGetActivity() throws Exception {
    createSampleActivity("1", "app-id", "body");
    String id = session.createQuery("select id from KatariActivity")
      .uniqueResult().toString();

    UserId userId = (new UserId(UserId.Type.userId, "1"));    
    GroupId groupId = new GroupId(GroupId.Type.self, null);
    // Looks like the default, by looking at shindig sources.
    SecurityToken token = new FakeGadgetToken();

    Activity activity = service.getActivity(userId, groupId, "app-id",
        null, id, token).get();
    
    assertThat(activity.getAppId(), is("app-id"));
    assertThat(activity.getBody(), is("body"));
    assertThat(activity.getUserId(), is("1"));
  }

  @Test
  public void testCreateActivity() {
    createSampleActivity("1", "app-id", "body");
    KatariActivity activity = (KatariActivity) session.createQuery(
        "from KatariActivity where body = 'body'").uniqueResult();
    assertThat(activity.getAppId(), is("app-id"));
    assertThat(activity.getBody(), is("body"));
    assertThat(activity.getUserId(), is("1"));
  }

  @Test
  public void testDeleteActivities() {
  }
  
  /** Creates a sample activity for the userid 1.
   */
  private void createSampleActivity(final String userId, final String appId,
      final String body) {
    Activity activity = new ActivityImpl();
    activity.setAppId(appId);
    activity.setBody(body);

    service.createActivity(new UserId(UserId.Type.userId, userId),
        new GroupId(GroupId.Type.self, "@self"), appId, null, activity, null);
  }
}

