/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.JsonRepresentation;

import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupTemplate;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;
import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.hibernate.coreuser.domain.CoreUserRepository;

/** Looks for a gadget group by name.
 *
 * Call setGroupName() and optionally setOwnerId() before executing this
 * command. If you pass an owner id, this command returns the gadget group of
 * this user. If you do not call setOwnerId, this command returns the gadget
 * group of the currently logged on user.
 *
 * It generates the json representation for the gadget group.
 */
public class GadgetGroupCommand implements Command<JsonRepresentation> {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(GadgetGroupCommand.class);

  /** The repository for gadget groups.
   *
   * This is never null.
   */
  private final GadgetGroupRepository gadgetGroupRepository;

  /** The repository for users.
   *
   * This is never null.
   */
  private final CoreUserRepository userRepository;

  /** Service used to obtain the currently logged on user.
   *
   * This is never null.
   */
  private final ContextUserService userService;

  /** The open social token service implementation.
   *
   * This is never null.
   */
  private final TokenService tokenService;

  /** Decides if a user can see a group he does not own.
   *
   * If null, users will only see gadget groups that they own.
   */
  private ViewerOwnerRestriction viewerOwnerRestriction = null;

  /** The name of the gadget group to search, as provided by the user.
   */
  private String groupName;

  /** The id of the owner of the gadget group to search.
   *
   * If this is not specified (0), the command will return the gadget group for
   * the currently logged on user.
   */
  private long requestedOwnerId = 0;

  /** Constructor.
   *
   * @param theUserRepository Cannot be null.
   *
   * @param theGroupRepository Cannot be null.
   *
   * @param theUserService Cannot be null.
   *
   * @param theTokenService Cannot be null.
   *
   * @param restriction decides if the user can see a gadget group that he
   * doesn't own.  If null, users will only see their own gadget groups.
   */
  public GadgetGroupCommand(final CoreUserRepository theUserRepository,
      final GadgetGroupRepository theGroupRepository,
      final ContextUserService theUserService,
      final TokenService theTokenService,
      final ViewerOwnerRestriction restriction) {

    Validate.notNull(theUserRepository, "user repository cannot be null");
    Validate.notNull(theGroupRepository, "gadget repository cannot be null");
    Validate.notNull(theUserService, "user service cannot be null");
    Validate.notNull(theTokenService, "token service cannot be null");

    userRepository = theUserRepository;
    gadgetGroupRepository = theGroupRepository;
    userService = theUserService;
    tokenService = theTokenService;
    viewerOwnerRestriction = restriction;
  }

  /** The name of the group to search for, as provided by the user.
   *
   * @param name the groupName to set. It must be called with a non empty
   * string before calling execute.
   */
  public void setGroupName(final String name) {
    groupName = name;
  }

  /** The id of the user that owns the group to search for.
   *
   * @param id the user id. It cannot be 0.
   */
  public void setOwnerId(final long id) {
    Validate.isTrue(id != 0, "The owner id cannot be 0.");
    requestedOwnerId = id;
  }

