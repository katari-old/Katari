/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;

/** Fetches the dependencies for a list of file's paths.
 *
 * The dependencies are returned in a list that contains their paths. The
 * returned list of dependencies doesn't contain repeated dependencies and are
 * ordered accordingly.
 *
 * @author ivan.bedecarats@globant.com
 */
public class DependenciesResolver {

  /**
   * The Dependencies Finder, never null.
   */
  private DependenciesFinder dependenciesFinder;

  /** Makes a new instance for the {@link DependenciesResolver} with the given
   * parameter.
   * @param theDependenciesFinder The {@link DependenciesFinder} used to look
   * for the dependencies. It's never null.
   */
  public DependenciesResolver(
      final DependenciesFinder theDependenciesFinder) {
    Validate.notNull(theDependenciesFinder,
        "The immediate dependencies resolver cannot be null.");
    dependenciesFinder = theDependenciesFinder;
  }

  /** Fetches all the dependencies for a given list of files.
   *
   * @param filesToBeResolved The list of files whose dependencies are going to
   * be fetched.  It's never null.
   *
   * @return A List containing all the dependencies for the given list of files.
   * Never returns null.
   */
  public List<String> resolve(final List<String> filesToBeResolved) {
    Validate.notNull(filesToBeResolved,
        "The filesToBeResolved cannot be null.");
    return resolve(filesToBeResolved, new HashSet<String>());
  }

  /** It looks for the dependencies for a given list of files.
   *
   * It stores the dependency ancestors to see if it finds a circular
   * dependency. If that happens a {@link RuntimeException} is thrown.
   *
   * @param filesToBeResolved The list of files whose dependencies are going to
   * be fetched.  It's never null.
   *
   * @param ancestors A {@link Set} containing the ancestors for a dependency.
   * It's never null.
   *
   * @return A List containing all the dependencies for the given list of
   * files.  Never returns null.
   */
  private List<String> resolve(final List<String> filesToBeResolved,
      final Set<String> ancestors) {
    Validate.notNull(filesToBeResolved,
        "The filesToBeResolved cannot be null.");
    Validate.notNull(ancestors, "The ancestors cannot be null.");
    List<String> fetchedDependencies = new ArrayList<String>();

    for (String file : filesToBeResolved) {
      if (ancestors.contains(file)) {
        throw new RuntimeException("Circular dependency found: " + file
            + " is in the list of its ancestors. Ancestors are " + ancestors);
      } else {
        HashSet<String> newAncestors = new HashSet<String>();
        newAncestors.addAll(ancestors);
        newAncestors.add(file);
        List<String> dependenciesFound = dependenciesFinder.find(file);
        addIgnoreDuplicates(fetchedDependencies,
            resolve(dependenciesFound, newAncestors));
      }
      addIgnoreDuplicate(fetchedDependencies, file);
    }
    return fetchedDependencies;
  }

  /** Adds a list of files to another list of source files. If any of the files
   * already exists in the source list, then it won't be added.
   * @param sourceFiles The list where the new files will be added if they are
   * not duplicated. It's never null.
   * @param filesToBeAdded The list of files to be added. If any of them exists
   * in the source list, then it won't be added. It's never null.
   */
  private void addIgnoreDuplicates(final List<String> sourceFiles,
      final List<String> filesToBeAdded) {
    Validate.notNull(sourceFiles, "The list of source files cannot be null.");
    Validate.notNull(filesToBeAdded,
        "The list of files to be added cannot be null.");
    for (String fileToBeAdded : filesToBeAdded) {
      addIgnoreDuplicate(sourceFiles, fileToBeAdded);
    }
  }

  /** Adds a file to a list of files if that file doesn't already exist in the
   * list.
   * @param sourceFiles The list where the new file will be added if it doesn't
   * already exist in the list. It's never null.
   * @param fileToBeAdded The file to be added in the list. If it already exist
   * in the list, then it won't be added. It's never null.
   */
  private void addIgnoreDuplicate(final List<String> sourceFiles,
      final String fileToBeAdded) {
    Validate.notNull(sourceFiles, "The list of source files cannot be null.");
    Validate.notNull(fileToBeAdded, "The files to be added cannot be null.");
    if (!sourceFiles.contains(fileToBeAdded)) {
      sourceFiles.add(fileToBeAdded);
    }
  }
}
