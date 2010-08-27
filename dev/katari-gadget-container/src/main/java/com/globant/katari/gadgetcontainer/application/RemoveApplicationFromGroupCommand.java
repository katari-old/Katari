/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.apache.commons.lang.StringUtils.isBlank;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;

import com.globant.katari.core.application.Command;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

/** Removes a gadget instance from a gadget group.
 * 
 * This command expects a gadget group name and the gadget instance id.
 *
 * The gadget group must be customizable, owned by the logged in user, and the
 * gadget must be in the group.
 *
 * It returns an empty JsonRepresentation ({}).
 */
public class RemoveApplicationFromGroupCommand
  implements Command<JsonRepresentation> {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      RemoveApplicationFromGroupCommand.class);

  /** The repository for gadget groups.
   *
   * This is never null.
   */
  private final GadgetGroupRepository gadgetGroupRepository;

  /** Service used to obtain the currently logged on user.
   *
   * This is never null.
   */
  private final ContextUserService userService;

  /** The name of the gadget group to search, provided by the client.
   */
  private String groupName;

  /** The id of the gadget instance to remove from the gadget group.
   */
  private long gadgetId;

  /** Constructor.
   *
   * @param theGroupRepository Cannot be null.
   *
   * @param theUserService Cannot be null.
   */
  public RemoveApplicationFromGroupCommand(
      final GadgetGroupRepository theGroupRepository,
      final ContextUserService theUserService) {

    Validate.notNull(theGroupRepository, "group repository can not be null");
    Validate.notNull(theUserService, "user service can not be null");

    gadgetGroupRepository = theGroupRepository;
    userService = theUserService;
  }

  /** Obtains the group name, as provided by the client.
   *
   * @return the group name.
   */
  public String getGroupName() {
    return groupName;
  }

  /** The name of the group, as provided by the client.
   *
   * @param name the group name to set. It must be called with a non empty
   * string before calling execute.
   */
  public void setGroupName(final String name) {
    groupName = name;
  }

  /** Obtains the gadget instance id, as provided by the client.
   *
   * @return the gadget instance id.
   */
  public long getGadgetId() {
    return gadgetId;
  }

  /** Sets the id of the gadget instance to remove, as provided by the client.
   *
   * @param name the id of the gadget instance to remove.
   */
  public void setGadgetId(final long id) {
    gadgetId = id;
  }

  /** Adds a new application to the gadget group.
   *
   * Before calling execute, call setGroupName with a non empty string, and set
   * the application id. The group name and application must exist.
   *
   * Returns an empty json object ({}).
   *
   * @return a json object, never returns null.
   */
  public JsonRepresentation execute() {

    log.trace("Entering execute");

    if(isBlank(groupName)) {
      throw new IllegalArgumentException("groupName can not be blank");
    }
    long uid = userService.getCurrentUserId();
    log.debug("searching group name = " + groupName + " for the user:" + uid);
    GadgetGroup group = gadgetGroupRepository.findGadgetGroup(uid, groupName);
    if (group == null) {
      throw new RuntimeException("Group not found");
    } else if (!group.isCustomizable()) {
      throw new RuntimeException(
          "The group you are trying to modify is not configurable.");
    }
    if (!group.remove(gadgetId)) {
      throw new RuntimeException("The gadget is not in the group.");
    }
    gadgetGroupRepository.save(group);
    log.trace("Leaving execute");
    return new JsonRepresentation(new JSONObject());
  }
}

