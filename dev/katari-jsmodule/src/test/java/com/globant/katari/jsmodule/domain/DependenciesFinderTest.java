/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/** Represents the Test Cases for a {@link DependenciesFinder}.
 *
 * @author ivan.bedecarats@globant.com
 */
public class DependenciesFinderTest {

  /**
   * The dependencies for calendar.js file are the following:
   * <ul>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/calendar.js = [
   *      /com/globant/katari/jsmodule/testfile/jquery.js,
   *      /com/globant/katari/jsmodule/testfile/jquery-ui.js
   *    ]
   *  </li>
   * </ul>
   */
  @Test
  public void find() {
    DependenciesFinder depFinder = new DependenciesFinder(true);
    List<String> depsFound = depFinder.find(
        "/com/globant/katari/jsmodule/testfile/calendar.js");
    List<String> expectedResult = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/jquery.js",
        "/com/globant/katari/jsmodule/testfile/jquery-ui.js");
    assertThat(depsFound, is(expectedResult));
  }

  /**
   * The jquery.js doesn't have any dependencies so there is no .dep.js file
   * associated with it.
   * <ul>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/calendar.js = []
   *  </li>
   * </ul>
   */
  @Test
  public void find_noDeps() {
    DependenciesFinder depFinder = new DependenciesFinder(true);
    List<String> depsFound = depFinder.find(
        "/com/globant/katari/jsmodule/testfile/jquery.js");
    List<String> expectedResult = new ArrayList<String>();
    assertThat(depsFound, is(expectedResult));
  }

  /**
   * If the file doesn't exist, then an empty {@link List} will be returned
   * (it's the same case as if the files doesn't have any dependencies).
   */
  @Test
  public void find_depFileDoesntExit() {
    DependenciesFinder depFinder = new DependenciesFinder(true);
    List<String> depsFound = depFinder.find(
        "/com/globant/katari/jsmodule/testfile/doesntExit.js");
    List<String> expectedResult = new ArrayList<String>();
    assertThat(depsFound, is(expectedResult));
  }

  /* empty.js has an invalid empty .dep.js file.
   *
   * <ul>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/empty.js = empty.
   *  </li>
   * </ul>
   */
  @Test (expected = RuntimeException.class)
  public void find_emptyDepFile() {
    DependenciesFinder depFinder = new DependenciesFinder(true);
    depFinder.find("/com/globant/katari/jsmodule/testfile/empty.js");
  }

  @Test (expected = RuntimeException.class)
  public void find_invalidDepFile() {
    DependenciesFinder depFinder = new DependenciesFinder(true);
    List<String> depsFound = depFinder.find(
        "/com/globant/katari/jsmodule/testfile/invalidJson.js");
    List<String> expectedResult = new ArrayList<String>();
    assertThat(depsFound, is(expectedResult));
  }

  @Test (expected = IllegalArgumentException.class)
  public void find_nullFile() {
    (new DependenciesFinder(true)).find(null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void find_emptyFile() {
    (new DependenciesFinder(true)).find(null);
  }

  @Test (expected = IllegalArgumentException.class)
  public void find_invalidJsFile() {
    (new DependenciesFinder(true)).find("file.txt");
  }
}
