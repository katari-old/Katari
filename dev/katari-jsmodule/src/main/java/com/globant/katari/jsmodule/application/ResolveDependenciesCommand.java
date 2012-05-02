/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.JsonRepresentation;

import com.globant.katari.jsmodule.view.ContentModuleServlet;

import com.globant.katari.jsmodule.domain.BundleCache;
import com.globant.katari.jsmodule.domain.DependenciesBundler;
import com.globant.katari.jsmodule.domain.DependenciesResolver;

/** Returns the list of dependencies for a given list of files.
 *
 * This command resolves all the transitive dependencies for a list of
 * javascript files. In debug mode, it returns the list of files. In non-debug
 * mode, it bundles the files and makes the bundle available to the
 * ContentModuleServlet (through the BundleCache).
 *
 * Bundled content will be server from the path
 * /com/globant/katari/jsmodule/bundle relative to this module, by the
 * ContentModuleServlet.
 *
 * @author ivan.bedecarats@globant.com
 */
public class ResolveDependenciesCommand implements Command<JsonRepresentation> {

  /**
   * The Dependencies Resolver, it's never null.
   */
  private DependenciesResolver resolver;

  /**
   * If it's true means the application is running in debug mode, false
   * otherwise.
   */
  private boolean debugMode;

  /**
   * Contains a Map with the cache of the bundled files. It's never null.
   */
  private BundleCache cache;

  /**
   * The list of files whose dependencies are going to be fetched, it's never
   * null.
   */
  private List<String> files;

  /** Constructs a new {@link ResolveDependenciesCommand} to list the files
   * dependencies.
   * @param theResolver The {@link DependenciesResolver} used to resolve the
   * dependencies of the given list of files. Cannot be null.
   * @param theCache The {@link BundleCache} contains a Map with the cache of
   * the bundled files. Cannot be null.
   * @param theDebugMode If it's true means the application is running in debug
   * mode, false otherwise.
   */
  public ResolveDependenciesCommand(final DependenciesResolver theResolver,
      final BundleCache theCache, final boolean theDebugMode) {
    Validate.notNull(theResolver, "The dependencies resolver cannot be null.");
    Validate.notNull(theCache, "The Bundle Cache cannot be null.");
    resolver = theResolver;
    debugMode = theDebugMode;
    cache = theCache;
  }

  /**
   * Lists the dependency file paths for the given list of file paths.
   *
   * @return A {@link JsonRepresentation} with the following structure:
   *
   * <pre>
   *
   * If the application is running in debug mode, then it'll return the list of
   * dependency files:
   *
   * [{
   *   "js" : [{/com/globant/katari/jsmodule/testfile/jquery.js,
   *            /com/globant/katari/jsmodule/testfile/jquery-ui.js
   *          }]
   * }]
   *
   * If the application is not running in debug mode, there are 2 possible
   * scenarios:
   *
   * The list of files were already bundled in a single .js file and cached in
   * the {@link BundleCache}.
   * The list of files were never bundled in a single .js file and hence not
   * cached in the {@link BundleCache}.
   * In both cases, the key of the bundled file which is cached in the
   * {@link BundleCache} for the given files will be returned in "js"
   * {@link JSONArray} of the {@link JsonRepresentation}:
   * [{
   *   "js" : [{30171d9260a024d0e87201555b450617.js
   *          }]
   * }]
   * </pre>
   * Please note that the "js" array in the above {@link JsonRepresentation} was
   * filled with dummy values.
   */
  public JsonRepresentation execute() {
    List<String> dependenciesFound = new LinkedList<String>();

    // We sort here the files to guarantee that the sort order is the same in
    // debug and non-debug modes.
    Collections.sort(files);

    if (!debugMode) {
      String bundledName = cache.findKey(files);
      if (bundledName == null) {
        List<String> dependencies = resolver.resolve(files);
        DependenciesBundler depBundler = new DependenciesBundler();
        String bundledFileContent = depBundler.bundleFiles(dependencies);
        bundledName = cache.store(files, bundledFileContent);
      }
      dependenciesFound.add(ContentModuleServlet.BUNDLE_PATH + bundledName);
    } else {
      dependenciesFound = resolver.resolve(files);
    }

    JSONArray jsJsonDependencies = new JSONArray();
    for (String dependency : dependenciesFound) {
      jsJsonDependencies.put(dependency);
    }
    JSONObject jsonDependencies = new JSONObject();
    try {
      jsonDependencies.put("js", jsJsonDependencies);
      return new JsonRepresentation(jsonDependencies);
    } catch (JSONException e) {
      throw new RuntimeException("Error generating JSON", e);
    }
  }

  /** Sets the list of files whose dependencies are going to be fetched.
   * @param theFiles The list of files whose dependencies are going to be
   * fetched. It's never null.
   */
  public void setFiles(final List<String> theFiles) {
    Validate.notNull(theFiles, "The files cannot be null.");
    files = theFiles;
  }
}
