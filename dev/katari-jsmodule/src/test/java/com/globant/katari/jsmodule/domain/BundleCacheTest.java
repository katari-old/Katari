/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.domain;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/** Represents the Test Cases for the {@link BundleCache}.
 *
 * @author ivan.bedecarats@globant.com.
 */
public class BundleCacheTest {

  @Test
  public void findKey_fileNotCached() {
    List<String> files = Arrays.asList("a.js", "b.js");
    BundleCache cache = new BundleCache();
    String key = cache.findKey(files);
    assertThat(key, is(nullValue()));
  }

  @Test
  public void store() {
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js",
        "/com/globant/katari/jsmodule/testfile/compress-test-2.js");
    String content = "var testFunction=function(){var a=10};"
      + "var thisFunction=function(b,c,a){var d=1;return d\n};";
    BundleCache cache = new BundleCache();
    String key = cache.store(files, content);
    assertThat(key, is("7784ff6c556abbc9a5af856260931dd0.js"));
  }

  @Test
  public void store_diffentKeysDifferentFiles() {
    List<String> file1 = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js");
    String content1 = "var testFunction=function(){var a=10};";
    List<String> file2 = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-2.js");
    String content2 = "var thisFunction=function(b,c,a){var d=1;return d\n};";
    BundleCache cache = new BundleCache();
    String key1 = cache.store(file1, content1);
    assertThat(key1, is("fdb0dfbb82819f9ff8520500bab35710.js"));
    String key2 = cache.store(file2, content2);
    assertThat(key2, is("3f38f7ff5855779821b22e1d54662851.js"));
    assertThat(!key1.equals(key2), is(true));
  }

  @Test
  public void findKey() {
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js",
        "/com/globant/katari/jsmodule/testfile/compress-test-2.js");
    String content = "var testFunction=function(){var a=10};"
      + "var thisFunction=function(b,c,a){var d=1;return d\n};";
    BundleCache cache = new BundleCache();
    String key = cache.store(files, content);
    String returnedKey = cache.findKey(files);
    assertThat(returnedKey, is(key));
  }

  @Test
  public void findContent() {
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js",
        "/com/globant/katari/jsmodule/testfile/compress-test-2.js");
    String content = "var testFunction=function(){var a=10};"
      + "var thisFunction=function(b,c,a){var d=1;return d\n};";
    BundleCache cache = new BundleCache();
    cache.store(files, content);
    String key = "7784ff6c556abbc9a5af856260931dd0.js";
    String returnedContent = cache.findContent(key);
    assertThat(returnedContent, is(content));
  }

  @Test (expected = RuntimeException.class)
  public void findContent_nullKey() {
    BundleCache cache = new BundleCache();
    cache.findContent(null);
  }

  @Test (expected = RuntimeException.class)
  public void findContent_emptyKey() {
    BundleCache cache = new BundleCache();
    cache.findContent(new String());
  }

  @Test (expected = RuntimeException.class)
  public void store_nullFiles() {
    BundleCache cache = new BundleCache();
    cache.store(null, "content");
  }

  @Test (expected = RuntimeException.class)
  public void store_emptyFiles() {
    BundleCache cache = new BundleCache();
    cache.store(new ArrayList<String>(), "content");
  }

  @Test (expected = RuntimeException.class)
  public void store_nullContent() {
    BundleCache cache = new BundleCache();
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js",
        "/com/globant/katari/jsmodule/testfile/compress-test-2.js");
    cache.store(files, null);
  }

  @Test (expected = RuntimeException.class)
  public void store_emptyContent() {
    BundleCache cache = new BundleCache();
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/compress-test-1.js",
        "/com/globant/katari/jsmodule/testfile/compress-test-2.js");
    cache.store(files, new String());
  }

  @Test (expected = RuntimeException.class)
  public void findkey_nullFiles() {
    BundleCache cache = new BundleCache();
    cache.findKey(null);
  }

  @Test (expected = RuntimeException.class)
  public void findkey_emptyFiles() {
    BundleCache cache = new BundleCache();
    cache.findKey(new ArrayList<String>());
  }

}
