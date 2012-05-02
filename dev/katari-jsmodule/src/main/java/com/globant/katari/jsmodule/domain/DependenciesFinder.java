/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Looks for the dep file corresponding to the given javascript file path.
 *
 * It then fetches for the dependencies files included in that dep and returns
 * them in a list.
 *
 * @author ivan.bedecarats@globant.com
 */
public class DependenciesFinder {

  /** Fetches the immediate dependencies of the given file.
   *
   * If the .dep.js is empty, i.e., it doesn't have any sort of information, a
   * {@link RuntimeException} will be thrown. The same will happen if the file
   * couldn't be parsed or if a {@link JSONArray} named "js" couldn't be
   * obtained from it.
   *
   * @param fileToBeResolved The path of the file whose dependencies are going
   * to be fetched. Cannot be null nor empty.
   *
   * @return The list of paths of the dependencies of the given file. Never
   * returns null. Returns an empty {@link List} if no dependencies are found or
   * if the given file doesn't exist (because we don't support .dep.js files
   * with empty {@link JSONArray} for .js with no dependencies).
   */
  public List<String> find(final String fileToBeResolved) {
    Validate.notEmpty(fileToBeResolved, "The file cannot be null nor empty");

    List<String> foundDependencies = new ArrayList<String>();

    String file = getPathWithDepJsExtension(fileToBeResolved);
    InputStream fileContent = getClass().getResourceAsStream(file);

    // if the file was found that means it has dependencies files. Otherwise, it
    // hasn't.
    if (fileContent != null) {
      String jsonTxt;
      try {
        jsonTxt = IOUtils.toString(fileContent);
      } catch (IOException e) {
        throw new RuntimeException("Error reading dep file " + file, e);
      } finally {
        IOUtils.closeQuietly(fileContent);
      }

      JSONObject fileDependency;
      try {
        fileDependency = new JSONObject(jsonTxt);
      } catch (JSONException e) {
        throw new RuntimeException("Error parsing dep file " + file, e);
      }
      JSONArray fileDependencyFiles;
      try {
        fileDependencyFiles = fileDependency.getJSONArray("js");
        for (int i = 0; i < fileDependencyFiles.length(); i++) {
          foundDependencies.add(fileDependencyFiles.getString(i));
        }
      } catch (JSONException e) {
        throw new RuntimeException(
            "Error obtaning js dependencies files from dep file " + file, e);
      }
    }

    return foundDependencies;
  }

  /** Returns the file with the .dep.js depency extension.
   * @param file The file path of the original js file. It's never null nor
   * empty. It should be a valid path for a javascript file, i.e., it should end
   * with the extension .js.
   * @return The file path ending with the extension .dep.js. It's never null.
   */
  private String getPathWithDepJsExtension(final String file) {
    Validate.notEmpty(file, "The file cannot be empty null nor empty.");
    Validate.isTrue(file.endsWith(".js"),
        "The file must have a javascript extension.");
    int fileExtensionDot = file.lastIndexOf(".");
    String fileWithoutExtension = file.substring(0, fileExtensionDot);
    return fileWithoutExtension + ".dep.js";
  }
}

