/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import org.springframework.beans.DirectFieldAccessor;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.hibernate.coreuser.domain.CoreUserRepository;

import com.globant.katari.shindig.domain.Application;

import com.globant.katari.gadgetcontainer.application.TokenService;
import com.globant.katari.gadgetcontainer.domain.SampleUser;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.CustomizableGadgetGroup;
import com.globant.katari.gadgetcontainer.domain.SharedGadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupTemplate;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

public class GadgetGroupCommandTest {

  private String gadgetXmlUrl = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private CoreUser viewer = new SampleUser("me");

  private CoreUser owner = new SampleUser("him");

  private TokenService sameViewerAndOwnerToken;

  private TokenService differentViewerAndOwnerToken;

  private String groupName = "theGroup";

  private CustomizableGadgetGroup customizableGroup;

  private SharedGadgetGroup sharedGroup;

  private GadgetGroupTemplate templateGroup;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    new DirectFieldAccessor(viewer).setPropertyValue("id", 1);
    new DirectFieldAccessor(owner).setPropertyValue("id", 2);

    sameViewerAndOwnerToken = createMock(TokenService.class);
    expect(sameViewerAndOwnerToken.createSecurityToken(eq(2L), eq(2L),
        isA(GadgetInstance.class))).andReturn("mockToken").anyTimes();
    replay(sameViewerAndOwnerToken);

    differentViewerAndOwnerToken = createMock(TokenService.class);
    expect(differentViewerAndOwnerToken.createSecurityToken(eq(1L), eq(2L),
        isA(GadgetInstance.class))).andReturn("mockToken").anyTimes();
    replay(differentViewerAndOwnerToken);

    Application application = new Application(gadgetXmlUrl);

    customizableGroup = new CustomizableGadgetGroup(owner, groupName, "default", 3);
    GadgetInstance gadgetInstance = new GadgetInstance(application, 1, 2);
    customizableGroup.add(gadgetInstance);

    sharedGroup = new SharedGadgetGroup(groupName, "default", 3);
    gadgetInstance = new GadgetInstance(application, 1, 2);
    ((Set<GadgetInstance>) new DirectFieldAccessor(sharedGroup)
      .getPropertyValue("gadgets")).add(gadgetInstance);

