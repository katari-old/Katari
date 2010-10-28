/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.core.application.JsonRepresentation;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;

import org.json.JSONArray;
import org.json.JSONObject;

/** This is a full integration test of the json representation wrapper and json
 * representation model.
 */
public final class JsonRepresentationWrapperTest {

  private static Logger log = LoggerFactory.getLogger(
      JsonRepresentationWrapper.class);

  Configuration configuration;

  @Before
  public void setUp() {
    configuration = new Configuration();
    configuration.setObjectWrapper(new JsonRepresentationWrapper());
  }

  @Test
  public void testSimpleJSONObject() throws Exception {
    StringReader reader = new StringReader(
        "element1='${result.element1}', element2='${result.element2}'");

    Template template = new Template("wrapper", reader, configuration);
    Map<String, Object> model = new HashMap<String, Object>();

    JSONObject element = new JSONObject();
    element.put("element1", "value-1");
    element.put("element2", "value-2");
    model.put("result", new JsonRepresentation(element));

    Writer out = new StringWriter();
    template.process(model, out);
    String result = out.toString();
    log.debug("Output for testSimpleJSONObject is {}", result);
    assertThat(result, is("element1='value-1', element2='value-2'"));
  }

  @Test
  public void testUseArrayAsObject() throws Exception {
    StringReader reader = new StringReader("'${result.element1}'");

    Template template = new Template("wrapper", reader, configuration);
    Map<String, Object> model = new HashMap<String, Object>();

    JSONArray element = new JSONArray();
    element.put(0, "value-1");
    element.put(1, "value-2");
    model.put("result", new JsonRepresentation(element));

    Writer out = new StringWriter();
    try {
      template.process(model, out);
      // Should throw TemplateModelException.
      assertThat(true, is(false));
    } catch (TemplateModelException e) {
      assertThat(e.getMessage(), is("Tried to access an array as an object"));
    }
  }

  @Test
  public void testNestedJSONObject() throws Exception {
    StringReader reader = new StringReader(
        "element1-1='${result.object.subelement1}',"
        + "element1-2='${result.object.subelement2}',"
        + "element2-1='${result.array[0]}',"
        + "element2-2='${result.array[1]}'");

    Template template = new Template("wrapper", reader, configuration);
    Map<String, Object> model = new HashMap<String, Object>();

    JSONObject object = new JSONObject();
    object.put("subelement1", "value-1-1");
    object.put("subelement2", "value-1-2");

    JSONArray array = new JSONArray();
    array.put(0, "value-2-1");
    array.put(1, "value-2-2");

    JSONObject element = new JSONObject();
    element.put("object", object);
    element.put("array", array);

    model.put("result", new JsonRepresentation(element));

    Writer out = new StringWriter();
    template.process(model, out);
    String result = out.toString();
    log.debug("Output for testSimpleJSONObject is {}", result);
    assertThat(result, is(
        "element1-1='value-1-1',"
        + "element1-2='value-1-2',"
        + "element2-1='value-2-1',"
        + "element2-2='value-2-2'"));
  }

  @Test
  public void testUseObjectAsArray() throws Exception {
    StringReader reader = new StringReader("'${result[0]}'");

    Template template = new Template("wrapper", reader, configuration);
    Map<String, Object> model = new HashMap<String, Object>();

    JSONObject element = new JSONObject();
    element.put("key1", "value-1");
    element.put("key2", "value-2");
    model.put("result", new JsonRepresentation(element));

    Writer out = new StringWriter();
    try {
      template.process(model, out);
      // Should throw TemplateModelException.
      assertThat(true, is(false));
    } catch (TemplateModelException e) {
      assertThat(e.getMessage(), is("Tried to iterate over a json object."));
    }
  }

  @Test
  public void testSimpleJSONArray() throws Exception {
    StringReader reader = new StringReader(
        "index0='${result[0]}', index1='${result[1]}'");

    Map<String, Object> model = new HashMap<String, Object>();
    JSONArray array = new JSONArray();
    array.put(0, "value 0");
    array.put(1, "value 1");
    model.put("result", new JsonRepresentation(array));

    Writer out = new StringWriter();

    Template template = new Template("wrapper", reader, configuration);
    template.process(model, out);
    String result = out.toString();
    log.debug("Output for testSimpleJSONObject is {}", result);
    assertThat(result, is("index0='value 0', index1='value 1'"));
  }

  @Test
  public void testNestedJSONArray() throws Exception {
    StringReader reader = new StringReader(
        "key0='${result[0].key1}', key0='${result[0].key2}', "
        + "index1='${result[1][0]}', index1='${result[1][1]}'"
        );

    Map<String, Object> model = new HashMap<String, Object>();

    JSONObject nestedObject = new JSONObject();
    nestedObject.put("key1", "value key1");
    nestedObject.put("key2", "value key2");

    JSONArray nestedArray = new JSONArray();
    nestedArray.put(0, "value 0");
    nestedArray.put(1, "value 1");

    JSONArray array = new JSONArray();
    array.put(0, nestedObject);
    array.put(1, nestedArray);

    model.put("result", new JsonRepresentation(array));

    Writer out = new StringWriter();

    Template template = new Template("wrapper", reader, configuration);
    template.process(model, out);
    String result = out.toString();
    log.debug("Output for testNestedJSONObject is {}", result);
    assertThat(result, is("key0='value key1', key0='value key2', "
          + "index1='value 0', index1='value 1'"));
  }

  @Test
  public void testNullJSONObject() throws Exception {
    StringReader reader = new StringReader(
        "e='${result.element!'nullValue'}'");

    Template template = new Template("wrapper", reader, configuration);
    Map<String, Object> model = new HashMap<String, Object>();

    JSONObject element = new JSONObject();
    element.put("element1", "value-1");
    model.put("result", new JsonRepresentation(element));

    Writer out = new StringWriter();
    template.process(model, out);
    String result = out.toString();
    log.debug("Output for testSimpleJSONObject is {}", result);
    assertThat(result, is("e='nullValue'"));
  }

}

