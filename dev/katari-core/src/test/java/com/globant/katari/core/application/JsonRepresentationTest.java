/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.Writer;
import java.io.StringWriter;

import org.junit.Test;

import org.json.JSONObject;
import org.json.JSONArray;

public class JsonRepresentationTest {

  @Test
  public void write_object() throws Exception {

    Writer writer = new StringWriter();

    JsonRepresentation rep = new JsonRepresentation(new JSONObject());
    rep.write(writer);

    assertThat(writer.toString(), is("{}"));
    assertThat(rep.getJsonObject(), is(not(nullValue())));
    assertThat(rep.getJsonArray(), is(nullValue()));
  }

  @Test
  public void write_array() throws Exception {

    Writer writer = new StringWriter();

    JsonRepresentation rep = new JsonRepresentation(new JSONArray());
    rep.write(writer);

    assertThat(writer.toString(), is("[]"));
    assertThat(rep.getJsonObject(), is(nullValue()));
    assertThat(rep.getJsonArray(), is(not(nullValue())));
  }

  @Test
  public void toString_object() throws Exception {
    JsonRepresentation rep = new JsonRepresentation(new JSONObject());
    assertThat(rep.toString(), is("{}"));
  }

  @Test
  public void toString_array() throws Exception {
    JsonRepresentation rep = new JsonRepresentation(new JSONArray());
    assertThat(rep.toString(), is("[]"));
  }
}

