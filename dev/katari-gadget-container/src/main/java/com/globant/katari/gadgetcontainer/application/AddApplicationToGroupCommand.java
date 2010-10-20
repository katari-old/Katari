/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.apache.commons.lang.StringUtils.isBlank;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.JsonRepresentation;

import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.gadgetcontainer.domain.GadgetGroupRepository;

import com.globant.katari.shindig.domain.Application;
import com.globant.katari.gadgetcontainer.domain.ApplicationRepository;

/** Adds an application to a gadget group.
 * 
 * This command expects a gadget group name and the application id. It adds the
 * application to the column 0, position 0 in the gadget group.
 *
 * The gadget group must be customizable, owned by the logged in user, and the
 * application must not be already in the group.
 *
 * It returns an empty JsonRepresentation ({}).
 */
public class AddApplicationToGroupCommand
  implements Command<JsonRepresentation> {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      AddApplicationToGroupCommand.class);

  /** The repository for applications.
   *
   * This is never null.
   */
  private final ApplicationRepository applicationRepository;

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

  /** The id of the application to add to the gadget group.
   */
  private long applicationId;

  /** Constructor.
   *
   * @param theApplicationRepository Cannot be null.
   *
   * @param theGroupRepository Cannot be null.
   *
   * @param theUserService Cannot be null.
   */
  public AddApplicationToGroupCommand(
      final ApplicationRepository theApplicationRepository,
      final GadgetGroupRepository theGroupRepository,
      final ContextUserService theUserService) {

    Validate.notNull(theApplicationRepository,
        "application repository can not be null");
    Validate.notNull(theGroupRepository, "gadget repository can not be null");
    Validate.notNull(theUserService, "user service can not be null");

    applicationRepository = theApplicationRepository;
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

  /** Obtains the application id, as provided by the client.
   *
   * @return the application id.
   */
  public long getApplicationId() {
    return applicationId;
  }

  /** Sets the id of the application to add, as provided by the client.
   *
   * @param name the id of the application to add.
   */
  public void setApplicationId(final long id) {
    applicationId = id;
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

    Application application = applicationRepository.find(applicationId);
    if (application == null) {
      throw new RuntimeException(
          "The application you are trying to add was not found");
    }
    if (group.contains(application)) {
      throw new RuntimeException("Application is already in the group.");
    }
    group.add(new GadgetInstance(application, 0, 0));
    gadgetGroupRepository.save(group);
    log.trace("Leaving execute");
    return new JsonRepresentation(new JSONObject());
  }
}

