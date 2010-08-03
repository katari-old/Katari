/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain;

/** Adapts a domain object to the needs of the search module.
 *
 * The search module needs a way to render each object found and to filter the
 * possible objects with security constraints.
 *
 * Modules implement this interface and registers it in the search module,
 * appending it to the list named 'search.converters' in spring.
 *
 * TODO Find a more representative name.
 *
 * @author nira.amit@globant.com
 */
public interface SearchAdapter {

  /** Converts an object into a SearchResultElement object.
   *
   * The object passed to convert is the one found by the search module. It is
   * generated based on the information stored in the index. It is not attached
   * to any hibernate session. Also, the object is not guaranteed to be
   * completely initialized, and there is also the chance that the invariats on
   * the object do not hold.
   *
   * @param o - the object. It cannot be null.
   *
   * @param score - the score for this object.
   *
   * @return a SearchResult, never null.
   */
  SearchResultElement convert(Object o, float score);

  /** Obtains the base view url.
   *
   * The base view url is used to check if the user has permission to view the
   * object. The search module will only return objects where the user has
   * access to the view url. The returned url cannot depend on the object being
   * indexed. It must be relative to the context path.
   *
   * @return a url, never null.
   */
  String getViewUrl();

  /** The class of the indexed object.
   *
   * @return the class of the object adapted by this adapter, never null.
   */
  Class<?> getAdaptedClass();
}

