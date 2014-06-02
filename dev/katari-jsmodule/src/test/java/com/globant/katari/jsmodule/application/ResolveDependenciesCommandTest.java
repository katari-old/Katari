/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.application;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.globant.katari.jsmodule.domain.BundleCache;

import com.globant.katari.jsmodule.domain.DependenciesFinder;
import com.globant.katari.jsmodule.domain.DependenciesResolver;
import com.globant.katari.core.application.JsonRepresentation;

/** Test cases for the {@link ResolveDependenciesCommand}.
 */
public class ResolveDependenciesCommandTest {

  private ResolveDependenciesCommand command;
  private DependenciesResolver resolver;
  private BundleCache cache;

  @Before
  public void setUp() {
    DependenciesFinder finder = new DependenciesFinder(true);
    resolver = new DependenciesResolver(finder);
    cache = createMock(BundleCache.class);
  }

  @Test
  public void execute_debugMode() throws JSONException {

    command = new ResolveDependenciesCommand(resolver, cache, true);
    List<String> files = Arrays.asList(
    "/com/globant/katari/jsmodule/testfile/calendar.js");
    command.setFiles(files);
    JsonRepresentation result = command.execute();
    JSONObject jsonDeps = (JSONObject)result.getJsonObject();
    JSONArray jsDeps = (JSONArray) jsonDeps.getJSONArray("js");
    List<String> depsFound = new ArrayList<String>();
    for (int i = 0; i < jsDeps.length(); i++) {
      depsFound.add(jsDeps.getString(i));
    }

    List<String> expectedResult = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/jquery.js",
        "/com/globant/katari/jsmodule/testfile/ui.js",
        "/com/globant/katari/jsmodule/testfile/jquery-ui.js",
        "/com/globant/katari/jsmodule/testfile/calendar.js");

    assertThat(depsFound, is(expectedResult));
  }

  @Test
  public void execute_notDebugModeAlreadyCachedFiles() throws JSONException {

    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js",
    "/com/globant/katari/jsmodule/testfile/compress-test-2.js");
    String content =
      "/***************************************************\n"
      + " * Bundled from"
      + " '/com/globant/katari/jsmodule/testfile/compress-test-1.js'\n"
      + " ***************************************************/\n"
      + "var testFunction=function(){var a=10};\n"
      + "/***************************************************\n"
      + " * Bundled from"
      + " '/com/globant/katari/jsmodule/testfile/compress-test-2.js'\n"
      + " ***************************************************/\n"
      + "var thisFunction=function(b,c,a){var d=1;return d};\n";

    expect(cache.findKey(files)).andReturn(null);
    expect(cache.store(files, content)).andReturn("md5_cache_key.js");
    replay(cache);

    command = new ResolveDependenciesCommand(resolver, cache, false);
    command.setFiles(files);
    JsonRepresentation result = command.execute();
    JSONObject jsonDeps = (JSONObject)result.getJsonObject();
    JSONArray jsDeps = (JSONArray) jsonDeps.getJSONArray("js");
    List<String> depsFound = new ArrayList<String>();
    for (int i = 0; i < jsDeps.length(); i++) {
      depsFound.add(jsDeps.getString(i));
    }

    List<String> expectedResult = Arrays.asList(
        "/com/globant/katari/jsmodule/bundle/md5_cache_key.js");

    assertThat(depsFound, is(expectedResult));
    verify(cache);
  }

  @Test
  public void execute_notDebugModeNotCachedFiles() throws JSONException {

    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js",
    "/com/globant/katari/jsmodule/testfile/compress-test-2.js");
    expect(cache.findKey(files)).andReturn("md5_cache_key.js");
    replay(cache);

    command = new ResolveDependenciesCommand(resolver, cache, false);
    command.setFiles(files);
    JsonRepresentation result = command.execute();
    JSONObject jsonDeps = (JSONObject)result.getJsonObject();
    JSONArray jsDeps = (JSONArray) jsonDeps.getJSONArray("js");
    List<String> depsFound = new ArrayList<String>();
    for (int i = 0; i < jsDeps.length(); i++) {
      depsFound.add(jsDeps.getString(i));
    }

    List<String> expectedResult = Arrays.asList(
        "/com/globant/katari/jsmodule/bundle/md5_cache_key.js");

    assertThat(depsFound, is(expectedResult));
    verify(cache);
  }

  @Test (expected = RuntimeException.class)
  public void newCommand_nullResolver() {
    command = new ResolveDependenciesCommand(null, cache, true);
  }

  @Test (expected = RuntimeException.class)
  public void newCommand_nullCache() {
    command = new ResolveDependenciesCommand(resolver, null, false);
  }

  @Test (expected = RuntimeException.class)
  public void setFiles_nullFiles() {
    command =
      new ResolveDependenciesCommand(resolver, cache, false);
    command.setFiles(null);
  }
}
