/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;

import org.json.JSONObject;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.globant.katari.core.application.Command;
import com.globant.katari.gadgetcontainer.application.GadgetGroupCommand;
import com.globant.katari.gadgetcontainer.domain.ContextUserService;
import com.globant.katari.gadgetcontainer.application.TokenService;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;

/**
 * Retrieve the representation for the requested {@link GadgetGroup}
 *
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
public abstract class GadgetGroupController extends AbstractCommandController {

  /** Used to retrieve the current logged in user.
   *
   * This is never null.
   */
  private final ContextUserService userService;

  /** The open social token service implementation.
   *
   * This is never null.
   */
  private final TokenService tokenService;

  /** Constructor.
   *
   * @param theTokenService the token generator service. It can not be null.
   */
  public GadgetGroupController(final ContextUserService theUserService,
      final TokenService theTokenService) {
    Validate.notNull(theUserService, "user service can not be null");
    Validate.notNull(theTokenService, "token service can not be null");
    userService = theUserService;
    tokenService = theTokenService;
  }

  /** Write directly on the servlet output the json representation of
   * the requested group.
   *
   * If the gadget group is found, it writes a json of the form:
   *
   * TODO The viewer id should probably go at the gadget group level.
   *
   * <pre>
   * {
   *   "id":<long>,
   *   "name":"<string>"
   *   "ownerId":<long>,
   *   "viewerId":<long>,
   *   "numberOfColumns":<int>,
   *   "gadgets":[
   *     {
   *       "id":<long>,
   *       "appId":<long>,
   *       "column":<int>,
   *       "order":<int>,
   *       "securityToken":"token"
   *       "url":"url"
   *     }
   *   ],
   * }
   * </pre>
   *
   * Otherwise, it writes an empty json object ({}).
   *
   * {@inheritDoc}
   *
   * Note: This controller always returns null, because write directly to the
   * response.
   */
  @Override
  protected ModelAndView handle(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException errors) throws Exception {

    GadgetGroupCommand gadgetGroupCommand = (GadgetGroupCommand) command;
    GadgetGroup group = gadgetGroupCommand.execute();

    response.addHeader("Content-type", "application/json");

    // The id of the logged in user (viewer).
    long uid = userService.getCurrentUserId();

    JSONObject groupJson = new JSONObject();
    if (group != null) {
      long owner = group.getOwner().getId();
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

    groupJson.write(response.getWriter());
    return null;
  }

  /** @return {@link Command<GadgetGroup>}
   */
  protected abstract Command<GadgetGroup> createCommandBean();

  /** Create a new {@link Command} instance.
   *
   * @see org.springframework.web.servlet.mvc.BaseCommandController#getCommand(
   *  javax.servlet.http.HttpServletRequest)
   */
  @Override
  protected Object getCommand(final HttpServletRequest request)
    throws Exception {
    return createCommandBean();
  }
}