  /** Find the group with the given group name for the currently logged in
   * user.
   *
   * Call setGroupName with a non empty string before calling execute.
   *
   * The json structure is:
   *
   * <pre>
   * {
   *   "id":&lt;long&gt;,
   *   "name":"&lt;string&gt;"
   *   "ownerId":&lt;long&gt;,
   *   "viewerId":&lt;long&gt;,
   *   "view":"&lt;string&gt;",
   *   "numberOfColumns":&lt;int&gt;,
   *   "customizable":&lt;true|false&gt;,
   *   "gadgets":[
   *     {
   *       "id":&lt;long&gt;,
   *       "title":&lt;string&gt;,
   *       "appId":&lt;long&gt;,
   *       "column":&lt;int&gt;,
   *       "order":&lt;int&gt;,
   *       "securityToken":"token"
   *       "url":"url"
   *     }
   *   ],
   * }
   * </pre>
   *
   * If the gadget was not found, it returns an empty json object ({}).
   *
   * @return a json object, never returns null.
   */
  public JsonRepresentation execute() {
    log.trace("Entering execute");

    Validate.notEmpty(groupName, "groupName cannot be blank");

    // The viewer of the group. It can be an anonymous user, so getCurrentUser
    // may return null.
    CoreUser viewer = userService.getCurrentUser();
    long viewerId = 0;
    if (viewer != null) {
      viewerId = viewer.getId();
    }

    long ownerId = viewerId;
    if (requestedOwnerId != 0) {
      ownerId = requestedOwnerId;
    }

    log.debug("Searching group = " + groupName + " for the user:" + ownerId);
    GadgetGroup group;
    group = gadgetGroupRepository.findGadgetGroup(ownerId, groupName);

    if (group == null) {
      // Group not found, it could be because the user id is invalid.
      CoreUser owner = null;
      if (requestedOwnerId != 0) {
        owner = userRepository.findUser(requestedOwnerId);
        if (owner == null) {
          throw new RuntimeException("Owner not found: " + requestedOwnerId);
        }
      } else {
        owner = viewer;
      }

      GadgetGroupTemplate templ;
      templ = gadgetGroupRepository.findGadgetGroupTemplate(groupName);
      if (templ == null) {
        throw new RuntimeException("Group not found " + groupName);
      }

      group = templ.createFromTemplate(owner);
      gadgetGroupRepository.save(group);
    }

    if (requestedOwnerId != 0 && requestedOwnerId != viewerId) {
      // The user is trying to see a group he does not own.
      if (viewerOwnerRestriction == null) {
        throw new RuntimeException("Group is not accesible by user.");
      }
      if (!viewerOwnerRestriction.canView(group, requestedOwnerId, viewerId)) {
        throw new RuntimeException("Group is not accesible by user.");
      }
    }

    // The gadget group is never null here, it was found or created from a
    // template.
    JsonRepresentation result = null;
    try {
      result = new JsonRepresentation(toJson(viewerId, ownerId, group));
    } catch (JSONException e) {
      throw new RuntimeException("Error serializing to json", e);
    }
    log.trace("Leaving execute");
    return result;
  }

  /** Generates the json representation of the provided gadget group.
   *
   * @param viewerId The id of the user making the request.
   *
   * @param ownerId The id of the user that owns the gadget group. We cannot
   * use the gadget group owner because the group can be a shared one, where
   * the owner is 0.
   *
   * @param group The group to convert to json. It cannot be null.
   *
   * @return a json object that represents the gadget group. See javadoc of
   * execute for the json structure.
   *
   * @throws JSONException when the json object could not be generated.
   */
  private JSONObject toJson(final long viewerId, final long ownerId,
      final GadgetGroup group) throws JSONException {
    Validate.notNull(group, "The gadget group cannot be null.");

    JSONObject groupJson = new JSONObject();

    boolean isCustomizable = group.isCustomizable();
    if (viewerId != ownerId || viewerId == 0) {
      isCustomizable = false;
    }

    groupJson = new JSONObject();
    groupJson.put("id", group.getId());
    groupJson.put("name", group.getName());
    groupJson.put("ownerId", ownerId);
    groupJson.put("viewerId", viewerId);
    groupJson.put("view", group.getView());
    groupJson.put("numberOfColumns", group.getNumberOfColumns());
    groupJson.put("customizable", isCustomizable);

    JSONArray gadgets = new JSONArray();
    for (GadgetInstance gadget : group.getGadgets()) {
      JSONObject gadgetJson = new JSONObject();
      gadgetJson.put("id", gadget.getId());
      gadgetJson.put("title", gadget.getTitle());
      gadgetJson.put("appId", gadget.getApplication().getId());
      gadgetJson.put("column", gadget.getColumn());
      gadgetJson.put("order", gadget.getOrder());
      gadgetJson.put("icon", gadget.getApplication().getIcon());
      gadgetJson.put("url", gadget.getApplication().getUrl());
      String token;
      token = tokenService.createSecurityToken(viewerId, ownerId, gadget);
      gadgetJson.put("securityToken", token);
      gadgets.put(gadgetJson);
    }
    groupJson.put("gadgets", gadgets);

    return groupJson;
  }
}

