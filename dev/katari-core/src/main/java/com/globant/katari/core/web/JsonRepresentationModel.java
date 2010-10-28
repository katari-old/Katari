/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globant.katari.core.application.JsonRepresentation;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

/** A Freemarker JsonRepresentation model to expose the Json objects from the
 * JsonRepresentation as freemarker elements.
 */
public final class JsonRepresentationModel implements TemplateHashModel,
    TemplateSequenceModel {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      JsonRepresentationModel.class);

  /** The wrapped JsonRepresentation, never null.
   */
  private JsonRepresentation representation;

  /** Constructor.
   *
   * @param object the wrapper JsonRepresentation object, never null.
   */
  public JsonRepresentationModel(final JsonRepresentation object) {
    Validate.notNull(object, "The json representation object cannot be null.");
    representation = object;
  }

  /** {@inheritDoc}
   *
   * Returns a wrapped array, object or scalar for the given json object
   * member.
   *
   * @throws TemplateModelException if the wrapped object references a json
   * array instead of a json object.
   */
  public TemplateModel get(final String key) throws TemplateModelException {
    log.trace("Entering get {}.", key);

    JSONObject object = representation.getJsonObject();
    if (object == null) {
      // This is not an object, bail out.
      throw new TemplateModelException(
          "Tried to access an array as an object");
    }
    JSONObject target = object.optJSONObject(key);
    if (target != null) {
      TemplateModel model;
      model = new JsonRepresentationModel(new JsonRepresentation(target));
      log.trace("Leaving get with json object");
      return model;
    }
    JSONArray array = object.optJSONArray(key);
    if (array != null) {
      TemplateModel model;
      model = new JsonRepresentationModel(new JsonRepresentation(array));
      log.trace("Leaving get with json array");
      return model;
    }
    if (object.has(key)) {
      String value;
      try {
        value = object.getString(key);
      } catch (JSONException e) {
        throw new RuntimeException("Error converting " + key + " to string.", e);
      }
      log.trace("Leaving get with string");
      return new SimpleScalar(value);
    } else {
      log.trace("Leaving get with null");
      return null;
    }
  }

  /** {@inheritDoc}
   *
   * Returns a wrapped array, object or scalar for the given json array index.
   *
   * @throws TemplateModelException if the wrapped object references a json
   * object instead of a json array.
   */
  public TemplateModel get(int index) throws TemplateModelException {
    JSONArray array = representation.getJsonArray();
    if (array == null) {
      // This is not an array, bail out.
      throw new TemplateModelException("Tried to iterate over a json object.");
    }
    
    JSONObject object = array.optJSONObject(index);
    if (object != null) {
      TemplateModel model;
      model = new JsonRepresentationModel(new JsonRepresentation(object));
      log.trace("Leaving with json array");
      return model;
    }
    JSONArray target = array.optJSONArray(index);
    if (target != null) {
      TemplateModel model;
      model = new JsonRepresentationModel(new JsonRepresentation(target));
      log.trace("Leaving with json array");
      return model;
    }
    String value;
    try {
      value = array.getString(index);
    } catch (JSONException e) {
      throw new RuntimeException("Error converting " + index + " to string.",
          e);
    }
    log.trace("Leaving with string");
    return new SimpleScalar(value);
  }

  public boolean isEmpty() throws TemplateModelException {
    JSONObject object = representation.getJsonObject();
    if (object == null) {
      // This is not an array, bail out.
      throw new TemplateModelException("isEmpty() only applies to an object");
    }
    return object.length() == 0;
  }

  public int size() throws TemplateModelException {
    JSONArray array = representation.getJsonArray();
    if (array == null) {
      // This is not an array, bail out.
      throw new TemplateModelException("size() only applies to an array");
    }
    return array.length();
  }
}

