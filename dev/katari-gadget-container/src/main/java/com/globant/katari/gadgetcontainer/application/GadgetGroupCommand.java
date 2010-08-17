/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.apache.commons.lang.StringUtils.isBlank;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;
import org.json.JSONException;

import com.globant.katari.core.application.Command;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

/** Command that looks for a gadget group.
 *
 * This command generates the json representation for the gadget group.
 *
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 */
public class GadgetGroupCommand implements Command<JSONObject> {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(GadgetGroupCommand.class);

  /** {@link GadgetGroupRepository} the DAO for page.
   */
  private final GadgetGroupRepository gadgetGroupRepository;

  /** {@link ContextUserService} retrieve the current user.
   */
  private final ContextUserService userService;

  /** The open social token service implementation.
   *
   * This is never null.
   */
  private final TokenService tokenService;

  /** {@link String} the page name to search.
   */
  private String groupName;

  /** Constructor.
   *
   * @param thePageRepository {@link GadgetGroupRepository}. Can not be null.
   * @param theUserService {@link ContextUserService}. Can not be null.
   * @param theTokenService {@link TokenService}. Can not be null.
   */
  public GadgetGroupCommand(final GadgetGroupRepository thePageRepository,
      final ContextUserService theUserService,
      final TokenService theTokenService) {

    Validate.notNull(thePageRepository, "page repository can not be null");
    Validate.notNull(theUserService, "user service can not be null");
    Validate.notNull(theTokenService, "token service can not be null");

    gadgetGroupRepository = thePageRepository;
    userService = theUserService;
    tokenService = theTokenService;
  }

  /** @return @link{String} the groupName. Never returns null.
   */
  public String getGroupName() {
    return groupName;
  }

  /** @param name {@link String} the groupName to set
   */
  public void setGroupName(final String name) {
    groupName = name;
  }

  /** Retrieve the user id from the spring security context then find the
   * group with the context user and the given groupName.
   *
   * The json structure is:
   *
   * <pre>
   * {
   *   "id":&lt;long&gt;,
   *   "name":"&lt;string&gt;"
   *   "ownerId":&lt;long&gt;,
   *   "viewerId":&lt;long&gt;,
   *   "numberOfColumns":&lt;int&gt;,
   *   "gadgets":[
   *     {
   *       "id":&lt;long&gt;,
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
   * @return a json object, never returns null.
   */
  public JSONObject execute() {

    if(isBlank(groupName)) {
      throw new IllegalArgumentException("groupName can not be blank");
    }
    long uid = userService.getCurrentUserId();
    log.debug("searching group name = " + groupName + "for the user:" + uid);
    GadgetGroup group = gadgetGroupRepository.findGadgetGroup(uid, groupName);
    try {
      if (group != null) {
        return toJson(uid, group);
      } else {
        return new JSONObject();
      }
    } catch (JSONException e) {
      throw new RuntimeException("Error serializing to json", e);
    }
  }

  /** Generates the json representation of the provided gadget group.
   *
   * @param uid The id of the user making the request.
   *
   * @param group The group to convert to json. It cannot be null.
   */
  private JSONObject toJson(final long uid, final GadgetGroup group)
    throws JSONException {
    Validate.notNull(group, "The gadget group cannot be null.");

    JSONObject groupJson = new JSONObject();
    if (group != null) {
      long owner = 0;
      if (group.getOwner() != null) {
        owner = group.getOwner().getId();
      }
      groupJson = new JSONObject();
      groupJson.put("id", group.getId());
      groupJson.put("name", group.getName());
      groupJson.put("ownerId", owner);
      groupJson.put("viewerId", uid);
      groupJson.put("numberOfColumns", group.getNumberOfColumns());

      for (GadgetInstance gadget : group.getGadgets()) {
        JSONObject gadgetJson = new JSONObject();
        gadgetJson.put("id", gadget.getId());
        gadgetJson.put("appId", gadget.getApplication().getId());
        gadgetJson.put("column", gadget.getColumn());
        gadgetJson.put("order", gadget.getOrder());
        gadgetJson.put("url", gadget.getApplication().getUrl());
        String token = tokenService.createSecurityToken(uid, owner, gadget);
        gadgetJson.put("securityToken", token);

        groupJson.append("gadgets", gadgetJson);
      }
    }
    return groupJson;
  }
}

