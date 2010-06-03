/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain;

import org.apache.commons.lang.Validate;

import java.util.List;
import java.util.ArrayList;

/** The result of a full text search in the search module.
 *
 * This result contains general data related to the query execution result (for
 * example, the total number of pages that match the query), and all the 'rows'
 * of the result, as a list of SearchResultElement.
 */
public class SearchResult {

  /** The total number of pages that matched the query.
   */
  private int totalPages;

  /** The 'rows' that matched the result.
   *
   * It is never null.
   */
  private List<SearchResultElement> elements;

  /** Constructor, build an empty search result object.
   */
  public SearchResult() {
    this(0, new ArrayList<SearchResultElement>());
  }

  /** Constructor, builds a search result object.
   *
   * @param theTotalPages the total number of pages that matched the result.
   *
   * @param theElements the list of elements that matched the query. It cannot
   * be null.
   */
  public SearchResult(final int theTotalPages, final List<SearchResultElement>
      theElements) {
    Validate.notNull(theElements, "The elements cannot be null.");
    totalPages = theTotalPages;
    elements = theElements;
  }

  /** Returns the number of pages that matched the query.
   *
   * @return the total number of pages.
   */
  public int getTotalPages() {
    return totalPages;
  }

  /** The elements that matched the query corresponding to the selected page.
   *
   * @return the elements, never returns null. If there are no results, it
   * returns an empty list.
   */
  public List<SearchResultElement> getElements() {
    return elements;
  }
}

