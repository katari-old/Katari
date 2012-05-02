/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.domain;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class DependenciesBundlerTest {

  @Test
  public void bundle_simpleJSFile() {
    List<String> scriptToCompress = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js");
    DependenciesBundler bundler = new DependenciesBundler();
    String compressedScript = bundler.bundleFiles(scriptToCompress);

    assertThat(compressedScript,
        is("/***************************************************\n"
         + " * Bundled from"
         + " '/com/globant/katari/jsmodule/testfile/compress-test-1.js'\n"
         + " ***************************************************/\n"
         + "var testFunction=function(){var a=10};\n"));
  }

  @Test
  public void bundle_concatenationJSFile() {
    List<String> scriptToCompress = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js",
        "/com/globant/katari/jsmodule/testfile/compress-test-2.js");
    DependenciesBundler bundler = new DependenciesBundler();
    String compressedScript = bundler.bundleFiles(scriptToCompress);

    assertThat(compressedScript,
        is("/***************************************************\n"
         + " * Bundled from"
         + " '/com/globant/katari/jsmodule/testfile/compress-test-1.js'\n"
         + " ***************************************************/\n"
         + "var testFunction=function(){var a=10};\n"
         + "/***************************************************\n"
         + " * Bundled from"
         + " '/com/globant/katari/jsmodule/testfile/compress-test-2.js'\n"
         + " ***************************************************/\n"
         + "var thisFunction=function(b,c,a){var d=1;return d};\n"));
  }

  @Test
  public void bundle_failIfConcatenatedBeforeCompression() {
    List<String> scriptToCompress = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-3.js",
        "/com/globant/katari/jsmodule/testfile/compress-test-4.js");
    DependenciesBundler bundler = new DependenciesBundler();
    String compressedScript = bundler.bundleFiles(scriptToCompress);

    assertThat(compressedScript,
        is("/***************************************************\n"
         + " * Bundled from"
         + " '/com/globant/katari/jsmodule/testfile/compress-test-3.js'\n"
         + " ***************************************************/\n"
         + "var a=function(b){};(function(b){var c=10})(10);\n"
         + "/***************************************************\n"
         + " * Bundled from"
         + " '/com/globant/katari/jsmodule/testfile/compress-test-4.js'\n"
         + " ***************************************************/\n"
         + "a(10);\n"));
  }

  @Test (expected = RuntimeException.class)
  public void bundleFiles_nullFiles() {
    DependenciesBundler bundler = new DependenciesBundler();
    bundler.bundleFiles(null);
  }
}

