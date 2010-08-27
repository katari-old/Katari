/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.apache.commons.lang.StringUtils.isBlank;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.globant.katari.core.application.Command;

import com.globant.katari.shindig.domain.Application;
import com.globant.katari.gadgetcontainer.domain.ApplicationRepository;

/** Lists all the registered applications.
 */
public class ListApplicationsCommand implements Command<List<Application>> {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      ListApplicationsCommand.class);

  /** The repository for applications, never null.
   */
  private final ApplicationRepository applicationRepository;

  /** The gadget group where to add the application to.
   */
  private String gadgetGroupName;

  /** The url to go back when the user 'closes' the application list.
   */
  private String returnUrl;

  /** Constructor.
   *
   * @param theApplicationRepository Cannot be null.
   */
  public ListApplicationsCommand(
      final ApplicationRepository theApplicationRepository) {
    Validate.notNull(theApplicationRepository,
        "gadget repository can not be null");
    applicationRepository = theApplicationRepository;
  }

  /** Obtains all the applications.
   *
   * @return a list of applications, never returns null.
   */
  public List<Application> execute() {
    log.trace("Entering execute");
    List<Application> applications = applicationRepository.findAll();
    log.trace("Entering execute");
    return applications;
  }

  /** Obtains the name of the group to add the application to.
   *
   * @return the name of the group.
   */
  public String getGadgetGroupName() {
    return gadgetGroupName;
  }

  /** Sets the name of the group to add the application to.
   *
   * @param name the name of the group.
   */
  public void setGadgetGroupName(final String name) {
    gadgetGroupName = name;
  }

  /** Obtains the url to return to when the user closes the application list.
   *
   * @return the url to return to.
   */
  public String getReturnUrl() {
    return returnUrl;
  }

  /** Sets the url to return to when the user closes the application list.
   *
   * @param url the url to return to.
   */
  public void setReturnUrl(final String url) {
    returnUrl = url;
  }
}

