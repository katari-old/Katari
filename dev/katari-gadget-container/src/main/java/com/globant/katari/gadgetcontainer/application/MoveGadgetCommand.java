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

/** Moves a gadget to a new column and/or position in the column, for a gadget
 * group.
 *
 * This command expects a gadget group name, the gadget instance id, and the
 * new column and position of the gadget instance.
 *
 * It returns an empty JSONObject ({}).
 */
public class MoveGadgetCommand implements Command<JSONObject> {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(MoveGadgetCommand.class);

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

  /** The gadget instance id to move, provided by the client.
   */
  private long gadgetInstanceId;

  /** The colmun to move the gadget to, provided by the client.
   */
  private int column;

  /** The position to move the gadget to, provided by the client.
   */
  private int order;

  /** Constructor.
   *
   * @param theGroupRepository Cannot be null.
   *
   * @param theUserService Cannot be null.
   */
  public MoveGadgetCommand(final GadgetGroupRepository theGroupRepository,
      final ContextUserService theUserService) {

    Validate.notNull(theGroupRepository, "gadget repository can not be null");
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

  /** The name of the group to search for, as provided by the client.
   *
   * @param name the group name to set. It must be called with a non empty
   * string before calling execute.
   */
  public void setGroupName(final String name) {
    groupName = name;
  }

  /** Obtains the gadget instance id, as provided by the client.
   *
   * @return the gadget id.
   */
  public long getGadgetInstanceId() {
    return gadgetInstanceId;
  }

  /** Sets id of the gadget instance to move, as provided by the client.
   *
   * @param name the id of the gadget to move.
   */
  public void setGadgetInstanceId(final long id) {
    gadgetInstanceId = id;
  }

  /** Obtains the target column number, 0 being the first.
   *
   * @return the new column.
   */
  public int getColumn() {
    return column;
  }

  /** The target column to move the gadget to.
   *
   * @param targetColumn the column to move the gadget to.
   */
  public void setColumn(final int targetColumn) {
    column = targetColumn;
  }

  /** Obtains the target position of the gadget in the column, 0 being the
   * first.
   *
   * @return the new position.
   */
  public int getOrder() {
    return order;
  }

  /** The target column to move the gadget to.
   *
   * @param targetColumn the column to move the gadget to.
   */
  public void setOrder(final int targetOrder) {
    order = targetOrder;
  }

  /** Moves the gadget in the group to the new column and/or order.
   *
   * Before calling execute, call setGroupName with a non empty string, and set
   * the gadget instance id and destination column and order. The group name
   * and gadget instance must exist.
   *
   * Returns an empty json object ({}).
   *
   * @return a json object, never returns null.
   */
  public JSONObject execute() {

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
    } else if (!(0 <= column && column < group.getNumberOfColumns())) {
      throw new RuntimeException(
          "Trying to move the gadget to a non existing column.");
    } else {
      group.move(gadgetInstanceId, column, order);
      gadgetGroupRepository.save(group);
      log.trace("Leaving execute");
      return new JSONObject();
    }
  }
}

