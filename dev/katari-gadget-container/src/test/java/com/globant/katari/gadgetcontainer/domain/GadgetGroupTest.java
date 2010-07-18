/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.easymock.classextension.EasyMock.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONObject;

import com.globant.katari.shindig.domain.Application;

/**
 * Test for the bean {@link GadgetGroup}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class GadgetGroupTest {

  private CoreUser user;

  @Before
  public void setUp() {
    user = createNiceMock(CoreUser.class);
    expect(user.getId()).andReturn(1L);
    replay(user);
  }

  @Test
  public void testConstructorOk() {
    GadgetGroup cp = new GadgetGroup(user, "groupName", 1);
    assertThat(cp.getOwner(), is(user));
    assertTrue(cp.getId() == 0);
    assertTrue(cp.getName().equals("groupName"));
  }

  @Test
  public void testConstructorWithNullPageName() {
    try {
      new GadgetGroup(user, null, 1);
      fail("Should be an illegal argument exception because pagename is null");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testConstructorWithEmptyPageName() {
    try {
      new GadgetGroup(user, "", 1);
      fail("Should be an illegal argument exception because pagename is empty");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testAddCanvasInstance() {
    GadgetGroup group = new GadgetGroup(user, "1", 1);
    Application app = new Application("a");
    GadgetInstance instance = new GadgetInstance(app, "1");
    group.addGadget(instance);
    assertTrue(group.getGadgets().contains(instance));
  }

  private class ApplicationSerializer implements JsonSerializer<Application> {
    public JsonElement serialize(Application src, Type typeOfSource,
        JsonSerializationContext context) {
      return new JsonPrimitive(src.getId());
    }
  }

  private class UserSerializer implements JsonSerializer<CoreUser> {
    public JsonElement serialize(CoreUser src, Type typeOfSource,
        JsonSerializationContext context) {
      return new JsonPrimitive(src.getId());
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testToJson() throws Exception {
    GadgetGroup group = new GadgetGroup(user, "dashboard", 2);
    Application app = new Application("trac");
    GadgetInstance instance = new GadgetInstance(app, "1#1");
    group.addGadget(instance);

    JSONObject groupJson = new JSONObject();
    groupJson.put("id", group.getId());
    groupJson.put("name", group.getName());
    groupJson.put("ownerId", group.getOwner().getId());
    groupJson.put("numberOfColumns", group.getNumberOfColumns());
    groupJson.put("viewerId", 150);
    for (GadgetInstance gadget : group.getGadgets()) {
      JSONObject gadgetJson = new JSONObject();
      gadgetJson.put("id", gadget.getId());
      gadgetJson.put("appId", gadget.getApplication().getId());
      gadgetJson.put("position", gadget.getGadgetPosition());
      gadgetJson.put("url", gadget.getApplication().getUrl());
      groupJson.append("gadgets", gadgetJson);
    }
    String sss = groupJson.toString();

    Map<String, Object> map = new HashMap<String, Object>();
    map.remove("class");
    map.remove("owner");
    map.put("ownerId", group.getOwner().getId());

    org.json.JSONObject o1 = new org.json.JSONObject(group);
    /*
        new org.json.JSONObject(group),
        new String[]{"id", "numberOfColumns", "name"});
        */
    String s1 = o1.toString();

    org.json.simple.JSONObject o = new org.json.simple.JSONObject();
    o.put("", group);
    String s2 = o.toJSONString();
    // o.
    // String s2 = o.toJSONString();
    o.putAll(map);
    String json1 = o.toJSONString();

    GsonBuilder b = new GsonBuilder();
    b.registerTypeAdapter(Application.class, new ApplicationSerializer());
    b.setFieldNamingStrategy(new FieldNamingStrategy() {

      public String translateName(Field f) {
        if (f.getName().equals("owner")) {
          return "ownerId";
        } else if (f.getName().equals("application")) {
          return "appId";
        }
        return f.getName();
      }
    });
    Gson gson = b.create();
    String json2 = gson.toJson(group);
  }
}

