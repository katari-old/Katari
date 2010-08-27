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
 * 
 * The execute operation returns a json representation of the list of all
 * available applications.
 */
public class ListApplicationsCommand implements Command<JsonRepresentation> {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      ListApplicationsCommand.class);

  /** The repository for applications, never null.
   */
  private final ApplicationRepository applicationRepository;

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

  /** Obtains all the applications and returns a json representation of them.
   *
   * The json structure is:
   *
   * <pre>
   * [
   *   {
   *     "id":&lt;long&gt;,
   *     "title":&lt;string&gt;,
   *     "url":&lt;string&gt;
   *   }
   * ]
   * </pre>
   *
   * @return a json object, never returns null.
   */
  public JsonRepresentation execute() {
    log.trace("Entering execute");
    List<Application> applications = applicationRepository.findAll();
    JsonRepresentation result;
    try {
      result = toJson(applications);
    } catch (JSONException e) {
      throw new RuntimeException("Error serializing to json", e);
    }
    log.trace("Entering execute");
    return result;
  }

  /** Generates the json representation of the provided application.
   *
   * @param applications The list of applications to convert to json. It cannot
   * be null.
   */
  private JsonRepresentation toJson(final List<Application> applications)
    throws JSONException {
    Validate.notNull(applications, "applications cannot be null.");

    JSONArray applicationsJson = new JSONArray();
    for (Application application : applications) {
      JSONObject applicationJson = new JSONObject();
      applicationJson = new JSONObject();
      applicationJson.put("id", application.getId());
      applicationJson.put("title", application.getTitle());
      applicationJson.put("url", application.getUrl());
      applicationsJson.put(applicationJson);
    }
    return new JsonRepresentation(applicationsJson);
  }
}