    templateGroup = new GadgetGroupTemplate(groupName, "default", 3);
    gadgetInstance = new GadgetInstance(application, 1, 2);
    ((Set<GadgetInstance>) new DirectFieldAccessor(templateGroup)
      .getPropertyValue("gadgets")).add(gadgetInstance);
  }

  @Test
  public void testExecute_nullGroup() {
    GadgetGroupCommand command = new GadgetGroupCommand(
        createMock(CoreUserRepository.class),
        createMock(GadgetGroupRepository.class),
        createMock(ContextUserService.class),
        createMock(TokenService.class), null);
    try {
      command.execute();
      fail("should fail because we never set the groupName command property");
    } catch (Exception e) {
    }
  }

  @Test
  public void testExecute() throws Exception {

    CoreUserRepository userRepository = createMock(CoreUserRepository.class);
    replay(userRepository);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(owner.getId(), groupName))
      .andReturn(customizableGroup);
    replay(repository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUser()).andReturn(owner);
    replay(userService);

    GadgetGroupCommand command = new GadgetGroupCommand(userRepository,
        repository, userService, sameViewerAndOwnerToken, null);
    command.setGroupName(groupName);

    assertThat(command.execute().write(new StringWriter()).toString(),
        is(baselineJson(true, false, owner.getId())));

    verify(userService);
    verify(repository);
  }

  @Test
  public void testExecute_viewOther() throws Exception {

    String groupName = "theGroup";

    CoreUserRepository userRepository = createMock(CoreUserRepository.class);
    replay(userRepository);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(owner.getId(), groupName))
      .andReturn(customizableGroup);
    replay(repository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUser()).andReturn(viewer);
    replay(userService);

    GadgetGroupCommand command = new GadgetGroupCommand(userRepository,
        repository, userService, differentViewerAndOwnerToken,
        new ViewerOwnerRestriction() {
          public boolean canView(final GadgetGroup group, final long ownerId,
            final long viewerId) {
            return true;
          }
        });
    command.setGroupName(groupName);
    command.setOwnerId(owner.getId());

    assertThat(command.execute().write(new StringWriter()).toString(),
        is(baselineJson(false, false, viewer.getId())));

    verify(userService);
    verify(repository);
  }

  @Test
  public void testExecute_viewOtherDefault() throws Exception {

    String groupName = "theGroup";

    CoreUserRepository userRepository = createMock(CoreUserRepository.class);
    replay(userRepository);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(owner.getId(), groupName))
      .andReturn(customizableGroup);
    replay(repository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUser()).andReturn(viewer);
    replay(userService);

    GadgetGroupCommand command = new GadgetGroupCommand(userRepository,
        repository, userService, differentViewerAndOwnerToken,
        new ViewerOwnerRestriction() {
          public boolean canView(final GadgetGroup group, final long ownerId,
            final long viewerId) {
            return false;
          }
        });
    command.setGroupName(groupName);
    command.setOwnerId(owner.getId());

    try {
      command.execute();
      fail("should fail because we cannot see the group.");
    } catch (Exception e) {
    }
  }

  @Test
  public void testExecute_viewOtherNotViewable() throws Exception {

    String groupName = "theGroup";

    CoreUserRepository userRepository = createMock(CoreUserRepository.class);
    replay(userRepository);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(owner.getId(), groupName))
      .andReturn(customizableGroup);
    replay(repository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUser()).andReturn(viewer);
    replay(userService);

    GadgetGroupCommand command = new GadgetGroupCommand(userRepository,
        repository, userService, differentViewerAndOwnerToken, null);
    command.setGroupName(groupName);
    command.setOwnerId(owner.getId());

    try {
      command.execute();
      fail("should fail because we cannot see the group.");
    } catch (Exception e) {
    }
  }

  @Test
  public void testExecute_viewOtherFromTemplate() throws Exception {

    String groupName = "theGroup";

    CoreUserRepository userRepository = createMock(CoreUserRepository.class);
    expect(userRepository.findUser(2)).andReturn(owner);
    replay(userRepository);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(2, groupName)).andReturn(null);
    expect(repository.findGadgetGroupTemplate(groupName))
      .andReturn(templateGroup);
    repository.save(isA(GadgetGroup.class));
    replay(repository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUser()).andReturn(viewer);
    replay(userService);

    GadgetGroupCommand command = new GadgetGroupCommand(userRepository,
        repository, userService, differentViewerAndOwnerToken,
        new ViewerOwnerRestriction() {
          public boolean canView(final GadgetGroup group, final long ownerId,
            final long viewerId) {
            return true;
          }
        });
    command.setGroupName(groupName);
    command.setOwnerId(2);

    assertThat(command.execute().write(new StringWriter()).toString(),
        is(baselineJson(false, false, 1)));

    verify(userService);
    verify(repository);
  }

  @Test
  public void testExecute_staticGroup() throws Exception {
    String groupName = "theGroup";

    CoreUserRepository userRepository = createMock(CoreUserRepository.class);
    replay(userRepository);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(owner.getId(), groupName))
      .andReturn(sharedGroup);
    replay(repository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUser()).andReturn(owner);
    replay(userService);

    GadgetGroupCommand command = new GadgetGroupCommand(userRepository,
        repository, userService, sameViewerAndOwnerToken, null);
    command.setGroupName(groupName);

    assertThat(command.execute().write(new StringWriter()).toString(),
        is(baselineJson(false, false, owner.getId())));

    verify(userService);
    verify(repository);
  }

  @Test
  public void testExecute_createFromTemplate() throws Exception {
    String groupName = "theGroup";

    CoreUserRepository userRepository = createMock(CoreUserRepository.class);
    replay(userRepository);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(owner.getId(), groupName))
      .andReturn(null);
    expect(repository.findGadgetGroupTemplate(groupName))
      .andReturn(templateGroup);
    repository.save(isA(GadgetGroup.class));
    replay(repository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUser()).andReturn(owner);
    replay(userService);

    GadgetGroupCommand command = new GadgetGroupCommand(userRepository,
        repository, userService, sameViewerAndOwnerToken, null);
    command.setGroupName(groupName);

    assertThat(command.execute().write(new StringWriter()).toString(),
        is(baselineJson(true, false, owner.getId())));

    verify(userService);
    verify(repository);
  }

  @Test
  public void testExecute_noGadgets() throws Exception {
    String groupName = "theGroup";

    SharedGadgetGroup gadgetGroup;
    gadgetGroup = new SharedGadgetGroup(groupName, "default", 3);

    CoreUserRepository userRepository = createMock(CoreUserRepository.class);
    replay(userRepository);

    GadgetGroupRepository repository = createMock(GadgetGroupRepository.class);
    expect(repository.findGadgetGroup(owner.getId(), groupName))
      .andReturn(gadgetGroup);
    replay(repository);

    ContextUserService userService = createMock(ContextUserService.class);
    expect(userService.getCurrentUser()).andReturn(owner);
    replay(userService);

    GadgetGroupCommand command = new GadgetGroupCommand(userRepository,
        repository, userService, sameViewerAndOwnerToken, null);
    command.setGroupName(groupName);

    assertThat(command.execute().write(new StringWriter()).toString(),
        is(baselineJson(false, true, owner.getId())));

    verify(userService);
    verify(repository);
  }

  /** Creates the baseline json string, a string with a sample json object.
   *
   * @return the json string.
   *
   * @throws JSONException
   */
  private String baselineJson(final boolean isCustomizable,
      final boolean noGadgets, final long viewerId)
    throws JSONException {
    try {
      JSONObject groupJson = new JSONObject();
      groupJson.put("id", 0);
      groupJson.put("name", "theGroup");
      groupJson.put("ownerId", 2);
      groupJson.put("viewerId", viewerId);
      groupJson.put("view", "default");
      groupJson.put("numberOfColumns", 3);
      groupJson.put("customizable", isCustomizable);
      groupJson.put("gadgets", new JSONArray());

      if (!noGadgets) {
        JSONObject gadgetJson = new JSONObject();
        gadgetJson.put("id", 0);
        gadgetJson.put("title", "Test title");
        gadgetJson.put("appId", 0);
        gadgetJson.put("column", 1);
        gadgetJson.put("order", 2);
        gadgetJson.put("url", gadgetXmlUrl);
        gadgetJson.put("icon", "");
        gadgetJson.put("securityToken", "mockToken");
        groupJson.append("gadgets", gadgetJson);
      }
      return groupJson.toString();
    } catch(JSONException e) {
      throw new RuntimeException("Error generating json", e);
    }
  }
}

