package com.globant.katari.jsmodule.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.globant.katari.jsmodule.domain.DependenciesFinder;
import com.globant.katari.jsmodule.domain.DependenciesResolver;

/** Integration test cases for {@link DependenciesResolver} using non-mocked
 * {@link DependenciesFinder} instances and dep.js files which can be found in
 * src/test/resources/com/globant/igexpnasion/smp/jslib/testfile.
 *
 * @author ivan.bedecarats@globant.com
 *
 */
public class DependenciesResolverIntegrationTest {

  /**
   * The dependencies involved in this test case are the following:
   * <ul>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/calendar.js = [
   *      /com/globant/katari/jsmodule/testfile/jquery.js,
   *      /com/globant/katari/jsmodule/testfile/jquery-ui.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/jquery.js = []
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/jquery-ui.js = [
   *      /com/globant/katari/jsmodule/testfile/ui.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/ui.js = []
   *  </li>
   * </ul>
   */
  @Test
  public void resolve_singleDependency() throws Exception {
    DependenciesFinder finder = new DependenciesFinder();
    DependenciesResolver resolver = new DependenciesResolver(finder);
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/calendar.js");
    List<String> foundDependencies = resolver.resolve(files);
    List<String> expectedResult = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/jquery.js",
        "/com/globant/katari/jsmodule/testfile/ui.js",
        "/com/globant/katari/jsmodule/testfile/jquery-ui.js",
        "/com/globant/katari/jsmodule/testfile/calendar.js");
    assertThat(foundDependencies, is(expectedResult));
  }

  /**
   * The dependencies involved in this test case are the following:
   * <ul>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/calendar.js = [
   *      /com/globant/katari/jsmodule/testfile/jquery.js,
   *      /com/globant/katari/jsmodule/testfile/jquery-ui.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/jquery.js = []
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/jquery-ui.js = [
   *      /com/globant/katari/jsmodule/testfile/ui.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/ui.js = []
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/colorPicker.js = [
   *      /com/globant/katari/jsmodule/testfile/jquery.js,
   *      /com/globant/katari/jsmodule/testfile/color.js,
   *      /com/globant/katari/jsmodule/testfile/picker.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/color.js = []
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/picker.js = []
   *  </li>
   * </ul>
   * Please take note that if a .js doesn't have any dependency files then
   * there will be no .dep.js file assossiated to it.
   */
  @Test
  public void resolve_MultipleDependency() throws Exception {
    DependenciesFinder finder = new DependenciesFinder();
    DependenciesResolver resolver = new DependenciesResolver(finder);
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/calendar.js",
        "/com/globant/katari/jsmodule/testfile/colorPicker.js");
    List<String> foundDependencies = resolver.resolve(files);
    List<String> expectedResult = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/jquery.js",
        "/com/globant/katari/jsmodule/testfile/ui.js",
        "/com/globant/katari/jsmodule/testfile/jquery-ui.js",
        "/com/globant/katari/jsmodule/testfile/calendar.js",
        "/com/globant/katari/jsmodule/testfile/color.js",
        "/com/globant/katari/jsmodule/testfile/picker.js",
        "/com/globant/katari/jsmodule/testfile/colorPicker.js");
    assertThat(foundDependencies, is(expectedResult));
  }

  /**
   * The dependencies involved in this test case are the following:
   * <ul>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/calendar.js = [
   *      /com/globant/katari/jsmodule/testfile/jquery.js,
   *      /com/globant/katari/jsmodule/testfile/jquery-ui.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/jquery.js = []
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/jquery-ui.js = [
   *      /com/globant/katari/jsmodule/testfile/ui.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/ui.js = []
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/complex.js = [
   *      /com/globant/katari/jsmodule/testfile/button.js,
   *      /com/globant/katari/jsmodule/testfile/picker.js,
   *      /com/globant/katari/jsmodule/testfile/ui.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/button.js = [
   *      /com/globant/katari/jsmodule/testfile/submit.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/submit.js = []
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/picker.js = []
   *  </li>
   * </ul>
   * Please take note that if a .js doesn't have any dependency files then
   * there will be no .dep.js file assossiated to it.
   */
  @Test
  public void resolve_ComplexDependency() throws Exception {
    DependenciesFinder finder = new DependenciesFinder();
    DependenciesResolver resolver = new DependenciesResolver(finder);
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/calendar.js",
        "/com/globant/katari/jsmodule/testfile/complex.js");
    List<String> foundDependencies = resolver.resolve(files);
    List<String> expectedResult = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/jquery.js",
        "/com/globant/katari/jsmodule/testfile/ui.js",
        "/com/globant/katari/jsmodule/testfile/jquery-ui.js",
        "/com/globant/katari/jsmodule/testfile/calendar.js",
        "/com/globant/katari/jsmodule/testfile/submit.js",
        "/com/globant/katari/jsmodule/testfile/button.js",
        "/com/globant/katari/jsmodule/testfile/picker.js",
        "/com/globant/katari/jsmodule/testfile/complex.js");
    assertThat(foundDependencies, is(expectedResult));
  }

  /**
   * The dependencies involved in this test case are the following:
   * <ul>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/event.js = [
   *      /com/globant/katari/jsmodule/testfile/action.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/action.js = [
   *      /com/globant/katari/jsmodule/testfile/event.js
   *    ]
   *  </li>
   * </ul>
   */
  @Test (expected = RuntimeException.class)
  public void resolve_singleCircularDependency() {
    DependenciesFinder finder = new DependenciesFinder();
    DependenciesResolver resolver = new DependenciesResolver(finder);
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/event.js");
    resolver.resolve(files);
  }

  /**
   * The dependencies involved in this test case are the following:
   * <ul>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/post.js = [
   *      /com/globant/katari/jsmodule/testfile/comment.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/comment.js = [
   *      /com/globant/katari/jsmodule/testfile/share.js,
   *      /com/globant/katari/jsmodule/testfile/like.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/like.js = [
   *      /com/globant/katari/jsmodule/testfile/post.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/share.js = [
   *      /com/globant/katari/jsmodule/testfile/link.js,
   *      /com/globant/katari/jsmodule/testfile/post.js
   *    ]
   *  </li>
   *  <li>
   *    /com/globant/katari/jsmodule/testfile/link.js = []
   *  </li>
   * </ul>
   * Please take note that if a .js doesn't have any dependency files then
   * there will be no .dep.js file assossiated to it.
   */
  @Test (expected = RuntimeException.class)
  public void resolve_complexCircularDependency() {
    DependenciesFinder finder = new DependenciesFinder();
    DependenciesResolver resolver = new DependenciesResolver(finder);
    List<String> files = Arrays.asList(
        "/com/globant/katari/jsmodule/testfile/post.js");
    resolver.resolve(files);
  }
}
