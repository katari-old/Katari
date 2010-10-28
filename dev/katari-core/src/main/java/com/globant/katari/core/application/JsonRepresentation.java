/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.application;

import org.apache.commons.lang.Validate;

import java.io.Writer;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/** A json representation of an object, based on JSON.
 *
 * It wraps a JSONObject or a JSONArray to provide a unified interface to write
 * a json string to a writer.
 */
public class JsonRepresentation {

  /** A wrapped json object.
   *
   * Either this or jsonArray must be not null.
   */
  private JSONObject jsonObject = null;

  /** A wrapped json array.
   *
   * Either this or jsonObject must be not null.
   */
  private JSONArray jsonArray = null;

  /** Wraps a JSONObject.
   *
   * @param object The json object. It cannot be null.
   */
  public JsonRepresentation(final JSONObject object) {
    Validate.notNull(object, "the json object cannot be null");
    jsonObject = object;
  }

  /** Wraps a JSONArray.
   *
   * @param array The json array. It cannot be null.
   */
  public JsonRepresentation(final JSONArray array) {
    Validate.notNull(array, "the json array cannot be null");
    jsonArray = array;
  }

  /** Writes this representation to the provided writer.
   *
   * @param writer The writer to write the json representation to. It cannot be
   * null.
   *
   * @throws JSONException if the json object could not be writen to the
   * writer.
   *
   * @return the provided writer, for writer chaining.
   */
  public Writer write(final Writer writer) throws JSONException {
    if (jsonArray != null) {
      return jsonArray.write(writer);
    } else {
      return jsonObject.write(writer);
    }
  }

  /** Obtains the underlying JSONObject.
   *
   * @return the underlying JSONObject or null if this representation wrapps an
   * array.
   */
  public JSONObject getJsonObject() {
    return jsonObject;
  }

  /** Obtains the underlying JSONArray.
   *
   * @return the underlying JSONArray or null if this representation wrapps an
   * object.
   */
  public JSONArray getJsonArray() {
    return jsonArray;
  }
}

